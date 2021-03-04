package edu.leicester.co2103.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.leicester.co2103.ErrorInfo;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;

//TODO Streamline code: redundant validation methods
@RestController
public class SessionRestController {

	@Autowired
	private ConvenorRepository conRepo;
	@Autowired
	private ModuleRepository modRepo;
	@Autowired
	private SessionRepository seshRepo;
	
	//Delete session (endpoint 18)
	@DeleteMapping("/sessions")
	public ResponseEntity<?> deleteSessions() {

		if (seshRepo.count() == 0) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("No sessions found."),
					HttpStatus.NOT_FOUND); 
		} else {
			seshRepo.deleteAll();
			return ResponseEntity.ok(null);
		}


		
	}
	
	//Search sessions
	@RequestMapping("/sessions")
	public ResponseEntity<?> searchSession(@RequestParam(name = "convenor", required = false) Long id, @RequestParam(name = "module", required = false) String code) {
		
		//No search parameters given
		if (id == null && code == null) {
			List<Session> sessions = seshRepo.findAll();
			return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
		}
		//Module code given
		if (id == null && code != null) {
			if (modRepo.existsById(code) == true) {
				List<Session> sessions = modRepo.findById(code).get().getSessions();
				return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
			} else {
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
			}
		}
		//Convenor ID given
		if (id != null && code == null) {
			if(conRepo.existsById(id) == true) {
				List<Session> sessions = new ArrayList<>();
				List<Module> modules = conRepo.findById(id).get().getModules();
				for (int i = 0; i < modules.size(); i++) { 
					sessions.addAll(modules.get(i).getSessions());
				}
				return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
			} else {
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with ID " + id + " not found"), HttpStatus.NOT_FOUND);
			}
		}
		//Both search parameters given
		if (modRepo.existsById(code) == false) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
		}
		if(conRepo.existsById(id) == false) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with ID " + id + " not found"), HttpStatus.NOT_FOUND);
		}
		List<Session> sessions = modRepo.findById(code).get().getSessions();
		List<Module> modules = conRepo.findById(id).get().getModules();
		for (int i = 0; i < modules.size(); i++) { 
			sessions.addAll(modules.get(i).getSessions());
		}
		return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
	}
}
