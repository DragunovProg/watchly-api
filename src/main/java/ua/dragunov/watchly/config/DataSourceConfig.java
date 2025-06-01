package ua.dragunov.watchly.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceConfig {
    private static final Logger logger = LogManager.getLogger(DataSourceConfig.class);


    private volatile static DataSource dataSource;
    private final static Object lock = new Object();

    private DataSourceConfig() {}

    public static DataSource getConfiguredDataSource() {
        if (dataSource == null) {
            synchronized (lock) {
                if (dataSource == null) {
                    logger.info("⚙️ Initializing DataSource...");
                    try(InputStream configProperties = DataSourceConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                        if (configProperties == null) {
                            logger.error("❌ config.properties not found in classpath.");
                            throw new RuntimeException("config.properties not found");
                        }

                        Properties properties = new Properties();
                        properties.load(configProperties);
                        HikariConfig hikariConfig = new HikariConfig();

                        String timezone = getPropertyOrThrow(properties, "timezone");
                        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(timezone));
                        logger.debug("⏱ Timezone set to {}", timezone);


                        hikariConfig.setJdbcUrl(getPropertyOrThrow(properties, "jdbc.url"));
                        hikariConfig.setUsername(getPropertyOrThrow(properties, "jdbc.username"));
                        hikariConfig.setPassword(getPropertyOrThrow(properties, "jdbc.password"));
                        hikariConfig.setMaximumPoolSize(Integer.parseInt(getPropertyOrThrow(properties, "hikari.maxPoolSize")));
                        hikariConfig.setConnectionTimeout(Integer.parseInt(getPropertyOrThrow(properties, "hikari.connectionTimeout")));
                        dataSource = new HikariDataSource(hikariConfig);
                        logger.info("✅ DataSource initialized successfully.");

                        runMigration();
                    } catch (IOException e) {
                        logger.error("❌ Failed to load config.properties", e);
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        logger.error("❌ Unexpected error during DataSource initialization", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }



        return dataSource;
    }

    public static void runMigration() {
        if (dataSource == null) {
            synchronized (lock) {
                if (dataSource == null) {
                    logger.error("⛔ Cannot run Flyway migration: DataSource is not initialized.");
                    throw new IllegalStateException("Cannot run Flyway migration: DataSource is not initialized.");
                }
            }
        }

        try {
            logger.info("🚀 Starting Flyway migration...");
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load();

            flyway.migrate();

            logger.info("✅ Flyway migration completed successfully.");
        } catch (Exception e) {
            logger.error("❌ Flyway migration failed", e);
            throw new RuntimeException("Flyway migration failed", e);
        }
    }

    private static String getPropertyOrThrow(Properties properties, String key) {
        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            logger.error("🔍 Missing required property: {}", key);
            throw new IllegalArgumentException("Missing required property " + key + " in " + properties);
        }

        logger.debug("📦 Loaded property {} = {}", key, maskIfSensitive(key, value));
        return value;
    }

    private static String maskIfSensitive(String key, String value) {
        if (key.toLowerCase().contains("password")) {
            return "******";
        }
        return value;
    }

}
