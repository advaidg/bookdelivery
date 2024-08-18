package com.example.demo.controller;

import com.example.demo.base.BaseControllerTest;
import com.example.demo.model.User;
import com.example.demo.model.enums.Role;
import com.example.demo.payload.request.auth.LoginRequest;
import com.example.demo.payload.request.auth.SignupRequest;
import com.example.demo.payload.request.auth.TokenRefreshRequest;
import com.example.demo.payload.response.auth.JWTResponse;
import com.example.demo.payload.response.auth.TokenRefreshResponse;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {

    private static final String SUCCESS = "success";
    private static final String ADMIN_FULLNAME = "admin_fullname";
    private static final String ADMIN_1 = "admin_1";
    private static final String ADMIN_EMAIL = "admin@bookdelivery.com";
    private static final String BEARER = "Bearer ";
    private static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    private static final String CUSTOMER_EMAIL = "customer@bookdelivery.com";
    private static final String CUSTOMER_FULLNAME = "customer_fullname";
    private static final String CUSTOMER_1 = "customer_1";

    @MockBean
    private AuthServiceImpl authService;


    @Test
    void givenSignupRequest_WhenCustomerRole_ReturnSuccess() throws Exception {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName(CUSTOMER_FULLNAME)
                .password("customer_password")
                .username(CUSTOMER_1)
                .email(CUSTOMER_EMAIL)
                .role(Role.ROLE_CUSTOMER)
                .build();

        when(authService.register(request)).thenReturn(SUCCESS);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    void givenSignupRequest_WhenAdminRole_ReturnSuccess() throws Exception {

        // given
        SignupRequest request = SignupRequest.builder()
                .fullName(ADMIN_FULLNAME)
                .password("admin_password")
                .username(ADMIN_1)
                .email(ADMIN_EMAIL)
                .role(Role.ROLE_ADMIN)
                .build();

        when(authService.register(request)).thenReturn(SUCCESS);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    void givenLoginRequest_WhenCustomerRole_ReturnSuccess() throws Exception {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(CUSTOMER_EMAIL)
                .password("customer_password")
                .build();

        JWTResponse mockResponse = JWTResponse.builder()
                .email(request.getEmail())
                .token("mockedToken")
                .refreshToken("mockedRefreshToken")
                .build();

        // when
        when(authService.login(request)).thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void givenRefreshTokenRequestAndAccessToken_WhenCustomerRole_Token_ReturnRefreshTokenSuccess() throws Exception {

        // given
        User mockUser = User.builder()
                .id(1L)
                .username(CUSTOMER_1)
                .email(CUSTOMER_EMAIL)
                .role(Role.ROLE_CUSTOMER)
                .fullName(CUSTOMER_FULLNAME)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);


        String accessToken = jwtUtils.generateJwtToken(userDetails);

        String mockBearerToken = BEARER + accessToken;

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        TokenRefreshResponse mockResponse = TokenRefreshResponse.builder()
                .accessToken("newMockedToken")
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        // when
        when(authService.refreshToken(request)).thenReturn(mockResponse);
        when(customUserDetailsService.loadUserByUsername(CUSTOMER_EMAIL)).thenReturn(userDetails);

        // then
        mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void givenAccessToken_WhenCustomerRole_ReturnLogoutSuccess() throws Exception {

        // Given
        User mockUser = User.builder()
                .id(1L)
                .username(CUSTOMER_1)
                .email(CUSTOMER_EMAIL)
                .role(Role.ROLE_CUSTOMER)
                .fullName(CUSTOMER_FULLNAME)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);


        String accessToken = jwtUtils.generateJwtToken(userDetails);

        String mockBearerToken = BEARER + accessToken;

        // When
        when(customUserDetailsService.loadUserByUsername(CUSTOMER_EMAIL)).thenReturn(userDetails);
        when(authService.logout(mockBearerToken)).thenReturn(SUCCESS);

        // Then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, mockBearerToken))
                .andExpect(status().isOk());

        verify(authService).logout(mockBearerToken);

    }

    @Test
    void givenLoginRequest_WhenAdminRole_ReturnSuccess() throws Exception {

        // given
        LoginRequest request = LoginRequest.builder()
                .email(ADMIN_EMAIL)
                .password("admin_password")
                .build();

        JWTResponse mockResponse = JWTResponse.builder()
                .email(request.getEmail())
                .token("mockedToken")
                .refreshToken("mockedRefreshToken")
                .build();

        // when
        when(authService.login(request)).thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void givenRefreshTokenRequestandAccessToken_WhenAdminRole_Token_ReturnRefreshTokenSuccess() throws Exception {

        // given
        User mockUser = User.builder()
                .id(2L)
                .username(ADMIN_1)
                .email(ADMIN_EMAIL)
                .role(Role.ROLE_ADMIN)
                .fullName(ADMIN_FULLNAME)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);


        String accessToken = jwtUtils.generateJwtToken(userDetails);

        String mockBearerToken = BEARER + accessToken;

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        TokenRefreshResponse mockResponse = TokenRefreshResponse.builder()
                .accessToken("newMockedToken")
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        // when
        when(authService.refreshToken(request)).thenReturn(mockResponse);
        when(customUserDetailsService.loadUserByUsername(ADMIN_EMAIL)).thenReturn(userDetails);

        // then
        mockMvc.perform(post("/api/v1/auth/refreshtoken")
                        .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void givenAccessToken_WhenAdminRole_ReturnLogoutSuccess() throws Exception {

        // Given
        User mockUser = User.builder()
                .id(2L)
                .username(ADMIN_1)
                .email(ADMIN_EMAIL)
                .role(Role.ROLE_ADMIN)
                .fullName(ADMIN_FULLNAME)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);


        String accessToken = jwtUtils.generateJwtToken(userDetails);

        String mockBearerToken = BEARER + accessToken;

        // When
        when(customUserDetailsService.loadUserByUsername(ADMIN_EMAIL)).thenReturn(userDetails);
        when(authService.logout(mockBearerToken)).thenReturn(SUCCESS);

        // Then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, mockBearerToken))
                .andExpect(status().isOk());

        verify(authService).logout(mockBearerToken);

    }
}
