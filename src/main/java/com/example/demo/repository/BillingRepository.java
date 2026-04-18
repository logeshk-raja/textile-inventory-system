package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Billing;

public interface BillingRepository extends JpaRepository<Billing, Long> {
}