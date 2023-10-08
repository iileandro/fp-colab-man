package org.fpcm.control.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path= "api/v1/collaborator")
public class CollaboratorController {

	@GetMapping
	public  String[] list() {
		return new String[]{"Item 1", "Item 2", "Item 3"};
	}
}
