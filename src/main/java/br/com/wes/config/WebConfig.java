package br.com.wes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.originPatterns:http://localhost:8080}")
    private String corsOriginPatterns = "";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrigins = corsOriginPatterns.split(",");
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(allowedOrigins)
                .allowCredentials(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        /* This example shows how to configure using the keyword mediaType as query param
           configurer
                   .favorParameter(true)
                   .parameterName("mediaType")
                   .ignoreAcceptHeader(true)
                   .useRegisteredExtensionsOnly(false)
                   .defaultContentType(MediaType.APPLICATION_JSON)
                   .mediaType("json", MediaType.APPLICATION_JSON)
        */

        /* This example shows how to configure using headers, e.g. Accept application/json */
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}
