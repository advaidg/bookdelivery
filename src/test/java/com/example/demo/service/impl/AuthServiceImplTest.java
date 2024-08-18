package com.example.demo.service.impl;

import com.example.demo.base.BaseServiceTest;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.model.enums.Role;
import com.example.demo.payload.request.auth.LoginRequest;
import com.example.demo.payload.request.auth.SignupRequest;
import com.example.demo.payload.request.auth.TokenRefreshRequest;
import com.example.demo.payload.response.auth.JWTResponse;
import com.example.demo.payload.response.auth.TokenRefreshResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest extends BaseServiceTest {


    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String SUCCESS = "success";
    private static final String MOCKED_TOKEN = "mockedToken";
    private static final String ACTUAL_REFRESH_TOKEN = "actualRefreshToken";
    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final String NEW_MOCKED_TOKEN = "newMockedToken";
    private static final String INVALID_AUTH_TOKEN = "invalidAuthToken";
    private static final String FAILED = "failed";
    private static final String ADMIN_PASSWORD = "admin_password";
    private static final String ADMIN_EMAIL = "admin@bookdelivery.com";
    private static final String CUSTOMER_PASSWORD = "customer_password";
    private static final String CUSTOMER_EMAIL = "customer@bookdelivery.com";

    @Test
    void givenSignUpRequest_WhenCustomerRole_ReturnSuccess() {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName("customer_fullname")
                .password(CUSTOMER_PASSWORD)
                .username("customer_1")
                .email(CUSTOMER_EMAIL)
                .role(Role.ROLE_CUSTOMER)
                .build();

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        // when
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // then
        String result = authService.register(request);

        assertEquals(SUCCESS, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenSignUpRequest_WhenCustomerRoleAndEmailAlreadyExists_ReturnException() {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName("customer_fullname")
                .password(CUSTOMER_PASSWORD)
                .username("customer_1")
                .email(CUSTOMER_EMAIL)
                .role(Role.ROLE_CUSTOMER)
                .build();

        // when
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // then
        assertThrows(Exception.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenLoginRequest_WhenCustomerRole_ReturnSuccess() {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(CUSTOMER_EMAIL)
                .password(CUSTOMER_PASSWORD)
                .build();

        User mockUser = User.builder()
                .email(request.getEmail())
                .fullName("Test User")
                .username("testuser")
                .password("hashedPassword")
                .role(Role.ROLE_CUSTOMER)
                .build();

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtils.generateJwtToken(mockAuthentication)).thenReturn(MOCKED_TOKEN);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(ACTUAL_REFRESH_TOKEN);

        // then
        JWTResponse jwtResponse = authService.login(request);

        assertNotNull(jwtResponse);
        assertEquals(request.getEmail(), jwtResponse.getEmail());
        assertEquals(MOCKED_TOKEN, jwtResponse.getToken());
        assertEquals(ACTUAL_REFRESH_TOKEN, jwtResponse.getRefreshToken());

    }

    @Test
    void givenLoginRequest_WhenCustomerRole_ReturnRuntimeException() {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(CUSTOMER_EMAIL)
                .password(CUSTOMER_PASSWORD)
                .build();

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        // then
        assertThrows(RuntimeException.class, () -> authService.login(request));

    }

    @Test
    void givenTokenRefreshRequest_WhenCustomerRole_ReturnSuccess() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(VALID_REFRESH_TOKEN)
                .user(User.builder().id(1L).build())
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.isRefreshExpired(refreshToken)).thenReturn(false);
        when(jwtUtils.generateJwtToken(any(CustomUserDetails.class))).thenReturn(NEW_MOCKED_TOKEN);

        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);

        assertNotNull(tokenRefreshResponse);
        assertEquals(NEW_MOCKED_TOKEN, tokenRefreshResponse.getAccessToken());
        assertEquals(VALID_REFRESH_TOKEN, tokenRefreshResponse.getRefreshToken());

    }

    @Test
    void givenTokenRefreshRequest_WhenCustomerRole_ReturnRefreshTokenNotFound() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("invalidRefreshToken")
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(Exception.class, () -> authService.refreshToken(request));
    }

    @Test
    void givenTokenRefreshRequest_WhenCustomerRole_ReturnRefreshTokenExpired() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("expiredRefreshToken")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(VALID_REFRESH_TOKEN)
                .user(User.builder().id(1L).build())
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.isRefreshExpired(refreshToken)).thenReturn(true);

        // then
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);

        assertNull(tokenRefreshResponse);
    }

    @Test
    void givenValidAccessToken_WhenCustomerRole_ReturnLogoutSuccess() {

        // Given
        String token = "validAuthToken";
        Long userId = 1L;

        // When
        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getIdFromToken(token)).thenReturn(userId);

        // Then
        String result = authService.logout(token);

        assertEquals(SUCCESS, result);
        verify(refreshTokenService).deleteByUserId(userId);
    }

    @Test
    void givenInvalidAccessToken_WhenCustomerRole_ReturnLogoutFailed() {
        // Given
        String token = INVALID_AUTH_TOKEN;

        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(null); // Invalid token

        // When
        String result = authService.logout(token);

        // Then
        assertEquals(FAILED, result);
        verify(refreshTokenService, never()).deleteByUserId(anyLong());
    }

    @Test
    void givenInvalidAccessToken_WhenCustomerRole_ReturnLogoutInvalidJwtToken() {
        // Given
        String token = INVALID_AUTH_TOKEN;

        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // When
        String result = authService.logout(token);

        // Then
        assertEquals(FAILED, result);
        verify(refreshTokenService, never()).deleteByUserId(anyLong());

    }

    @Test
    void givenSignUpRequest_WhenAdminRole_ReturnSuccess() {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName("admin_fullname")
                .password(ADMIN_PASSWORD)
                .username("admin_1")
                .email(ADMIN_EMAIL)
                .role(Role.ROLE_ADMIN)
                .build();

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        // when
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // then
        String result = authService.register(request);

        assertEquals(SUCCESS, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenSignUpRequest_WhenAdminRoleAndEmailAlreadyExists_ReturnException() {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName("admin_fullname")
                .password(ADMIN_PASSWORD)
                .username("admin_1")
                .email(ADMIN_EMAIL)
                .role(Role.ROLE_ADMIN)
                .build();

        // when
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // then
        assertThrows(Exception.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenLoginRequest_WhenWhenAdminRole_ReturnSuccess() {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(ADMIN_EMAIL)
                .password(ADMIN_PASSWORD)
                .build();

        User mockUser = User.builder()
                .email(request.getEmail())
                .fullName("Test User")
                .username("testuser")
                .password("hashedPassword")
                .role(Role.ROLE_CUSTOMER)
                .build();

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtils.generateJwtToken(mockAuthentication)).thenReturn(MOCKED_TOKEN);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(refreshTokenService.createRefreshToken(any(User.class)))
                .thenReturn(ACTUAL_REFRESH_TOKEN);

        // then
        JWTResponse jwtResponse = authService.login(request);

        assertNotNull(jwtResponse);
        assertEquals(request.getEmail(), jwtResponse.getEmail());
        assertEquals(MOCKED_TOKEN, jwtResponse.getToken());
        assertEquals(ACTUAL_REFRESH_TOKEN, jwtResponse.getRefreshToken());

    }

    @Test
    void givenLoginRequest_WhenAdminRole_ReturnRuntimeException() {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(ADMIN_EMAIL)
                .password(ADMIN_PASSWORD)
                .build();

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        // then
        assertThrows(RuntimeException.class, () -> authService.login(request));

    }

    @Test
    void givenTokenRefreshRequest_WhenAdminRole_ReturnSuccess() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(VALID_REFRESH_TOKEN)
                .user(User.builder().id(1L).build())
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.isRefreshExpired(refreshToken)).thenReturn(false);
        when(jwtUtils.generateJwtToken(any(CustomUserDetails.class))).thenReturn(NEW_MOCKED_TOKEN);

        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);

        assertNotNull(tokenRefreshResponse);
        assertEquals(NEW_MOCKED_TOKEN, tokenRefreshResponse.getAccessToken());
        assertEquals(VALID_REFRESH_TOKEN, tokenRefreshResponse.getRefreshToken());

    }

    @Test
    void givenTokenRefreshRequest_WhenAdminRole_ReturnRefreshTokenNotFound() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("invalidRefreshToken")
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(Exception.class, () -> authService.refreshToken(request));
    }

    @Test
    void givenTokenRefreshRequest_WhenAdminRole_ReturnRefreshTokenExpired() {

        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("expiredRefreshToken")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(VALID_REFRESH_TOKEN)
                .user(User.builder().id(1L).build())
                .build();

        // when
        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.isRefreshExpired(refreshToken)).thenReturn(true);

        // then
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);

        assertNull(tokenRefreshResponse);
    }

    @Test
    void givenValidAccessToken_WhenAdminRole_ReturnLogoutSuccess() {

        // Given
        String token = "validAuthToken";
        Long userId = 1L;

        // When
        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getIdFromToken(token)).thenReturn(userId);

        // Then
        String result = authService.logout(token);

        assertEquals(SUCCESS, result);
        verify(refreshTokenService).deleteByUserId(userId);
    }

    @Test
    void givenInvalidAccessToken_WhenAdminRole_ReturnLogoutFailed() {
        // Given
        String token = INVALID_AUTH_TOKEN;

        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(null); // Invalid token

        // When
        String result = authService.logout(token);

        // Then
        assertEquals(FAILED, result);
        verify(refreshTokenService, never()).deleteByUserId(anyLong());
    }

    @Test
    void givenInvalidAccessToken_WhenAdminRole_ReturnLogoutInvalidJwtToken() {
        // Given
        String token = INVALID_AUTH_TOKEN;

        when(jwtUtils.extractTokenFromHeader(token)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // When
        String result = authService.logout(token);

        // Then
        assertEquals(FAILED, result);
        verify(refreshTokenService, never()).deleteByUserId(anyLong());

    }
}
