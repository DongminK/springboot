package dmk.undertow.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@RestController
public class SyncCollectController {

	Logger logger = LoggerFactory.getLogger(SyncCollectController.class);

	@RequestMapping(value = "/collectd", method = RequestMethod.POST)
	public String collectd(@ApiIgnore HttpSession session, @ApiIgnore HttpServletRequest req,
			@ApiIgnore HttpServletResponse res, @RequestBody String loginData) throws Exception {

		String result = "OK";

		try {

			String host = req.getRemoteHost();
			String ipAddr = req.getRemoteAddr();

			logger.info("[collectd][" + host + "][" + ipAddr + "] " + loginData);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = "Failed";
		} finally {
			session.invalidate();
		}

		return result;

	}

	@RequestMapping(value = "/telegraf", method = RequestMethod.POST)
	public String telegraf(@ApiIgnore HttpSession session, @ApiIgnore HttpServletRequest req,
			@ApiIgnore HttpServletResponse res, @RequestBody String loginData) throws Exception {

		String result = "OK";

		try {

			String host = req.getRemoteHost();
			String ipAddr = req.getRemoteAddr();

			logger.info("[telegraf][" + host + "][" + ipAddr + "] " + loginData);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = "Failed";
		} finally {
			session.invalidate();
		}

		return result;

	}

	@RequestMapping(value = "/scouter", method = RequestMethod.POST)
	public String scouter(@ApiIgnore HttpSession session, @ApiIgnore HttpServletRequest req,
			@ApiIgnore HttpServletResponse res, @RequestBody String loginData) throws Exception {

		String result = "OK";

		try {

			String host = req.getRemoteHost();
			String ipAddr = req.getRemoteAddr();

			logger.info("[scouter][" + host + "][" + ipAddr + "] " + loginData);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = "Failed";
		} finally {
			session.invalidate();
		}

		return result;

	}
}
