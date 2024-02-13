package br.com.wes.security.jwt;

import br.com.wes.configuration.property.BoperegProperty;
import br.com.wes.exception.InvalidJwtAuthenticationException;
import br.com.wes.vo.v1.security.TokenVO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {

    private final BoperegProperty boperegProperty;
    private final UserDetailsService userDetailsService;
    Algorithm algorithm = null;
    String secretKeyEncoded = null;

    @PostConstruct
    protected void init() {
        secretKeyEncoded = Base64.getEncoder().encodeToString(boperegProperty.security().jwt().token().secretKey().getBytes());
        algorithm = Algorithm.HMAC256(secretKeyEncoded.getBytes());
    }

    public TokenVO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + boperegProperty.security().jwt().token().expireLength());

        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);

        return new TokenVO(
                username,
                true,
                now,
                validity,
                accessToken,
                refreshToken
        );
    }

    public TokenVO refreshToken(String refreshToken) {
        if (refreshToken.contains("Bearer")) {
            refreshToken = refreshToken.substring("Bearer ".length());
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        return createAccessToken(username, roles);
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm)
                .strip();
    }

    private String getRefreshToken(String username, List<String> roles, Date now) {
        Date validityRefreshToken = new Date(now.getTime() + (boperegProperty.security().jwt().token().expireLength() * 3)); // refresh token valid for 3 hours

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validityRefreshToken)
                .withSubject(username)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKeyEncoded.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            return decodedToken(token)
                    .getExpiresAt().after(new Date());
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT Token");
        }
    }
}
