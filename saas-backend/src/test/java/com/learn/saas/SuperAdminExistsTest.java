package com.learn.saas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.learn.saas.entity.User;
import com.learn.saas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SuperAdminExistsTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void superAdminShouldExistInDatabase() {
        Optional<User> userOpt = userRepository.findByUsername("SuperAdmin");
        assertThat(userOpt).isPresent();
        User user = userOpt.get();
        assertThat(user.getRole()).isEqualTo("SUPER_ADMIN");
        assertThat(user.getEmail()).isNotBlank();
    }
}
