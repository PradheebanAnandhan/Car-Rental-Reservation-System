package com.carrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    @NotNull(message = "Reservation is required")
    private ReservationEntity reservation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Positive(message = "Total amount must be positive")
    @Column(nullable = false)
    private double totalAmount;

    private String pdfPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
    }
}
