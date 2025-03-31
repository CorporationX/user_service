package school.faang.user_service.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class JwtService {

    public Optional<Jwt> getCurrentJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    public Optional<String> getUsername() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("preferred_username"));
    }

    public Optional<String> getEmail() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    public Optional<List<String>> getRoles() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsStringList("spring_sec_roles"));
    }

    public Optional<String> getToken() {
        return getCurrentJwt().map(Jwt::getTokenValue);
    }

    public boolean isTokenExpired() {
        return getCurrentJwt().map(jwt -> jwt.getExpiresAt().isBefore(Instant.now())).orElse(true);
    }
}
