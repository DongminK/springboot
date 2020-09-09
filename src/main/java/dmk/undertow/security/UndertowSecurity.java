package dmk.undertow.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import dmk.undertow.security.login.LoginService;
import dmk.undertow.security.login.SessionFilter;

@Configuration
@EnableWebSecurity
public class UndertowSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	LoginService loginService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().sessionManagement().maximumSessions(-1).maxSessionsPreventsLogin(false).and()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER).and().authorizeRequests()
				.antMatchers("/login", "/swagger*/**", "/webjars*/**", "/v2/api-docs", "/collectd", "/telegraf", "/scouter").permitAll().anyRequest()
				.authenticated().and().addFilterAfter(sessionFilter(), BasicAuthenticationFilter.class)
				.addFilterAfter(new CORSFilter(), SessionFilter.class);

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(loginService).passwordEncoder(passwordEncoder());
		auth.authenticationProvider(new AuthenticationProvider() {
			@Override
			public Authentication authenticate(Authentication auth) throws AuthenticationException {
				if (auth == null || !auth.isAuthenticated()) {
					return null;
				}

				return auth;
			}

			@Override
			public boolean supports(Class<?> authentication) {
				return true;
			}
		});
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	SessionFilter sessionFilter() throws Exception {
		SessionFilter sessionFilter = new SessionFilter();
		sessionFilter.setAuthenticationManager(authenticationManagerBean());

		return sessionFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
