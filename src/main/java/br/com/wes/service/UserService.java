package br.com.wes.service;

import br.com.wes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Finding one user by name {}", username);

        var user = userRepository.findByUsername(username);
        if (Objects.nonNull(user)) {
            return user;
        } else {
            throw new UsernameNotFoundException("Username %s not found".formatted(username));
        }
    }
}
