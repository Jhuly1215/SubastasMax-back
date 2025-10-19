package Subastasmax.notificationservice.integration;

import Subastasmax.notificationservice.dto.EventPayloadDTO;
import Subastasmax.notificationservice.service.SettlementEventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Escucha topics relevantes y delega a SettlementEventHandler.
 * Los topics pueden configurarse en application.yml
 */
@Component
public class KafkaEventListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

    private final SettlementEventHandler settlementEventHandler;

    public KafkaEventListener(SettlementEventHandler settlementEventHandler) {
        this.settlementEventHandler = settlementEventHandler;
    }

    @KafkaListener(topics = "${kafka.topic.auction:auction.events}", containerFactory = "kafkaListenerContainerFactory")
    public void listenAuctionEvents(ConsumerRecord<String, EventPayloadDTO> record, Acknowledgment ack) {
        try {
            EventPayloadDTO payload = record.value();
            log.info("Kafka auction event received: key={} topic={} offset={}", record.key(), record.topic(), record.offset());
            settlementEventHandler.handleEvent(payload);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing auction event", e);
            // Depending on requirements, avoid ack to reprocess, or ack to skip poisoned message.
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "${kafka.topic.wallet:wallet.events}", containerFactory = "kafkaListenerContainerFactory")
    public void listenWalletEvents(ConsumerRecord<String, EventPayloadDTO> record, Acknowledgment ack) {
        try {
            EventPayloadDTO payload = record.value();
            log.info("Kafka wallet event received: key={} topic={} offset={}", record.key(), record.topic(), record.offset());
            settlementEventHandler.handleEvent(payload);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing wallet event", e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "${kafka.topic.bid:bid.events}", containerFactory = "kafkaListenerContainerFactory")
    public void listenBidEvents(ConsumerRecord<String, EventPayloadDTO> record, Acknowledgment ack) {
        try {
            EventPayloadDTO payload = record.value();
            log.info("Kafka bid event received: key={} topic={} offset={}", record.key(), record.topic(), record.offset());
            settlementEventHandler.handleEvent(payload);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing bid event", e);
            ack.acknowledge();
        }
    }
}