package it.giuval.cloud.telemetry_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelemetrySimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemetrySimulatorApplication.class, args);
	}

}
