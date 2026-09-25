package it.giuval.cloud.telemetry_simulator.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.giuval.cloud.telemetry_simulator.domain.Drone;
import it.giuval.cloud.telemetry_simulator.domain.DroneCommand;
import it.giuval.cloud.telemetry_simulator.domain.DroneCommand.Relocate;
import it.giuval.cloud.telemetry_simulator.service.FleetLoader;
import it.giuval.cloud.telemetry_simulator.service.FleetRegistry;
import it.giuval.cloud.telemetry_simulator.service.FleetSimulator;

@RestController
@RequestMapping("/fleet")
public class FleetController {

	private static final Logger log = LoggerFactory.getLogger(FleetController.class);
	
	@Value("classpath:my-fleet.csv")
    private Resource csvResource;
	
	@Autowired
	private FleetSimulator fleetSimulator;
	@Autowired
	private FleetLoader fleetLoader;
	@Autowired
	private FleetRegistry fleetRegistry;
	
	@PostMapping("/launch")
	public ResponseEntity<List<Drone>> launch() throws IOException {
		List<Drone> fleet = fleetLoader.loadFromCsv(csvResource.getFilePath());
		
		fleetSimulator.launch(fleet);
		
		return ResponseEntity.ok(fleet);
	}
    
	@PostMapping("/{droneId}/recall")
	public ResponseEntity<String> recall(@PathVariable String droneId) {
		
		boolean sent = fleetRegistry.sendCommand(droneId, new DroneCommand.Recall("recall via API"));
		
		return sent ? ResponseEntity.ok().build():ResponseEntity.notFound().build();
	}
	
	@PostMapping("/{droneId}/relocate")
	public ResponseEntity<String> relocate(@PathVariable String droneId, @RequestBody Relocate relocateCommand) {
		
		log.info("Relocated command received: "+relocateCommand);
		
		boolean sent = fleetRegistry.sendCommand(droneId, new DroneCommand.Relocate(relocateCommand.newLat(), relocateCommand.newLon()));
		
		return sent ? ResponseEntity.ok().build():ResponseEntity.notFound().build();
	}

	@PostMapping("/recall-all")
	public ResponseEntity<String> recallAll() {
		
		boolean sent = fleetRegistry.sendCommandToAll(new DroneCommand.Recall("recall via API"));
		
		return sent ? ResponseEntity.ok().build():ResponseEntity.notFound().build();
	}
}