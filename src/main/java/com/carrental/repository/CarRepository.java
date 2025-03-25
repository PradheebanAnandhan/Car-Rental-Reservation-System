package com.carrental.repository;

import com.carrental.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {
    List<CarEntity> findByAvailableTrue();

    Optional<CarEntity> findByRegistrationNumber(String registrationNumber);

    List<CarEntity> findByBrandAndModel(String brand, String model);
}
