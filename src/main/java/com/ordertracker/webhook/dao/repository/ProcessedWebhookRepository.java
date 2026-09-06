package com.ordertracker.webhook.dao.repository;

import com.ordertracker.webhook.dao.entity.ProcessedWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedWebhookRepository extends JpaRepository<ProcessedWebhook, Long> {

    boolean existsByEventId(String eventId);

    Optional<ProcessedWebhook> findByEventId(String eventId);
}