package com.example.demo.security;

import com.example.demo.controller.model.ApiError;
import com.example.demo.controller.model.ErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static String PREFIX = "Bearer ";

    private static String SEPARATOR = " ";

    private static String AUTHORIZATION = "Authorization";

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${jwt.publicKey}")
    private String jwtPublicKey;

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            Authentication authentication = getAuthentication(servletRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ExpiredJwtException eje) {
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setHeader("Content-Type", "application/json");
            servletResponse.getWriter().print(mapper.writeValueAsString(ApiError.valueOf(ErrorInfo.TOKEN_EXPIRED)));
        } catch (Exception eje) {
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setHeader("Content-Type", "application/json");
            servletResponse.getWriter().print(mapper.writeValueAsString(ApiError.valueOf(ErrorInfo.PROTOCOL_UNAUTHORIZED)));
        }
}

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        Authentication authentication = null;
        if (token != null) {
            // parse the token.
            AccessTokenParser accessTokenParser = new AccessTokenParser(jwtPublicKey);
            Claims claims = accessTokenParser.getClaims(token.replace(PREFIX, ""));

            User user = new User();
            user.setUserId(claims.getSubject());
            String publisherIds = claims.get("publishers", String.class);
            Optional.ofNullable(publisherIds).ifPresent(p -> user.setPublisherIds(Arrays.asList(p.split(" "))));
            String scope = claims.get("scope", String.class);
            Set<GrantedAuthority> authorities = new HashSet<>();
            Optional.ofNullable(scope)
                    .map(s -> Arrays.asList(s.split(" ")))
                    .map(l -> authorities.addAll(l.stream().map(s -> "SCOPE_" + s).map(SimpleGrantedAuthority::new).collect(Collectors.toList())));

            authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return authentication;
    }
}
