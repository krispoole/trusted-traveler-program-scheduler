package com.krispoole.location.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {
    @Id
    private Long id;
    private String name;
    private String shortName;
    private String locationType;
    private String locationCode;
    private String address;
    private String addressAdditional;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private String tzData;
    private String phoneNumber;
    private String phoneAreaCode;
    private String phoneCountryCode;
    private String phoneExtension;
    private String phoneAltNumber;
    private String phoneAltAreaCode;
    private String phoneAltCountryCode;
    private String phoneAltExtension;
    private String faxNumber;
    private String faxAreaCode;
    private String faxCountryCode;
    private String faxExtension;
    private LocalDateTime effectiveDate;
    private Boolean temporary;
    private Boolean inviteOnly;
    private Boolean operational;
    @Column(columnDefinition = "TEXT")
    private String directions;
    @Column(columnDefinition = "TEXT")
    private String notes;
    private String mapFileName;
    private Boolean remoteInd;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "location_services",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceType> serviceTypes;

}