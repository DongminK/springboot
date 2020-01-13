package dmk.undertow;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
		@PropertySource(value = "file:${home:..}/config/undertow-${spring.profiles.active:local}.properties"),
		@PropertySource(value = "classpath:application.properties") })
@SpringBootApplication
public class UndertowApplication {

	public static void main(String[] args) {

		String home = System.getProperty("home", "..");
		
		SpringApplication app = new SpringApplicationBuilder().sources(UndertowApplication.class)
				.listeners(new ApplicationPidFileWriter(home + File.separator + "/config/application.pid")).build();

		app.run(args);

	}

}
