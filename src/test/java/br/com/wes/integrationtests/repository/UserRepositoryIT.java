package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.model.User;
import br.com.wes.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIT extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by username with success")
    public void shouldFindUserByUsername() {
        User user = userRepository.findByUsername("usertest");

        assertEquals("usertest", user.getUsername());
        assertEquals("User Test", user.getFullName());
        assertNotNull(user.getPassword());
        assertTrue(user.getAccountNonExpired());
        assertTrue(user.getAccountNonLocked());
        assertTrue(user.getCredentialsNonExpired());
        assertTrue(user.getEnabled());
    }

    @Test
    @DisplayName("Should return null when user does not found")
    public void shouldReturnNullWhenUserDoesNotFound() {
        User user = userRepository.findByUsername("Unknown");
        assertNull(user);
    }
}
