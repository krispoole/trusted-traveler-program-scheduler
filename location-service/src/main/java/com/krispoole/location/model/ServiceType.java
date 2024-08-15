package com.krispoole.location.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "services")
public class ServiceType {

    @Id
    private int id;
    private String name;

}
