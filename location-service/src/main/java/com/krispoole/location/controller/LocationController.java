package com.krispoole.location.controller;

import com.krispoole.location.model.Location;
import com.krispoole.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }

    // TODO: These will not be needed because it will be handled internally
//    @PostMapping
//    public Location createLocation(@RequestBody Location location) {
//        return locationService.saveLocation(location);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteLocation(@PathVariable Long id) {
//        locationService.deleteLocation(id);
//    }
}
