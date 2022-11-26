package org.testcontainers.bookstore;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderCancelledEventHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelledEventHandlerTest.class);

    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    @BeforeAll
    static void beforeAll() {
        Startables.deepStart(kafka).join();
    }

    @AfterAll
    static void afterAll() {
        // kafka.stop();
    }

    @DynamicPropertySource
    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldHandleOrderCancelledEvent() {
        String orderId = UUID.randomUUID().toString();

        log.info("Cancelling OrderId: {}", orderId);
        kafkaTemplate.send("cancelled-orders", new OrderCancelledEvent(orderId));

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendCancelledNotification(orderId);
        });
    }
}