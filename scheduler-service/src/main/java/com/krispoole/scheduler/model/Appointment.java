package com.krispoole.scheduler.model;

import java.util.List;

public record Appointment(List<Slot> availableSlots, String lastPublishedDate) {

    public record Slot(
            int locationId,
            String startTimestamp,
            String endTimestamp,
            boolean active,
            int duration,
            boolean remoteInd
    ) {}
}
