package edu.leicester.co2103.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.ErrorInfo;
import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;

@RestController
public class ModuleRestController {

	@Autowired
	private ModuleRepository modRepo;
	@Autowired
	private SessionRepository seshRepo; //TODO should this be here?
	
	//List all modules (endpoint 7)
	@GetMapping("/modules")
	public ResponseEntity<List<Module>> listModules() {
		
		List<Module> modules = modRepo.findAll();
		if (modules.isEmpty()) {
			return new ResponseEntity<List<Module>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Module>>(modules, HttpStatus.OK);
	}
	
	//Add module (endpoint 8)
	@PostMapping("/modules")
	public ResponseEntity<?> createModule(@RequestBody Module module, UriComponentsBuilder ucBuilder) {

		if (modRepo.existsById(module.getCode())) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("A module with code " + module.getCode() + " already exists."),
					HttpStatus.CONFLICT);
		}
		
		modRepo.save(module);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/modules/{code}").buildAndExpand(module.getCode()).toUri());
		return new ResponseEntity<Module>(module, HttpStatus.CREATED);
	}
	
	//Get module (endpoint 9)
	@GetMapping("/modules/{code}")
	public ResponseEntity<?> getModule(@PathVariable(value = "code") String code) {
		
		if (modRepo.findById(code).isPresent()) {
			Module module = modRepo.findById(code).get();
			return new ResponseEntity<Module>(module, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found"),
					HttpStatus.NOT_FOUND);
	}
	
	//Update module (endpoint 10)
	@PatchMapping("/modules/{code}")
	public ResponseEntity<?> updateModule(@PathVariable("code") String code, @RequestBody Module newModule) {

		if (modRepo.findById(code).isPresent()) {
			Module currentModule = modRepo.findById(code).get();
			currentModule.setTitle(newModule.getTitle());
			currentModule.setLevel(newModule.getLevel());
			currentModule.setOptional(newModule.isOptional());
			
			currentModule.getSessions().clear();
			currentModule.getSessions().addAll(newModule.getSessions());

			modRepo.save(currentModule);
			return new ResponseEntity<Module>(currentModule, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found."),
					HttpStatus.NOT_FOUND);

	}
	
	//Delete module (endpoint 11)
	@DeleteMapping("/modules/{code}")
	public ResponseEntity<?> deleteModule(@PathVariable(value = "code") String code) {

		if (modRepo.findById(code).isPresent()) {
			modRepo.deleteById(code);
			return ResponseEntity.ok(null);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found."),
					HttpStatus.NOT_FOUND);
		
	}
	
	//List module sessions (endpoint 12)
	@RequestMapping("/modules/{code}/sessions")
	public ResponseEntity<?> listModSessions(@PathVariable(value = "code") String code) {
		
		if (modRepo.findById(code).isPresent()) {
			List<Session> sessions = modRepo.findById(code).get().getSessions();
			return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
		} else 
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module with code " + code + " not found."), HttpStatus.NOT_FOUND);
		
	}
	
	//Add session (endpoint 13)
	@PostMapping("/modules/{code}/sessions")
	public ResponseEntity<?> createSession(@RequestBody Session session, @PathVariable(value = "code") String code, UriComponentsBuilder ucBuilder) {

		if (seshRepo.existsById(session.getId())) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("A session with ID " + session.getId() + " already exists."),
					HttpStatus.CONFLICT);
		}

		seshRepo.save(session);
		String message = "Session with ID " + String.valueOf(session.getId()) + " created";
		/*List<String> pathParameters = new ArrayList<>();
		pathParameters.add(code);
		pathParameters.add(String.valueOf(session.getId()));

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/modules/{code}/sessions/{id}").buildAndExpand(pathParameters).toUri()); 
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);*/
		return new ResponseEntity<Session>(session, HttpStatus.CREATED);
	}
	
	//Get session (endpoint 14)
	@GetMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> getSession(@PathVariable(value = "id") long id) {
		
		if (seshRepo.findById(id).isPresent()) {
			Session session = seshRepo.findById(id).get();
			return new ResponseEntity<Session>(session, HttpStatus.OK);
		} else {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session with ID " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}
	}	
	
	//Update session (endpoint 15)
	@PutMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> putSession(@PathVariable("id") Long id, @RequestBody Session newSession) {

		if (seshRepo.findById(id).isPresent()) {
			Session currentSession = seshRepo.findById(id).get();
			currentSession.setTopic(newSession.getTopic());
			currentSession.setDatetime(newSession.getDatetime());
			currentSession.setDuration(newSession.getDuration());

			seshRepo.save(currentSession);
			return new ResponseEntity<Session>(currentSession, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session with ID " + id + " not found."),
					HttpStatus.NOT_FOUND);

	}
	
	//Update session (endpoint 16)
	@PatchMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> patchSession(@PathVariable("id") Long id, @RequestBody Session newSession) {

		if (seshRepo.findById(id).isPresent()) {
			Session currentSession = seshRepo.findById(id).get();
			if (newSession.getTopic() != "") {
				currentSession.setTopic(newSession.getTopic());
			} if (newSession.getDatetime() != null) {
				currentSession.setDatetime(newSession.getDatetime());				
			} if (newSession.getDuration() != 0) {
				currentSession.setDuration(newSession.getDuration());
			}
			seshRepo.save(currentSession);
			return new ResponseEntity<Session>(currentSession, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session with ID " + id + " not found."),
					HttpStatus.NOT_FOUND);

	}
	
	//Delete session (endpoint 17)
	@DeleteMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> deleteSession(@PathVariable(value = "id") long id) {

		if (seshRepo.findById(id).isPresent()) {
			seshRepo.deleteById(id);
			return ResponseEntity.ok(null);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session with ID " + id + " not found."),
					HttpStatus.NOT_FOUND);
		
	}
	
}
