package it.giuval.cloud.telemetry_simulator.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import it.giuval.cloud.telemetry_simulator.domain.Drone;
import it.giuval.cloud.telemetry_simulator.domain.DroneType;

@Component
public class FleetLoader {

	public List<Drone> loadFromCsv(Path path) throws IOException {

		try(var lines = Files.lines(path)){
			return lines
					.skip(1)//header
					.map(this::parseLine)
					.toList();
		}
	}

	private Drone parseLine(String line) {
		var parts = line.split(",");
		
		return new Drone(
				parts[0],
				DroneType.valueOf(parts[1]),
				Double.parseDouble(parts[2]),
				Double.parseDouble(parts[3])
				);
	}

}