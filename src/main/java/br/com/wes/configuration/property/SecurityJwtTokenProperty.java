package br.com.wes.configuration.property;

public record SecurityJwtTokenProperty(String secretKey, long expireLength) {
}
