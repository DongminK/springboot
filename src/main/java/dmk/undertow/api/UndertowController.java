package dmk.undertow.api;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dmk.undertow.UndertowApplication;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class UndertowController {

	Logger logger = LoggerFactory.getLogger(UndertowApplication.class);

	@Value("${server.port}")
	private long port;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String simple_text(@ApiIgnore HttpSession session) throws Exception {
		String sessionId = session.getId();
		logger.info("TEST MESSAGE - " + sessionId);
		return "Hello undertow world:" + port + " - " + sessionId;
	}

}
