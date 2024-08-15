package com.krispoole.location.service;

import com.krispoole.location.model.Location;
import com.krispoole.location.model.ServiceType;
import com.krispoole.location.repository.LocationRepository;
import com.krispoole.location.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    @Transactional
    public Location saveLocation(Location location) {

        List<ServiceType> serviceTypes = new ArrayList<>();

        for (ServiceType serviceType : location.getServiceTypes()) {
            ServiceType existingServiceType = serviceRepository.findById(serviceType.getId()).orElse(null);
            serviceTypes.add(Objects.requireNonNullElseGet(existingServiceType, () -> serviceRepository.save(serviceType)));
        }

        location.setServiceTypes(serviceTypes);
        return locationRepository.save(location);
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
