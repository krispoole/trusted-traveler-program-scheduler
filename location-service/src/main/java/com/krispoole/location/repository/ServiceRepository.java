package com.krispoole.location.repository;

import com.krispoole.location.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceType, Integer> {
}
