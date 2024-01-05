package br.com.wes.integrationtests;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@Testcontainers
public abstract class AbstractIntegrationTest {
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.2.0");

    static {
        mySQLContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource("testcontainers", (Map) createMySQLConnectionConfiguration())
                    );
        }

        private static Map<String, String> createMySQLConnectionConfiguration() {
            return Map.of(
                    "spring.datasource.url", mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username", mySQLContainer.getUsername(),
                    "spring.datasource.password", mySQLContainer.getPassword()
            );
        }
    }
}
