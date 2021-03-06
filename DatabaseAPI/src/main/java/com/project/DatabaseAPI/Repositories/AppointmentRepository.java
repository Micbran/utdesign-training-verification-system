package com.project.DatabaseAPI.Repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.project.DatabaseAPI.Entities.*;

@EnableJpaRepositories
@EntityScan(basePackages = "com.project.DatabaseAPI.Entities.Appointment")
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

}