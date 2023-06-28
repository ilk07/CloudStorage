package com.hw.cloudstorage.security.jwt;

import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtTokenFilterTest {

    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtTokenFilter filter;

    @Test
    public void doFilter() throws ServletException, IOException, java.io.IOException {

        String token = "some.filter.test.token";

        when(jwtTokenProvider.resolveToken(httpRequest)).thenReturn(token);
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(anyString())).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilter(httpRequest, httpResponse, filterChain);
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}