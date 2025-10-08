package com.cre.hrms.messaging.publisher

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(EventPublisher::class.java)

    fun publish(topic: String, key: String, event: Any) {
        logger.info("Publishing event to topic: $topic with key: $key")
        kafkaTemplate.send(topic, key, event)
            .whenComplete { result, ex ->
                if (ex == null) {
                    logger.info("Event published successfully to topic: $topic, partition: ${result?.recordMetadata?.partition()}, offset: ${result?.recordMetadata?.offset()}")
                } else {
                    logger.error("Failed to publish event to topic: $topic", ex)
                }
            }
    }
}
