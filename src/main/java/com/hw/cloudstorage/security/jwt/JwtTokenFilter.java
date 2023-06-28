package com.hw.cloudstorage.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hw.cloudstorage.exceptions.Error;
import com.hw.cloudstorage.model.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            if (!jwtTokenProvider.isBlocked(token)) {
                try {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } catch (UsernameNotFoundException ex) {
                    HttpServletResponse response = (HttpServletResponse) servletResponse;
                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    response.getWriter().write(ow.writeValueAsString(new Error(ex.getMessage(), ErrorType.USER_NOT_FOUND.getErrorId())));
                    return;
                }

            } else {
                log.warn("In doFilter by JwtTokenProvider detected possibly stolen token(blocked) : {}", token);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
