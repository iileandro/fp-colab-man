package org.fpcm.control.api.v1;

import lombok.AllArgsConstructor;
import org.fpcm.model.entities.Collaborator;
import org.fpcm.model.services.CollaboratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path= "api/v1/collaborator")
public class CollaboratorController {

	private CollaboratorService service;

	@GetMapping
	public List<Collaborator> showTree() {
		return service.showTree();
	}


	@GetMapping(path = "/{id}")
	public ResponseEntity<Collaborator> get(@PathVariable("id") int id){
		if(id > 0){
			return ResponseEntity.ok(service.get(id));
		}else{
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping
	public ResponseEntity<Collaborator> save(@RequestBody Collaborator collaborator){
		if(collaborator.isValid()){
			return ResponseEntity.ok(service.insert(collaborator));
		}else{
			return ResponseEntity.badRequest().body(collaborator);
		}
	}

	@PutMapping
	public ResponseEntity<Collaborator> update(@RequestBody Collaborator collaborator){
		if(collaborator.isValid() && collaborator.getId() != 0){
			return ResponseEntity.ok(service.update(collaborator));
		}else{
			return ResponseEntity.badRequest().body(collaborator);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> delete(@PathVariable("id") int id){
		if(id > 0){
			return ResponseEntity.ok(service.delete(id));
		}else{
			return ResponseEntity.badRequest().body(false);
		}
	}
}
