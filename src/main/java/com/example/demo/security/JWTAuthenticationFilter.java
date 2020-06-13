package com.example.demo.security;

import com.example.demo.dto.ErrorDTO;
import com.example.demo.exception.BaseException;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static String PREFIX = "Bearer ";

    private static String AUTHORIZATION = "Authorization";

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AuthService authService;

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            Authentication authentication = getAuthentication(servletRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (BaseException e) {
            servletResponse.setStatus(e.getHttpCode());
            servletResponse.setHeader("Content-Type", "application/json");
            servletResponse.getWriter().print(mapper.writeValueAsString(ErrorDTO.valueOf(e)));
        } 
}

    private Authentication getAuthentication(HttpServletRequest request) throws BaseException {
        String token = request.getHeader(AUTHORIZATION);
        Authentication authentication = null;
        if (token != null) {
            // parse the token.
            User user = authService.validateToken(token.replace(PREFIX, ""));
            Set<GrantedAuthority> authorities = new HashSet<>();
            Optional.ofNullable(user.getAuthorities())
                    .map(a -> authorities.addAll(a.stream().map(s -> "SCOPE_" + s).map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
            authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return authentication;
    }
}
