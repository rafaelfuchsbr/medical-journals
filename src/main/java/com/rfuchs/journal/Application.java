package com.rfuchs.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.csrf.CsrfFilter;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.io.File;

@SpringBootApplication
public class Application {

	private static String applicationRootDirectory;

	public static void main(String[] args) throws Exception {
		File directory = new File(".");
		applicationRootDirectory = directory.getCanonicalPath();
		SpringApplication.run(Application.class, args);
	}

	public static String getApplicationRootDirectory() {
		return applicationRootDirectory + File.separator;
	}

//	@Configuration
//	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http
//					.httpBasic().and()
//					.authorizeRequests()
//					.antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll()
//					.antMatchers("/about.html", "/contact.html", "/signup.html", "/img/**", "/topics", "/journals").permitAll()
//					.antMatchers("/topicsList.html", "/signupconfirmation.html", "/journalList.html","/login/**").permitAll()
//					.antMatchers("/users/login","/javafx/**").permitAll()
//					.anyRequest().authenticated().and()
//					.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
//					.csrf().csrfTokenRepository(csrfTokenRepository())
//					.and()
//					.formLogin().loginPage("/login/index.html")
//					.and().logout();
//
//		}
//	}
//
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//				.inMemoryAuthentication()
//				.withUser("user2@fuchs.io").password("aaa").roles("USER");
//	}
//
//	private static CsrfTokenRepository csrfTokenRepository() {
//		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//		repository.setHeaderName("X-XSRF-TOKEN");
//		return repository;
//	}

}
