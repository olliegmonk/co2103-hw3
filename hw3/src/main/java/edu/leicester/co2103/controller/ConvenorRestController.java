package edu.leicester.co2103.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.ErrorInfo;

@RestController
public class ConvenorRestController {

	@Autowired
	private ConvenorRepository conRepo;
	
	//List all convenors (endpoint 1)
	@GetMapping("/convenors")
	public ResponseEntity<List<Convenor>> listConvenors() {
		
		List<Convenor> convenors = conRepo.findAll();
		if (convenors.isEmpty()) {
			return new ResponseEntity<List<Convenor>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Convenor>>(convenors, HttpStatus.OK);
	}
	
	//Add convenor (endpoint 2)
	@PostMapping("/convenors")
	public ResponseEntity<?> createConvenor(@RequestBody Convenor convenor, UriComponentsBuilder ucBuilder) {

		if (conRepo.existsById(convenor.getId())) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("A convenor named " + convenor.getName() + " already exists."),
					HttpStatus.CONFLICT);
		}
		
		conRepo.save(convenor);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/convenors/{id}").buildAndExpand(convenor.getId()).toUri());
		return new ResponseEntity<Convenor>(convenor, HttpStatus.CREATED);
	}
	
	//Get convenor (endpoint 3)
	@GetMapping("/convenors/{id}")
	public ResponseEntity<?> getConvenor(@PathVariable(value = "id") long id) {
		
		if (conRepo.findById(id).isPresent()) {
			Convenor convenor = conRepo.findById(id).get();
			return new ResponseEntity<Convenor>(convenor, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
	}
	
	//Update convenor (endpoint 4)
	@PutMapping("/convenors/{id}")
	public ResponseEntity<?> updateConvenor(@PathVariable("id") long id, @RequestBody Convenor newConvenor) {

		if (conRepo.findById(id).isPresent()) {
			Convenor currentConvenor = conRepo.findById(id).get();
			currentConvenor.setName(newConvenor.getName());
			currentConvenor.setPosition(newConvenor.getPosition());
			
			currentConvenor.getModules().clear();
			currentConvenor.getModules().addAll(newConvenor.getModules());

			conRepo.save(currentConvenor);
			return new ResponseEntity<Convenor>(currentConvenor, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with id " + id + " not found."),
					HttpStatus.NOT_FOUND);

	}
	
	//Delete convenor (endpoint 5)
	@DeleteMapping("/convenors/{id}")
	public ResponseEntity<?> deleteConvenor(@PathVariable(value = "id") long id) {

		if (conRepo.findById(id).isPresent()) {
			conRepo.deleteById(id);
			return ResponseEntity.ok(null);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		
	}
	
	//List convenor modules (endpoint 6)
	@RequestMapping("/convenors/{id}/modules")
	public ResponseEntity<?> listConModules(@PathVariable(value = "id") long id) {
		
		if (conRepo.findById(id).isPresent()) {
			List<Module> modules = conRepo.findById(id).get().getModules();
			return new ResponseEntity<List<Module>>(modules, HttpStatus.OK);
		} else 
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with id " + id + " not found."), HttpStatus.NOT_FOUND);
		
	}
}
