package br.com.wes.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bopereg")
public record BoperegProperty(
        CorsProperty cors,
        FileStorageProperty file,
        SecurityProperty security
) {
}
