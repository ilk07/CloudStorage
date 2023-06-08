package com.hw.cloudstorage.security.jwt;

import com.hw.cloudstorage.config.JwtTokenProperties;
import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.model.entity.Role;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.services.impl.TokenServiceImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final JwtTokenProperties jwtConfig;
    private final UserDetailsService userDetailsService;
    private final TokenServiceImpl tokenService;
    private String secret;

    @Autowired
    public JwtTokenProvider(JwtTokenProperties jwtConfig, UserDetailsService userDetailsService, TokenServiceImpl tokenService) {
        this.jwtConfig = jwtConfig;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(jwtConfig.getSecret().getBytes());
    }

    public String createToken(User user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("username", user.getUsername());
        claims.put("roles", getRoleNames(user.getRoles()));

        Date now = new Date();
        Date validTime = new Date(now.getTime() + jwtConfig.getExpired());

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setIssuer(jwtConfig.getIssuer())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validTime)
                .signWith(signingKey, signatureAlgorithm)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (bearerToken != null && bearerToken.startsWith(jwtConfig.getBearer() + " ")) {
            int bearerLength = jwtConfig.getBearer().length() + 1;
            return bearerToken.substring(bearerLength, bearerToken.length());
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            return !parseClaimsJwsToken(token).getBody().getExpiration().before(new Date());

        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException jwtEx) {
            log.warn("In JwtTokenProvider validateToken threw exception {}", jwtEx.getMessage());
        } catch (Exception ex) {
            log.error("In JwtTokenProvider validateToken threw Exception {}", ex.getMessage());
        }

        return false;
    }

    public boolean isBlocked(String token) {
        return tokenService.isBlocked(token);
    }

    public Jws<Claims> parseClaimsJwsToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .build()
                .parseClaimsJws(token);
    }

    public void tokenToBlackList(HttpServletRequest request) {
        String token = resolveToken(request);
        Date expiration = parseClaimsJwsToken(token).getBody().getExpiration();
        Date now = new Date();
        Long expirationTimeInSeconds = expiration.getTime() - now.getTime();
        if (expirationTimeInSeconds > 0) {
            tokenService.save(new BlockedToken(token, expirationTimeInSeconds));
        }
    }

    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> roles = new ArrayList<>();
        userRoles.forEach(role -> roles.add(role.getName()));
        return roles;
    }

}
