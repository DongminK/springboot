package dmk.undertow.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dmk.undertow.UndertowApplication;
import dmk.undertow.security.login.LoginService;
import dmk.undertow.security.login.SessionBinder;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class SessionController {

	Logger logger = LoggerFactory.getLogger(UndertowApplication.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	SessionBinder sessionBinder;

	@Autowired
	LoginService loginService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ApiIgnore HttpSession session, @ApiIgnore HttpServletRequest req,
			@ApiIgnore HttpServletResponse res, @RequestBody String loginData) {

		String result = "Login";

		try {
			JSONObject jObj = new JSONObject(loginData);
			String userId = jObj.getString("user_id");
			String password = jObj.getString("password");

			Authentication auth = sessionBinder.getAuthByUserId(userId);

			if (auth == null) {
				logger.info("[LOGIN] Auth is null");
				User user = (User) loginService.loadUserByUsername(userId);

				if (loginService.checkpassword(user, password)) {

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							user.getUsername(), user.getPassword(), user.getAuthorities());

					if (!authToken.isAuthenticated())
						authToken.setAuthenticated(true);

					authToken.setDetails(user);
					auth = authenticationManager.authenticate(authToken);

				} else {
					result = "Password incorrect";
					throw new Exception();
				}

			} else {
				logger.info("[LOGIN] Auth is exist");
				result = "Login exist";
			}

			// 사용자 인증 정보를 HTTP 세션에 넣어준다.
			String token = loginService.generateToken(auth);
			SecurityContextHolder.getContext().setAuthentication(auth);
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					SecurityContextHolder.getContext());
			session.setAttribute("session_binder", sessionBinder);
			session.setAttribute("X-Authorization", token);

			result = session.getId() + " / " + token;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				session.invalidate();
			} catch (IllegalStateException ex) {
			}

			if (result.equals("Login"))
				result = "Failed";
		}

		return result;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(@ApiIgnore HttpSession session) throws Exception {

		String result = "Logout";

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth instanceof UsernamePasswordAuthenticationToken)
				((UsernamePasswordAuthenticationToken) auth).eraseCredentials();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = "Failed";
		} finally {
			session.invalidate();
		}

		return result;

	}

}
