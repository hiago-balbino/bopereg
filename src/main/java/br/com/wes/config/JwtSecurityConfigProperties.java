package br.com.wes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtSecurityConfigProperties {

    private String secretKey;
    private long expireLength = 3600000;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpireLength() {
        return expireLength;
    }

    public void setExpireLength(long expireLength) {
        this.expireLength = expireLength;
    }
}
