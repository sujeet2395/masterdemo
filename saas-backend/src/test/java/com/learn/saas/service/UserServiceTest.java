package com.learn.saas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.learn.saas.entity.Tenant;
import com.learn.saas.entity.User;
import com.learn.saas.repository.TenantRepository;
import com.learn.saas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long EXISTING_TENANT_ID = 1L;
    private static final String RAW_PASSWORD = "Password_1";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerBySuperAdminShouldCreateAdminUnderExistingTenant() {
        Tenant existingTenant = existingTenant();
        when(userRepository.findByUsername("tenantAdmin")).thenReturn(Optional.empty());
        when(tenantRepository.findById(EXISTING_TENANT_ID)).thenReturn(Optional.of(existingTenant));
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerBySuperAdmin(
                "tenantAdmin",
                "tenant.admin@simpleagent.masterdemo.com",
                RAW_PASSWORD,
                "ADMIN",
                EXISTING_TENANT_ID
        );

        assertThat(registeredUser.getUsername()).isEqualTo("tenantAdmin");
        assertThat(registeredUser.getEmail()).isEqualTo("tenant.admin@simpleagent.masterdemo.com");
        assertThat(registeredUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(registeredUser.getRole()).isEqualTo("ADMIN");
        assertThat(registeredUser.getTenant()).isSameAs(existingTenant);
        verify(userRepository).findByUsername("tenantAdmin");
        verify(tenantRepository).findById(EXISTING_TENANT_ID);
        verify(passwordEncoder).encode(RAW_PASSWORD);
    }

    @Test
    void registerBySuperAdminShouldCreateUserUnderExistingTenant() {
        Tenant existingTenant = existingTenant();
        when(userRepository.findByUsername("tenantUser")).thenReturn(Optional.empty());
        when(tenantRepository.findById(EXISTING_TENANT_ID)).thenReturn(Optional.of(existingTenant));
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerBySuperAdmin(
                "tenantUser",
                "tenant.user@simpleagent.masterdemo.com",
                RAW_PASSWORD,
                "USER",
                EXISTING_TENANT_ID
        );

        assertThat(registeredUser.getUsername()).isEqualTo("tenantUser");
        assertThat(registeredUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(registeredUser.getRole()).isEqualTo("USER");
        assertThat(registeredUser.getTenant()).isSameAs(existingTenant);
        verify(userRepository).findByUsername("tenantUser");
        verify(tenantRepository).findById(EXISTING_TENANT_ID);
        verify(passwordEncoder).encode(RAW_PASSWORD);
    }

    @Test
    void registerBySuperAdminShouldRejectDuplicateUsername() {
        User existingUser = User.builder()
                .username("tenantAdmin")
                .email("tenant.admin@simpleagent.masterdemo.com")
                .role("ADMIN")
                .build();
        when(userRepository.findByUsername("tenantAdmin")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.registerBySuperAdmin(
                "tenantAdmin",
                "tenant.admin@simpleagent.masterdemo.com",
                RAW_PASSWORD,
                "ADMIN",
                EXISTING_TENANT_ID
        )).isInstanceOf(RuntimeException.class)
                .hasMessage("User already exists");

        verify(tenantRepository, never()).findById(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerBySuperAdminShouldRejectMissingTenant() {
        when(userRepository.findByUsername("tenantAdmin")).thenReturn(Optional.empty());
        when(tenantRepository.findById(EXISTING_TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registerBySuperAdmin(
                "tenantAdmin",
                "tenant.admin@simpleagent.masterdemo.com",
                RAW_PASSWORD,
                "ADMIN",
                EXISTING_TENANT_ID
        )).isInstanceOf(RuntimeException.class)
                .hasMessage("Tenant not found");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerByOrgAdminShouldCreateOnlyUserRoleUnderExistingTenant() {
        Tenant existingTenant = existingTenant();
        when(userRepository.findByUsername("tenantUser")).thenReturn(Optional.empty());
        when(tenantRepository.findById(EXISTING_TENANT_ID)).thenReturn(Optional.of(existingTenant));
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerByOrgAdmin(
                "tenantUser",
                "tenant.user@simpleagent.masterdemo.com",
                RAW_PASSWORD,
                EXISTING_TENANT_ID
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(registeredUser.getRole()).isEqualTo("USER");
        assertThat(userCaptor.getValue().getRole()).isEqualTo("USER");
        assertThat(userCaptor.getValue().getTenant()).isSameAs(existingTenant);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    private Tenant existingTenant() {
        return Tenant.builder()
                .id(EXISTING_TENANT_ID)
                .name("Simple Agent")
                .domain("simpleagent.masterdemo.com")
                .build();
    }
}
