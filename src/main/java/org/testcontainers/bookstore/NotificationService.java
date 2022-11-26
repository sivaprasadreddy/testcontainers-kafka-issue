package org.testcontainers.bookstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendCancelledNotification(String orderId) {
        log.info("==========================================================");
        log.info("This is to notify you that your order : {} is cancelled.", orderId);
        log.info("==========================================================");
    }


}
