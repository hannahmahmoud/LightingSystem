package com.lightingsystem.lightingsystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lightingsystem.lightingsystem.Model.SensorReading;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long>
{
    Optional<SensorReading> findTopByLocationAndLightTurnedOffAtIsNullOrderByLightTurnedOnAtDesc(String location); // Find the latest sensor reading at this location where the light has been turned ON but has NOT been turned OFF yet.
}
