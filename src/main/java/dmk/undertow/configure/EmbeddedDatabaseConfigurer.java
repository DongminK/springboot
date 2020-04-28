package dmk.undertow.configure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

@Configuration
public class EmbeddedDatabaseConfigurer {

	Logger logger = LoggerFactory.getLogger(EmbeddedDatabaseConfigurer.class);

	private DB db;

	@Value("${embedded.db.use}")
	private boolean isUseEmbeddedDB;

	@Value("${embedded.db.port}")
	private int embeddedDBPort;

	@Value("${embedded.db.user}")
	private String embeddedDBUser;

	@Value("${embedded.db.data.path}")
	private String dataDir;

	@Value("${embedded.db.options}")
	private String options;

	@Bean(name = "embeddeddb")
	public void startDb() throws Exception {

		logger.info("[EMBEDDED_DB][use:" + isUseEmbeddedDB + "][port:" + embeddedDBPort + "]");

		if (isUseEmbeddedDB) {

			String home = System.getProperty("home", "..");
			String databaseDir = home + File.separator + "database";
			String baseDir = databaseDir + File.separator + "mariadb";

			DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder();

			if (dataDir.trim().length() == 0)
				dataDir = databaseDir + File.separator + "datas";

			builder.setBaseDir(baseDir);
			builder.setDataDir(dataDir);
			builder.setPort(embeddedDBPort);
			builder.setDeletingTemporaryBaseAndDataDirsOnShutdown(false);
			builder.setSocket(databaseDir + File.separator + "mariadb.sock");
			builder.addArg("--user=" + embeddedDBUser);

			if (options.trim().length() > 0) {

				StringTokenizer stOptions = new StringTokenizer(options, " ");
				while (stOptions.hasMoreTokens()) {
					String option = stOptions.nextToken().trim();

					if (option.length() == 0)
						continue;

					builder.addArg(option);
				}

			}

			// Initilize
			boolean isInit = true;
			File f = new File(baseDir + File.separator + "db.init");
			BufferedReader br = null;

			try {
				if (f.exists()) {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
					isInit = Boolean.parseBoolean(br.readLine().trim());
				} else {
					isInit = false;
				}

			} catch (Exception e) {
				isInit = true;
			} finally {
				try {
					br.close();
				} catch (Exception ee) {
				}
			}

			if (!isInit) {

				FileWriter fw = null;

				try {

					db = DB.newEmbeddedDB(builder.build());
					db.start();

					logger.info("[EMBEDDED_DB] startup.");

					logger.info("[EMBEDDED_DB] execute initialize");

					String updateQuery = "use mysql;UPDATE user SET Password=password('Insoft!23') WHERE User = 'root';FLUSH PRIVILEGES;";
					db.run(updateQuery);
					db.source("sql/init.sql", "root", "Insoft!23", "");
					db.source("org/springframework/session/jdbc/schema-mysql.sql", "omw", "Insoft!23", "omw");

					logger.info("[EMBEDDED_DB] complete initialize");

					fw = new FileWriter(f);
					fw.write("true");
					fw.flush();

				} catch (Exception e) {
					logger.error("[EMBEDDED_DB]", e);
					stopDb();
				} finally {
					try {
						fw.close();
					} catch (Exception ee) {
					}
				}

			} else {

				builder.setSecurityDisabled(false);

				try {
					db = DB.newEmbeddedDB(builder.build());
					db.start();
					logger.info("[EMBEDDED_DB] startup.");
				} catch (Exception e) {
					logger.error("[EMBEDDED_DB]", e);
					stopDb();
				}
			}
		}
	}

	@PreDestroy
	public void stopDb() {
		try {
			if (db != null)
				db.stop();
		} catch (ManagedProcessException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
		} finally {
			if (db != null)
				logger.info("[EMBEDDED_DB] shutdown.");
		}
	}

}
