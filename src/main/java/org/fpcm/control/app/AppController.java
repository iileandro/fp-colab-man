package org.fpcm.control.app;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping(path= "app")
public class AppController {

	@GetMapping()
	public void execute(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		var resource = ResourceUtils.getURL("classpath:static/app/index.html");
		IOUtils.copy(resource.openStream(), response.getOutputStream());
	}
}