package com.carrental.repository;

import com.carrental.entity.CarEntity;
import com.carrental.entity.ReservationEntity;
import com.carrental.entity.ReservationStatus;
import com.carrental.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

        List<ReservationEntity> findByUser(UserEntity user);

        Optional<ReservationEntity> findByIdAndUser(Long id, UserEntity user);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservationEntity r " +
                        "WHERE r.car = :car AND r.status != 'CANCELLED' AND " +
                        "((r.startDate BETWEEN :startDate AND :endDate) OR " +
                        "(r.endDate BETWEEN :startDate AND :endDate) OR " +
                        "(:startDate BETWEEN r.startDate AND r.endDate))")
        boolean existsByCarAndDateRange(
                        @Param("car") CarEntity car,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        List<ReservationEntity> findByUserId(Long userId);

        List<ReservationEntity> findByCarId(Long carId);

        List<ReservationEntity> findByStatus(ReservationStatus status);

        List<ReservationEntity> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
}