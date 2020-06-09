package com.example.demo.security;

import com.example.demo.controller.model.ApiError;
import com.example.demo.controller.model.ErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends DelegatingFilterProxy {

    private static String PREFIX = "Bearer ";

    private static String SEPARATOR = " ";

    private static String AUTHORIZATION = "Authorization";

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${jwt.publicKey}")
    private String jwtPublicKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        try {
            Authentication authentication = getAuthentication((HttpServletRequest) servletRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ExpiredJwtException eje) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) servletResponse).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) servletResponse).getWriter().print(mapper.writeValueAsString(ApiError.valueOf(ErrorInfo.TOKEN_EXPIRED)));
        } catch (Exception eje) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) servletResponse).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) servletResponse).getWriter().print(mapper.writeValueAsString(ApiError.valueOf(ErrorInfo.PROTOCOL_UNAUTHORIZED)));
        }
}

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        Authentication authentication = null;
        if (token != null) {
            // parse the token.
            AccessTokenParser accessTokenParser = new AccessTokenParser(jwtPublicKey);
            Claims claims = accessTokenParser.getClaims(token.replace(PREFIX, ""));

            if (claims != null) {
                User userDetails = new User();
                userDetails.setUserId(claims.getSubject());
                String publisherIds = claims.get("publishers", String.class);
                Optional.ofNullable(publisherIds).ifPresent(p -> userDetails.setPublisherIds(Arrays.asList(p.split(" "))));
                String scope = claims.get("scope", String.class);
                Optional.ofNullable(scope).ifPresent(s -> userDetails.setPermissions(Arrays.asList(s.split(" "))));
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        return authentication;
    }
}
