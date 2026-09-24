package it.giuval.cloud.telemetry_simulator.events;

public record RecallCommandEvent(
		String droneId,
		String reason
	) {

}