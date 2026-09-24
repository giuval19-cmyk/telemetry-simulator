package it.giuval.cloud.telemetry_simulator.events;

import java.time.Instant;

import it.giuval.cloud.telemetry_simulator.domain.DroneStatus;
import it.giuval.cloud.telemetry_simulator.domain.Position;

public record TelemetryEvent(
		String droneId,
		Position position,
		int batteryLevel,
		DroneStatus status,
		Instant timestamp
		) {

}