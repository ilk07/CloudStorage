package com.hw.cloudstorage.security.jwt;

import com.hw.cloudstorage.model.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.assertj.core.util.DateUtil.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUserTest {

    private JwtUser sut;

    private final static Long ID = 1l;
    private final static String USERNAME = "userName";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "mail@mail";
    private final static boolean ENABLED = true;
    private final static Date LASTPASSWORDRESETDATE = now();
    private final static String TEST_ROLE = "ROLE_TEST";

    @Mock
    User user;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start JwtUser Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---JwtUser Class Test Completed---");
    }

    @BeforeEach
    void initOneTest() {
        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
        setAuths.add(new SimpleGrantedAuthority(TEST_ROLE));
        List<GrantedAuthority> resultAuthoritiesList = new ArrayList<GrantedAuthority>(setAuths);

        sut = new JwtUser(ID, USERNAME, EMAIL, PASSWORD, resultAuthoritiesList, ENABLED, LASTPASSWORDRESETDATE);
    }

    @Test
    void getId() {
        Long expected = ID;
        Long actual = sut.getId();

        assertEquals(expected, actual);
    }

    @Test
    void getUsername() {
        String expected = USERNAME;
        String actual = sut.getUsername();

        assertEquals(expected, actual);
    }

    @Test
    void isAccountNonExpired() {
        String expected = USERNAME;
        String actual = sut.getUsername();

        assertEquals(expected, actual);
    }

    @Test
    void isAccountNonLocked() {
        boolean expected = true;
        boolean actual = sut.isAccountNonLocked();
        assertEquals(expected, actual);
    }

    @Test
    void isCredentialsNonExpired() {
        boolean expected = true;
        boolean actual = sut.isCredentialsNonExpired();
        assertEquals(expected, actual);
    }

    @Test
    void getPassword() {
        String expected = PASSWORD;
        String actual = sut.getPassword();

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorities() {
        String expected = TEST_ROLE;
        String actual = sut.getAuthorities().toString();

        assertTrue(actual.contains(expected));

    }

    @Test
    void isEnabled() {
        boolean expected = ENABLED;
        boolean actual = sut.isEnabled();

        assertEquals(expected, actual);
    }

    @Test
    void getLastPasswordResetDate() {
        Date expected = LASTPASSWORDRESETDATE;
        Date actual = sut.getLastPasswordResetDate();

        assertEquals(expected, actual);
    }
}