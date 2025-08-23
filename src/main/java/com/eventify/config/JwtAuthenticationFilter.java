package com.eventify.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Ako heder ne postoji ili ne počinje sa "Bearer ", preskačemo filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // Validacija tokena i izvlačenje podataka
            Jws<Claims> claimsJws = jwtUtil.validate(jwt);
            Claims body = claimsJws.getBody();

            String email = body.getSubject();
            Long userId = body.get("uid", Long.class);
            String role = body.get("role", String.class);

            // Ako imamo email i korisnik još uvek nije autentifikovan
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Kreiramo UserDetails objekat sa podacima iz tokena
                UserDetails userDetails = new User(
                        email,
                        "", // Lozinka nije potrebna jer koristimo JWT
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Kreiramo Authentication objekat koji Spring Security razume
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Važno: Postavljamo ID korisnika u 'details'.
                // Kontroleri će odavde moći da ga pročitaju.
                authToken.setDetails(userId);

                // Postavljamo autentifikaciju u SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (JwtException e) {
            // Ako token nije validan (istekao, loš potpis), SecurityContext ostaje prazan.
            // Pristup zaštićenim resursima će biti automatski odbijen.
            SecurityContextHolder.clearContext();
        }

        // Prosleđujemo zahtev sledećem filteru u lancu
        filterChain.doFilter(request, response);
    }
}