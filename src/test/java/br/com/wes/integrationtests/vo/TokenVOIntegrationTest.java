package br.com.wes.integrationtests.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TokenVOIntegrationTest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4312617395517231347L;

    private String username;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    public TokenVOIntegrationTest(String username, Boolean authenticated, Date created, Date expiration, String accessToken, String refreshToken) {
        this.username = username;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public TokenVOIntegrationTest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenVOIntegrationTest tokenVOIntegrationTest = (TokenVOIntegrationTest) o;
        return Objects.equals(username, tokenVOIntegrationTest.username) && Objects.equals(authenticated, tokenVOIntegrationTest.authenticated) && Objects.equals(created, tokenVOIntegrationTest.created) && Objects.equals(expiration, tokenVOIntegrationTest.expiration) && Objects.equals(accessToken, tokenVOIntegrationTest.accessToken) && Objects.equals(refreshToken, tokenVOIntegrationTest.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authenticated, created, expiration, accessToken, refreshToken);
    }
}
