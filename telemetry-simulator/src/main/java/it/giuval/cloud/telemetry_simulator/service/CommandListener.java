package it.giuval.cloud.telemetry_simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.giuval.cloud.telemetry_simulator.domain.DroneCommand;
import it.giuval.cloud.telemetry_simulator.events.RecallCommandEvent;

@Component
public class CommandListener {

	private final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	@Autowired
	private FleetRegistry fleetRegistry;

	@RabbitListener(queues = "${telemetry.rabbitmq.queue}")
	public void onCommand(RecallCommandEvent command) {
		logger.info("Received command "+command);	
		
		boolean sent = fleetRegistry.sendCommand(command.droneId(), new DroneCommand.Recall(command.reason()));
	}
}
