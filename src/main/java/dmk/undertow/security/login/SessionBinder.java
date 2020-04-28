package dmk.undertow.security.login;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionBinder implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = 3955338893993155433L;

	private HashMap<String, String> mapUserId = new HashMap<String, String>();
	private HashMap<String, Authentication> mapAuth = new HashMap<String, Authentication>();
	private Logger logger = LoggerFactory.getLogger(SessionBinder.class);

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		String sessionId = event.getSession().getId();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		setAuth(auth.getName(), sessionId, auth);
		
		logger.info("[SESSION_BINDER] value bound - " + auth.getName() + " / " + sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http
	 * .HttpSessionBindingEvent)
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		mapAuth.remove(event.getSession().getId());

		if (auth != null) {
			mapUserId.remove(auth.getName());
		}

		logger.info("[SESSION_BINDER] value unbound - " + auth.getName() + " / " + event.getSession().getId());
	}

	private synchronized void setAuth(String userId, String token, Authentication auth) {
		mapAuth.put(token, auth);
		mapUserId.put(userId, token);
	}

	public Authentication getAuthByUserId(String userId) {
		String token = mapUserId.get(userId);
		if (token != null)
			return getAuthByToken(token);

		return null;
	}

	public Authentication getAuthByToken(String token) {
		logger.info("[SESSION_BINDER] " + token + " / " + mapAuth);

		return mapAuth.get(token);
	}
}
