package com.reboot.uam.lib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Reads gateway-injected identity headers and populates the {@code SecurityContext}.
 * Designed to run in each downstream service; the gateway is responsible for
 * JWT validation before forwarding these headers.
 *
 * <p>Expected headers:
 * <ul>
 *   <li>{@code X-User-Id} — authenticated user's numeric ID</li>
 *   <li>{@code X-User-Roles} — comma-separated role names (e.g. {@code ADMIN,USER})</li>
 *   <li>{@code X-User-Permissions} — comma-separated permission codes</li>
 * </ul>
 */
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-User-Roles";
    private static final String HEADER_PERMISSIONS = "X-User-Permissions";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);

        if (userId != null && !userId.isBlank()) {
            List<SimpleGrantedAuthority> authorities = buildAuthorities(
                    request.getHeader(HEADER_ROLES),
                    request.getHeader(HEADER_PERMISSIONS)
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> buildAuthorities(String roles, String permissions) {
        List<SimpleGrantedAuthority> roleAuthorities = parseAuthorities(roles, "ROLE_");
        List<SimpleGrantedAuthority> permissionAuthorities = parseAuthorities(permissions, "");

        List<SimpleGrantedAuthority> combined = new java.util.ArrayList<>(roleAuthorities);
        combined.addAll(permissionAuthorities);
        return Collections.unmodifiableList(combined);
    }

    private List<SimpleGrantedAuthority> parseAuthorities(String headerValue, String prefix) {
        if (headerValue == null || headerValue.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(headerValue.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(value -> new SimpleGrantedAuthority(prefix + value))
                .toList();
    }
}
