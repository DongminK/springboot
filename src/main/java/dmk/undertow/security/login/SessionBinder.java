package dmk.undertow.security.login;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionBinder implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = 3955338893993155433L;

	private Map<String, Object> loggingMap = new java.util.HashMap<>();

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		loggingMap.put(auth.getName(), auth);
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
		if (auth != null)
			loggingMap.remove(auth.getName());
	}

	public Authentication getAuth(String userId) {
		return (Authentication) loggingMap.get(userId);
	}
}
