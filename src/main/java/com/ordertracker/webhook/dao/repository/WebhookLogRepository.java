package com.ordertracker.webhook.dao.repository;

import com.ordertracker.webhook.dao.entity.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {

    List<WebhookLog> findByType(String type);

    List<WebhookLog> findByStatus(String status);
}