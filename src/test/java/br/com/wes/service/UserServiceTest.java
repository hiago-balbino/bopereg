package br.com.wes.service;

import br.com.wes.model.User;
import br.com.wes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should user details by username with success")
    public void shouldFindUserByUsername() {
        var username = "usertest";
        User user = new User();
        user.setId(1L);
        user.setUserName(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("usertest");

        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    public void shouldThrowExceptionWhenUserNotFound() {
        var username = "usertest";
        when(userRepository.findByUsername(username)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });

        assertEquals("Username usertest not found", exception.getMessage());
    }
}