package school.faang.user_service.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.auth.Token;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.access.secret}")
    private String accessSecret;
    @Value("${jwt.access.expiration}")
    private int accessExpiration;
    @Value("${jwt.refresh.secret}")
    private String refreshSecret;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    @Override
    public int getRefreshSecretExpiration() {
        return refreshExpiration;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiredAt(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey(accessSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiredAt(token).before(new Date());
    }

    public Token generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(Map.of(), userDetails);
    }

    public Token generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return generateToken(extraClaims, userDetails.getUsername(), accessSecret, accessExpiration);
    }

    public Token generateRefreshToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails.getUsername(), refreshSecret, refreshExpiration);
    }

    private Token generateToken(Map<String, Object> extraClaims, String username, String secret, int expirationMillis) {
        Token.TokenBuilder token = Token.builder();
        LocalDateTime expiredAt = LocalDateTime.now()
                .plus(expirationMillis, ChronoUnit.MILLIS);
        token.value(Jwts.builder()
                        .signWith(getSecretKey(secret))
                        .claims().add(extraClaims)
                        .subject(username)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                        .and().compact())
                .expireAt(expiredAt)
                .expiration(expirationMillis);
        return token.build();
    }

    private SecretKey getSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
