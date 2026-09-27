package it.giuval.cloud.telemetry_simulator.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import it.giuval.cloud.telemetry_simulator.domain.Drone;
import it.giuval.cloud.telemetry_simulator.domain.DroneType;

@Component
public class FleetLoader {

	public List<Drone> loadFromCsv(InputStream fileInputStream) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
		         Stream<String> lines = reader.lines()) {
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