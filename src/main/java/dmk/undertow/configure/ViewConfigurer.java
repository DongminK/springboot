package dmk.undertow.configure;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViewConfigurer implements WebMvcConfigurer {

	Logger logger = LoggerFactory.getLogger(ViewConfigurer.class);

	@Value("${view.path}")
	private String viewPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		logger.info("[system.info]" + System.getProperties());

		if (!viewPath.endsWith("/"))
			viewPath += "/";

		String delim = "";
		String os = System.getProperty("os.name", "linux");

		if (os.toLowerCase().contains("win"))
			delim += "///";

		File fView = new File(viewPath);

		for (File fDir : fView.listFiles()) {
			if (fDir.isDirectory()) {
				String name = fDir.getName();
				registry.addResourceHandler("/" + name + "/**").addResourceLocations("file:" + delim + viewPath + name + "/")
						.setCachePeriod(20);
			}
		}

	}
	

}
