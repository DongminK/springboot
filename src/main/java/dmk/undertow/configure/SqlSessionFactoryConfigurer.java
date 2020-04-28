package dmk.undertow.configure;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(value = { "insoft.openmanager.web.dao" }, sqlSessionFactoryRef = "sqlSessionFactory")
public class SqlSessionFactoryConfigurer {

	@Value("${db.driver}")
	String driverClassName;

	@Value("${db.url}")
	String jdbcUrl;

	@Value("${db.username}")
	String username;

	@Value("${db.password}")
	String password;

	@Value("${db.pool.min}")
	int minSize;

	@Value("${db.pool.max}")
	int maxSize;

	long idleTimeout = 60000;
	boolean isAutoCommit = true;

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource());
		sqlSessionFactory
				.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml"));
		return (SqlSessionFactory) sqlSessionFactory.getObject();
	}

	@Bean(name = "sqlSessionTransaction")
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@DependsOn(value = { "embeddeddb" })
	@Bean(name = "dataSource")
	public DataSource dataSource() {

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setPoolName("dbpool");
		hikariConfig.setMinimumIdle(minSize);
		hikariConfig.setMaximumPoolSize(maxSize);
		hikariConfig.setIdleTimeout(idleTimeout);
		hikariConfig.setAutoCommit(isAutoCommit);

		hikariConfig.setDriverClassName(driverClassName);
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		hikariConfig.setJdbcUrl(jdbcUrl);

		return new HikariDataSource(hikariConfig);

	}
}
