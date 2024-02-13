package br.com.wes.service;

import br.com.wes.repository.UserRepository;
import br.com.wes.security.jwt.JwtTokenProvider;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public ResponseEntity<?> signin(AccountCredentialsVO credentials) {
        log.info("Authenticating user");

        try {
            var username = credentials.getUsername();
            var password = credentials.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var tokenResponse = new TokenVO();
            var user = userRepository.findByUsername(username);
            if (Objects.nonNull(user)) {
                tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
            } else {
                throw new UsernameNotFoundException("Username %s not found".formatted(username));
            }

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    public ResponseEntity<?> refreshToken(String username, String refreshToken) {
        log.info("Refreshing token");

        var tokenResponse = new TokenVO();
        var user = userRepository.findByUsername(username);

        if (Objects.nonNull(user)) {
            tokenResponse = tokenProvider.refreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username %s not found".formatted(username));
        }

        return ResponseEntity.ok(tokenResponse);
    }
}
