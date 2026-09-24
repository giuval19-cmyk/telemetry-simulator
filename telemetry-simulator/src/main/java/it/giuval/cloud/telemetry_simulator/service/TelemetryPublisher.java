package it.giuval.cloud.telemetry_simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.giuval.cloud.telemetry_simulator.events.TelemetryEvent;

@Component
public class TelemetryPublisher {

	private final RabbitTemplate rabbitTemplate;
	private final Logger log = LoggerFactory.getLogger(TelemetryPublisher.class);
	
	@Value("${telemetry.rabbitmq.exchange}")
	private String EXCHANGE;

	@Value("${telemetry.rabbitmq.routing-key}")
	private String ROUTING_KEY;
	

	public TelemetryPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publish(TelemetryEvent event) {
		log.info("Publishing to "+EXCHANGE);
		
		rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY,event);
		
		log.info("Published event "+event);
	}
}