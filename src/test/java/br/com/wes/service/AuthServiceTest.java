package br.com.wes.service;

import br.com.wes.model.Permission;
import br.com.wes.model.User;
import br.com.wes.repository.UserRepository;
import br.com.wes.security.jwt.JwtTokenProvider;
import br.com.wes.vo.v1.security.AccountCredentialsVO;
import br.com.wes.vo.v1.security.TokenVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;

    @Nested
    class SigninTest {

        @Test
        @DisplayName("Should signin user with success")
        public void shouldSigninUserWithSuccess() {
            var username = "usertest";
            var password = "test123";
            var credentials = new AccountCredentialsVO(username, password);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(any());

            Permission permission = new Permission();
            permission.setDescription("ADMIN");
            var user = new User();
            user.setPermissions(List.of(permission));
            user.setUserName(username);
            user.setPassword(password);
            when(userRepository.findByUsername(username)).thenReturn(user);

            var tokenResponse = new TokenVO();
            tokenResponse.setAccessToken("token");
            tokenResponse.setRefreshToken("refreshToken");
            when(tokenProvider.createAccessToken(username, user.getRoles())).thenReturn(tokenResponse);

            var token = authService.signin(credentials);
            TokenVO tokenVO = (TokenVO) token.getBody();

            assertNotNull(tokenVO);
            assertEquals("token", tokenVO.getAccessToken());
            assertEquals("refreshToken", tokenVO.getRefreshToken());
        }

        @Test
        @DisplayName("Should throw exception when username not found")
        public void shouldThrowExceptionWhenUsernameNotFound() {
            var username = "usertest";
            var password = "test123";
            var credentials = new AccountCredentialsVO(username, password);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(any());
            when(userRepository.findByUsername(username)).thenReturn(null);

            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                authService.signin(credentials);
            });

            assertEquals("Invalid username/password supplied", exception.getMessage());
        }
    }

    @Nested
    class RefreshTokenTest {

        @Test
        @DisplayName("Should refresh token with success")
        public void shouldRefreshTokenWithSuccess() {
            var username = "usertest";
            var refreshToken = "refreshToken";
            when(userRepository.findByUsername(username)).thenReturn(new User());

            var tokenResponse = new TokenVO();
            tokenResponse.setAccessToken("token");
            tokenResponse.setRefreshToken("refreshToken");
            when(tokenProvider.refreshToken(refreshToken)).thenReturn(tokenResponse);

            var token = authService.refreshToken(username, refreshToken);
            TokenVO tokenVO = (TokenVO) token.getBody();

            assertNotNull(tokenVO);
            assertEquals("token", tokenVO.getAccessToken());
            assertEquals("refreshToken", tokenVO.getRefreshToken());
        }

        @Test
        @DisplayName("Should throw exception when username not found")
        public void shouldThrowExceptionWhenUsernameNotFound() {
            var username = "usertest";
            var refreshToken = "refreshToken";
            when(userRepository.findByUsername(username)).thenReturn(null);

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
                authService.refreshToken(username, refreshToken);
            });

            assertEquals("Username usertest not found", exception.getMessage());
        }
    }
}