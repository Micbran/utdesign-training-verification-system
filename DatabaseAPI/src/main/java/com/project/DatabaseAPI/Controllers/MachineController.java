package com.project.DatabaseAPI.Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.project.DatabaseAPI.Repositories.MachineRepository;
import com.project.DatabaseAPI.Services.MachineService;
import com.project.DatabaseAPI.Entities.*;

@RestController
@RequestMapping(path = "/api")
public class MachineController {

  @Autowired
  private MachineRepository machineRepository; // do we need this in the controller?


  @Autowired
  private MachineService machineService;

  @GetMapping(path="/machines")
  public List<Machine> listMachines() {
	  return machineService.getAllMachines();
  }

  @GetMapping("/machines/{id}")
  public ResponseEntity<Machine> getMachine(@PathVariable int id) {
	  try {
		  Machine machine = machineService.getMachine(id);
		  return new ResponseEntity<Machine>(machine, HttpStatus.OK);
	  }
	  catch(NoSuchElementException e) {
		  return new ResponseEntity<Machine>(HttpStatus.NOT_FOUND);
	  }
  }

  @PostMapping("/machines")
  public int addMachine(@RequestBody Machine machine) {
	  machineService.addMachine(machine);
    return machine.getMachinePk();
  }

  @PutMapping("/machines/{id}")
  public ResponseEntity<?> updateMachine(@RequestBody Machine machine,
		  					@PathVariable Integer id) {
	  try {
		  machineService.updateMachine(id, machine);
		  return new ResponseEntity<>(HttpStatus.OK);
	  }
	  catch (NoSuchElementException e) {
		  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  }
  }

  @DeleteMapping("/machines/{id}")
  public void deleteMachine(@PathVariable Integer id) {
      machineService.deleteMachine(id);
  }
  
  @PostMapping("/machines/uploadMachineCSV")
  public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
	  boolean conflict = false;
	  try {
		  
		  BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
		  CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
		  // parse the csv and add each student
		  for (CSVRecord csvRecord : csvParser) {
			  
			  Machine machine = new Machine(
					  csvRecord.get("Machine Tag"),
					  csvRecord.get("Machine Name")
					  );
			  
			  try {
				  machineService.addMachine(machine);
			  }
			  catch(Exception e) {
				  conflict = true;
				  continue;
			  }
				  
		  }
		  csvParser.close();
		  if (conflict == true) {
			  return new ResponseEntity<>(HttpStatus.CONFLICT);
		  }
		  else {
			  return new ResponseEntity<>(HttpStatus.OK);
		  }
	  }
	  catch (Exception e) {
		  throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to upload file", e);
	  }
  }
}
