package com.demoProject.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * the request, the response, consider filter chain like a pointer to the next filter in the chain
     * extract the authorization details from the request.
     * check whether the details is null or does not start with bearer.
     * if either of the above conditions, go to the next filter in the chain.
     * if both are fine,
     * get the token from the authorization header.
     * from the token, get the username. if username is not null && the user has not already been authenticated,
     * perform the following steps:
     * obtain the user details from the custom userServiceDetails class.
     * check if the token is valid (remember token expiration and that the user matches it)
     * if valid.
     * create an authentication object.
     * reinforce the details of the authentication object
     * set the security context holder with this authentication object
     * pass it to the next step in the filter chain.
     */

    private JWTService jwtService;
    private PassportUserDetailsService userDetailsService;
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwt = authHeader.substring(7);
        String username = jwtService.getUsername(jwt);
        log.info("username; {}",username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Boolean isValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.getRevoked() && !t.getExpired())
                    .orElse(false);


            if (jwtService.isTokenValid(jwt, userDetails) && isValid) {
                // Extract roles from the existing token
                List<String> roles = jwtService.getRolesFromToken(jwt);

                // Logging for debugging
                System.out.println("Roles extracted from the existing token: " + roles);

                // Generate a new token with roles after successful authentication
                String generatedToken = jwtService.generateTokenWithRolesAndPermissions(userDetails, roles);

                // Logging for debugging
                System.out.println("Generated Token with Roles and Permissions: " + generatedToken);

                // Convert roles to SimpleGrantedAuthority
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Logging for debugging
                System.out.println("Authorities after conversion: " + authorities);

                // Create an authentication object with the new token and set it in SecurityContextHolder
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, jwt, authorities);


                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }


        filterChain.doFilter(request, response);


    }

}
