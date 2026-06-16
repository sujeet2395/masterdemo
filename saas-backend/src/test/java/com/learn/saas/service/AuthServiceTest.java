package com.learn.saas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.learn.saas.entity.User;
import com.learn.saas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SUPER_ADMIN_USERNAME = "SuperAdmin";
    private static final String RAW_PASSWORD = "Password_1";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedSuperAdminPassword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticateShouldReturnSuperAdminWhenUsernameAndPasswordAreValid() {
        User superAdmin = User.builder()
                .id(1L)
                .username(SUPER_ADMIN_USERNAME)
                .email("superadmin@masterdemo.com")
                .password(ENCODED_PASSWORD)
                .role("SUPER_ADMIN")
                .build();

        when(userRepository.findByUsername(SUPER_ADMIN_USERNAME)).thenReturn(Optional.of(superAdmin));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        User authenticatedUser = authService.authenticate(SUPER_ADMIN_USERNAME, RAW_PASSWORD);

        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getUsername()).isEqualTo(SUPER_ADMIN_USERNAME);
        assertThat(authenticatedUser.getRole()).isEqualTo("SUPER_ADMIN");
        verify(userRepository).findByUsername(SUPER_ADMIN_USERNAME);
        verify(passwordEncoder).matches(RAW_PASSWORD, ENCODED_PASSWORD);
    }

    @Test
    void authenticateShouldReturnNullWhenSuperAdminPasswordIsInvalid() {
        User superAdmin = User.builder()
                .id(1L)
                .username(SUPER_ADMIN_USERNAME)
                .email("superadmin@masterdemo.com")
                .password(ENCODED_PASSWORD)
                .role("SUPER_ADMIN")
                .build();

        when(userRepository.findByUsername(SUPER_ADMIN_USERNAME)).thenReturn(Optional.of(superAdmin));
        when(passwordEncoder.matches("wrong-password", ENCODED_PASSWORD)).thenReturn(false);

        User authenticatedUser = authService.authenticate(SUPER_ADMIN_USERNAME, "wrong-password");

        assertThat(authenticatedUser).isNull();
        verify(userRepository).findByUsername(SUPER_ADMIN_USERNAME);
        verify(passwordEncoder).matches("wrong-password", ENCODED_PASSWORD);
    }

    @Test
    void authenticateShouldReturnNullWhenSuperAdminUsernameDoesNotExist() {
        when(userRepository.findByUsername(SUPER_ADMIN_USERNAME)).thenReturn(Optional.empty());

        User authenticatedUser = authService.authenticate(SUPER_ADMIN_USERNAME, RAW_PASSWORD);

        assertThat(authenticatedUser).isNull();
        verify(userRepository).findByUsername(SUPER_ADMIN_USERNAME);
        verifyNoInteractions(passwordEncoder);
    }
}
