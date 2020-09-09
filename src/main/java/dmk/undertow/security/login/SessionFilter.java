package dmk.undertow.security.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import dmk.undertow.security.login.user.UserInfo;

public class SessionFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	LoginService loginService;

	private static final String tokenHeader = "X-Authorization";
	private Logger logger = LoggerFactory.getLogger(SessionFilter.class);

	public SessionFilter() {
		super(new RequestHeaderRequestMatcher(tokenHeader));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// TODO Auto-generated method stub

		String token = request.getHeader(tokenHeader);

		if (token == null) {
			return null;
		}

		Authentication auth = null;

		try {
			auth = loginService.parseToken(token);

			if (auth != null) {

				UserInfo user = loginService.auth(auth);
				String remoteIpAddr = request.getRemoteAddr();

				// 사용자존재여부, 세션만료여부, IPAddr Check
				if (user != null && user.isAccountNonExpired() && remoteIpAddr.compareTo(user.getIpAddr()) == 0) {
					return getAuthenticationManager().authenticate(auth);
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}

		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.equals(authResult)) {
			SecurityContextHolder.getContext().setAuthentication(authResult);
			if (auth == null)
				logger.info("[SESSION] Session is null");
			else
				logger.info("[SESSION] Session is not equals");
		}

		chain.doFilter(request, response);

	}

}
