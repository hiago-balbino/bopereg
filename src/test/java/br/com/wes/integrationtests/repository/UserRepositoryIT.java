package br.com.wes.integrationtests.repository;

import br.com.wes.integrationtests.AbstractIT;
import br.com.wes.model.User;
import br.com.wes.repository.UserRepository;
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
    public void shouldReturnNullWhenUserDoesNotFound() {
        User user = userRepository.findByUsername("Unknown");
        assertNull(user);
    }
}
