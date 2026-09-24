package it.giuval.cloud.telemetry_simulator.domain;

public record Drone(
		String id,
		DroneType type,
		double baseLat,
		double baseLon
		) {

}