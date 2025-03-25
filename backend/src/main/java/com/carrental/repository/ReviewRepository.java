package com.carrental.repository;

import com.carrental.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByUserId(Long userId);

    List<ReviewEntity> findByCarId(Long carId);

    List<ReviewEntity> findByCarIdOrderByCreatedAtDesc(Long carId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.car.id = :carId")
    Double findAverageRatingByCarId(@Param("carId") Long carId);
}
