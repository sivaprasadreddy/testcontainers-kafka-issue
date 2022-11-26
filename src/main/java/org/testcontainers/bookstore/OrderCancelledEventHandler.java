package org.testcontainers.bookstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelledEventHandler.class);

    private final NotificationService notificationService;

    public OrderCancelledEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "cancelled-orders")
    public void handle(OrderCancelledEvent event) {
        log.info("Received a OrderCancelledEvent with orderId:{}: ", event.getOrderId());
        notificationService.sendCancelledNotification(event.getOrderId());
    }
}
