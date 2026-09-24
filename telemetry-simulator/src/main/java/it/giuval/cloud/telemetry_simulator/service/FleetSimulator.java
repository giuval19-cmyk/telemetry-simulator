package it.giuval.cloud.telemetry_simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.giuval.cloud.telemetry_simulator.domain.Drone;
import it.giuval.cloud.telemetry_simulator.domain.DroneCommand;
import it.giuval.cloud.telemetry_simulator.domain.DroneCommand.Recall;
import it.giuval.cloud.telemetry_simulator.domain.DroneStatus;
import it.giuval.cloud.telemetry_simulator.domain.Position;
import it.giuval.cloud.telemetry_simulator.events.TelemetryEvent;

@Service
public class FleetSimulator {

	private static final Logger log = LoggerFactory.getLogger(FleetSimulator.class);

	private final TelemetryPublisher publisher;
	private final FleetRegistry fleetRegistry;

	private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
	@Value("${telemetry.battery.critical-threshold}")
	private int BATTERY_THRESHOLD;

	public FleetSimulator(TelemetryPublisher publisher, FleetRegistry fleetRegistry) {
		this.publisher = publisher;
		this.fleetRegistry = fleetRegistry;
	}

	public void launch(List<Drone> fleet) {
		fleet.forEach(drone -> executor.submit(() -> simulateDrone(drone)));

		log.info("Flotta di {} droni avviata su virtual thread", fleet.size());
	}

	private void simulateDrone(Drone drone) {
		var commandsQueue = fleetRegistry.register(drone.id());
		var random = ThreadLocalRandom.current();
		int battery = 100;
		double currentLat = drone.baseLat();
		double currentLon = drone.baseLon();

		try {
			while(battery>0) {

				//Attende un commando oppure scade il timeout e ritorna null
				DroneCommand command = commandsQueue.poll(random.nextLong(1000, 5000), TimeUnit.MILLISECONDS);

				switch(command) {
				case DroneCommand.Recall recall -> {
					executeReturnFlight(drone, battery, currentLat, currentLon);
					return; // termina il thread
				}
				case DroneCommand.Relocate relocate -> {
					currentLat = relocate.newLat();
					currentLon = relocate.newLon();
				}
				case null -> {
					currentLat += random.nextDouble(-0.01, 0.01);
					currentLon += random.nextDouble(-0.01, 0.01);
				}
				}

				battery -= random.nextInt(0,3);
				battery = Math.max(battery,  0);

				Position newPosition = new Position(
						currentLat,
						currentLon,
						random.nextDouble(0, 500)
						);

				publisher.publish(buildEvent(drone, battery, newPosition));

			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			fleetRegistry.unregister(drone.id());
		}
	}

	private TelemetryEvent buildEvent(Drone drone, int battery, Position position) {
		DroneStatus status = getStatus (battery);

		return new TelemetryEvent(
				drone.id(),
				position,
				battery,
				status,
				Instant.now()
				);
	}

	private DroneStatus getStatus(int battery) {
		if(battery==0) {
			return new DroneStatus.Offline("battery depleted");
		}
		if(battery<BATTERY_THRESHOLD) {
			return new DroneStatus.LowBattery(battery);
		}
		return new DroneStatus.Active("nominal");
	}
	
	/**
	 * FASE 2: Sub-routine dedicata esclusivamente al rientro graduale alla base.
	 */
	private void executeReturnFlight(Drone drone,int initialBattery, double startLat, double startLon) throws InterruptedException {
		double currentLat = startLat;
		double currentLon = startLon;
		int battery = initialBattery;
		var random = ThreadLocalRandom.current();

		double targetLat = drone.baseLat();
		double targetLon = drone.baseLon();
		double initialDistance = Math.hypot(targetLat - startLat, targetLon - startLon);

		while (battery > 0) {
			double latDiff = targetLat - currentLat;
			double lonDiff = targetLon - currentLon;
			double distance = Math.hypot(latDiff, lonDiff);

			double step = 0.015; // Velocità di avvicinamento

			if (distance <= step) {
				// Arrivato a destinazione
				currentLat = targetLat;
				currentLon = targetLon;
				break;
			}

			// Interpolazione lineare verso la base
			currentLat += (latDiff / distance) * step;
			currentLon += (lonDiff / distance) * step;

			battery = Math.max(0, battery - random.nextInt(0, 3));

			// Calcolo altitudine proporzionale alla distanza residua
			double progressRatio = initialDistance == 0 ? 0 : distance / initialDistance;
			double altitude = Math.max(0, 200 * progressRatio);

			Position position = new Position(currentLat, currentLon, altitude);
			publisher.publish(buildEvent(drone, battery, position));

			// Ritmo costante di aggiornamento al secondo per un'animazione fluida via SSE
			Thread.sleep(1000);
		}

		// Evento finale di atterraggio
		var finalEvent = new TelemetryEvent(
				drone.id(),
				new Position(targetLat, targetLon, 0),
				battery,
				new DroneStatus.Offline("recalled: returned to base"),
				Instant.now()
		);
		publisher.publish(finalEvent);
		log.info("Drone {} atterrato con successo alla base.", drone.id());
	}

}