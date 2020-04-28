package dmk.undertow.security.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(LoginService.class);
	final static String signkey = Base64.getEncoder().encodeToString("undertow".getBytes());

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub

		String password = "insoft123";

		List<GrantedAuthority> authorities = new ArrayList<>();

		if (("admin").equals(username)) {
			authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
			password = "InSoft!23";
		} else {
			authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
		}

		return new User(username, password, authorities);
	}

	public boolean checkpassword(UserDetails userDetails, String userPw) throws Exception {

		if (userDetails.getPassword().equals(userPw))
			return true;

		return false;
	}

	public User auth(Authentication auth) {
		if (auth == null || !auth.isAuthenticated() || auth.getAuthorities().isEmpty()) {
			return null;
		}

		User user = null;
		try {
			Object detail = auth.getDetails();
			if (detail instanceof User) {
				user = (User) detail;
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return user;
	}

	public String generateToken(Authentication auth) throws IOException, NoSuchAlgorithmException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(auth);
		} finally {
			if (oos != null)
				oos.close();
		}

		String encToken = Base64.getEncoder().encodeToString(baos.toByteArray());

		Jwt jwt = JwtHelper.encode(encToken, new MacSigner(signkey));
		String token = jwt.getEncoded();

		return token;
	}

	/**
	 * @param authToken
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Authentication parseToken(String authToken) throws IOException, ClassNotFoundException {
		Authentication auth = null;
		Jwt jwt = JwtHelper.decodeAndVerify(authToken, new MacSigner(signkey));
		String claim = jwt.getClaims();

		byte[] decToken = Base64.getDecoder().decode(claim);
		ByteArrayInputStream bais = new ByteArrayInputStream(decToken);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			auth = (Authentication) ois.readObject();
		} finally {
			if (ois != null)
				ois.close();
		}

		return auth;
	}

}
