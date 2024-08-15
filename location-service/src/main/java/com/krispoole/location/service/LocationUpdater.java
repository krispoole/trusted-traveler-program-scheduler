package com.krispoole.location.service;

import com.krispoole.location.model.Location;
import com.krispoole.location.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;

@Service
public class LocationUpdater {

    private static final Logger logger = LoggerFactory.getLogger(LocationUpdater.class);

    @Value("${TRUSTED_TRAVELER_API}")
    private String trustedTravelerApi;

    @Autowired
    private LocationRepository locationRepository;

    private final WebClient webClient;

    @Autowired
    public LocationUpdater(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Transactional
    public void updateLocations() {
        logger.info("Starting updateLocations...");
        logger.info("Fetching Locations from {}", trustedTravelerApi);

        List<Location> fetchedLocations = fetchLocations();

        logger.info("Fetched {} Locations", fetchedLocations.size());
        logger.info("Filtering Locations...");

        List<Location> filteredLocations = fetchedLocations.stream()
                .filter(location -> location.getServiceTypes().stream()
                        .anyMatch(service -> service.getName().equals("Global Entry")))
                .filter(location -> !location.getInviteOnly())
                .filter(Location::getOperational)
                .toList();

        logger.info("Filtered down to {} to include only Operational locations with 'Global Entry'", filteredLocations.size());

        List<Location> dbLocations = locationRepository.findAll();

        logger.info("Saving Locations...");
        int locationsSavedCounter = 0;

        for (Location location : filteredLocations) {
            if (!dbLocations.contains(location)) {
                locationRepository.save(location);
                locationsSavedCounter++;
            }
        }
        logger.info("Locations Saved: {}", locationsSavedCounter);

        logger.info("Deleting Locations...");
        int locationsDeletedCounter = 0;
        for (Location dbLocation : dbLocations) {
            if (!filteredLocations.contains(dbLocation)) {
                locationRepository.delete(dbLocation);
            }
        }
        logger.info("Locations Deleted: {}", locationsDeletedCounter);

        logger.info("Finished updateLocations");
    }

    public List<Location> fetchLocations() {
        try {
            return webClient.get()
                    .uri(trustedTravelerApi)
                    .retrieve()
                    .onStatus(
                            httpStatus -> !httpStatus.is2xxSuccessful(),
                            clientResponse -> {
                                logger.error(clientResponse.statusCode().toString(), Level.WARNING);
                                return clientResponse.createException().flatMap(Mono::error);
                            })
                    .bodyToFlux(Location.class)
                    .collectList()
                    .onErrorResume(WebClientResponseException.class, e -> {
                        logger.error((Marker) Level.SEVERE, e.getMessage(), e);
                        return Mono.error(new CustomException("WebClient error", e));
                    })
                    .onErrorResume(Exception.class, e -> {
                        logger.error((Marker) Level.SEVERE, e.getMessage(), e);
                        return Mono.error(new CustomException("Unexpected error", e));
                    })
                    .block();
        } catch (Exception e) {
            logger.error((Marker) Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public static class CustomException extends RuntimeException {
        public CustomException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}