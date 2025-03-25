package com.carrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Color is required")
    private String color;

    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be positive")
    private BigDecimal dailyRate;

    @NotBlank(message = "Registration number is required")
    @Column(unique = true)
    private String registrationNumber;

    private boolean available = true;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();

    public void addReservation(ReservationEntity reservation) {
        reservations.add(reservation);
        reservation.setCar(this);
    }

    public void removeReservation(ReservationEntity reservation) {
        reservations.remove(reservation);
        reservation.setCar(null);
    }

    public void addReview(ReviewEntity review) {
        reviews.add(review);
        review.setCar(this);
    }

    public void removeReview(ReviewEntity review) {
        reviews.remove(review);
        review.setCar(null);
    }
}
