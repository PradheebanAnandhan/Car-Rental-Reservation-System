package com.carrental.service;

import com.carrental.entity.CarEntity;
import com.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarEntity createCar(CarEntity car) {
        return carRepository.save(car);
    }

    public List<CarEntity> getAllCars() {
        return carRepository.findAll();
    }

    public CarEntity getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}
