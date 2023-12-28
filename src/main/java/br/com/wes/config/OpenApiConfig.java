package br.com.wes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bopereg")
                        .description("Project for registering books and people")
                        .version("v1")
                        .termsOfService("https://github.com/hiago-balbino")
                        .license(new License().name("MIT").url("https://github.com/hiago-balbino/bopereg/blob/main/LICENSE"))
                );
    }
}
