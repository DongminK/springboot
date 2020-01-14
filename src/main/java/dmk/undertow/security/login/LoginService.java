package dmk.undertow.security.login;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

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

}
