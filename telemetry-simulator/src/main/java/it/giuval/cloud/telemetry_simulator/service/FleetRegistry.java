package it.giuval.cloud.telemetry_simulator.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import it.giuval.cloud.telemetry_simulator.domain.DroneCommand;
import it.giuval.cloud.telemetry_simulator.domain.DroneCommand.Recall;

@Component
public class FleetRegistry {

	private final ConcurrentHashMap<String, BlockingQueue<DroneCommand>> activeDrones = new ConcurrentHashMap<>();

	public BlockingQueue<DroneCommand> register(String droneId){
		var queue = new LinkedBlockingQueue<DroneCommand>();
		activeDrones.put(droneId, queue);
		return queue;
	}

	public void unregister(String droneId) {
		activeDrones.remove(droneId);
	}

	public boolean sendCommand(String droneId, DroneCommand command) {
		BlockingQueue<DroneCommand> commandsQueue = activeDrones.get(droneId);
		if(commandsQueue == null) {
			return false;
		}
		return commandsQueue.offer(command);
	}

	public boolean sendCommandToAll(DroneCommand command) {
		if (activeDrones.isEmpty()) {
			return false;
		}
		activeDrones.forEach((droneId, queue) -> {
			queue.offer(command);
		});
		return true;
	}

}