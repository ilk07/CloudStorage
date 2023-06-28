package com.hw.cloudstorage.security.jwt;

import com.hw.cloudstorage.config.JwtTokenProperties;
import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.repositories.BlacklistTokenRepository;
import com.hw.cloudstorage.services.impl.TokenServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;

import static org.assertj.core.util.DateUtil.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtTokenProviderTest {

    JwtTokenProvider sut;

    @Autowired
    JwtTokenProperties jwtConfig;
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    TokenServiceImpl tokenService;

    @Mock
    BlacklistTokenRepository blacklist;


    @Mock
    User user;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start JwtTokenProvider Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---JwtTokenProvider Class Test Completed---");
    }

    @BeforeEach
    void initOneTest(){
        sut = new JwtTokenProvider(jwtConfig, userDetailsService, tokenService);
        sut.init();
    }

    @Test
    void createToken() {
        String actual = sut.createToken(user);

        assertTrue(actual.length() > 0);
        assertTrue(sut.validateToken(actual));

    }

    @Test
    void resolveToken() {
        String expected = sut.createToken(user);
        String bearerToken = jwtConfig.getBearer() + " " + expected;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtConfig.getHeader(), bearerToken);

        String actual = sut.resolveToken(request);

        assertEquals(expected,actual);
    }

    @Test
    void getUsername() {
        String expected = "user-jwt-test";
        when(user.getUsername()).thenReturn(expected);
        String token = sut.createToken(user);

        String actual = sut.getUsername(token);

        assertEquals(expected,actual);
    }

    @Test
    void validateToken() {
        boolean actual = sut.validateToken(sut.createToken(user));
        assertTrue(actual);
    }

    @Test
    void isBlocked() {
        String token = "";
        when(tokenService.isBlocked(anyString())).thenReturn(false);
        boolean actual = sut.isBlocked(token);
        assertTrue(!actual);

    }

    @Test
    void parseClaimsJwsToken() {
        Date nowDate = now();
        String token = sut.createToken(user);
        Jws<Claims> claimsJws = sut.parseClaimsJwsToken(token);
        assertTrue(claimsJws.getSignature().length() > 0);
        assertTrue(claimsJws.getHeader().containsKey("alg"));
        assertTrue(claimsJws.getBody().containsKey("roles"));
        assertTrue(claimsJws.getBody().getExpiration().after(nowDate));
    }

    @Test
    void tokenToBlackList() {

        BlockedToken blockedToken = mock(BlockedToken.class);
        when(blacklist.save(any(BlockedToken.class))).thenReturn(blockedToken);

        String token = sut.createToken(user);

        String bearerToken = jwtConfig.getBearer() + " " + token;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(jwtConfig.getHeader(), bearerToken);
        sut.tokenToBlackList(request);
        verify(tokenService, times(1)).save( any(BlockedToken.class));

    }
}