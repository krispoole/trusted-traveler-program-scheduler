package com.krispoole.scheduler.tasks;

import com.krispoole.scheduler.model.Appointment;
import com.krispoole.scheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ScheduledApiCallTask {

    @Autowired
    private WebClient webClient;

    @Autowired
    private EmailService emailService;

    private boolean customerEmailSent = false;
    private boolean shouldSuspendService = false;

    @Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
    public void callApiPeriodically() {
        URI uri = UriComponentsBuilder.fromUriString("https://ttp.cbp.dhs.gov/schedulerapi/slot-availability")
                .queryParam("locationId", "5445")
                .build()
                .toUri();

        webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Appointment.class)
                .subscribe(response -> {
                    System.out.println("API Response: " + response);
                    if (!response.availableSlots().isEmpty() && !customerEmailSent) {
                        emailService.sendSimpleMessage(
                                "krispoole@pm.me",
                                "Philadelphia International Airport Global Entry Appointment Slot Available",
                                "An appointment slot is available at Philadelphia International Airport. Please go to <a href='https://ttp.cbp.dhs.gov'>https://ttp.cbp.dhs.gov</a>."
                        );
                        emailService.sendSimpleMessage(
                                "krispoole@pm.me",
                                "Suspend the API",
                                "Suspend the api running now, email has been sent."
                        );
                        customerEmailSent = true;
                        shouldSuspendService = true;
                    }
                });
    }
}
