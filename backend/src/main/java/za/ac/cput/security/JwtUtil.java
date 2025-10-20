package za.ac.cput.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long ttlMs;

    public JwtUtil(@Value("${app.jwt.secret}") String base64Secret,
                   @Value("${app.jwt.ttl-ms:3600000}") long ttlMs) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.ttlMs = ttlMs;
    }

    /** Generate a token with just a subject. */
    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    /** Generate a token with extra custom claims (e.g. role, ids). */
    public String generateToken(String subject, Map<String,Object> extraClaims) {
        Instant now = Instant.now();
        var b = Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(new Date(now.toEpochMilli() + ttlMs))
                .signWith(key, Jwts.SIG.HS256);

        if (extraClaims != null && !extraClaims.isEmpty()) {
            b.claims(extraClaims);
        }
        return b.compact();
    }

    /** Validate signature & expiration. */
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRole(String token) {
        Object r = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role");
        return r == null ? null : r.toString();
    }
}
