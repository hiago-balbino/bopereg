package br.com.wes.services;

import br.com.wes.repositories.UserRepository;
import br.com.wes.security.jwt.JwtTokenProvider;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;


    public AuthService(JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> signin(AccountCredentialsVO credentials) {
        try {
            var username = credentials.getUsername();
            var password = credentials.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var tokenResponse = new TokenVO();
            var user = userRepository.findByUsername(username);
            if (Objects.nonNull(user)) {
                tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
            } else {
                throw new UsernameNotFoundException("Username " + username + " not found!");
            }

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    public ResponseEntity<?> refreshToken(String username, String refreshToken) {
        var tokenResponse = new TokenVO();
        var user = userRepository.findByUsername(username);

        if (Objects.nonNull(user)) {
            tokenResponse = tokenProvider.refreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }

        return ResponseEntity.ok(tokenResponse);
    }
}
