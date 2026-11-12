
package com.kaveks.commonweb.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JjwtJwtDecoder implements JwtDecoder {

    private final RSAPublicKey publicKey;
    private final String issuer;

    public JjwtJwtDecoder(
            RSAPublicKey publicKey,
            String issuer
    ) {
        this.publicKey = publicKey;
        this.issuer = issuer;
    }

    @Override
    @NullMarked
    public Jwt decode(String token) {

        try {

            Jws<Claims> parsedJwt = Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);

            // Explicitly enforce RS256.
            Object algorithm = parsedJwt.getHeader().get("alg");

            if (!"RS256".equals(algorithm)) {
                throw new BadJwtException(
                        "Unsupported JWT signing algorithm."
                );
            }

            Claims claims = parsedJwt.getPayload();

            validateRequiredClaims(claims);

            Map<String, Object> headers =
                    new HashMap<>(parsedJwt.getHeader());

            Map<String, Object> jwtClaims =
                    new HashMap<>(claims);

            // Spring Security expects timestamp claims as Instant.
            convertTimestampClaim(jwtClaims, "iat");
            convertTimestampClaim(jwtClaims, "exp");

            return Jwt.withTokenValue(token)
                    .headers(values -> values.putAll(headers))
                    .claims(values -> values.putAll(jwtClaims))
                    .build();

        } catch (ExpiredJwtException exception) {

            throw new BadJwtException(
                    "Access token has expired.",
                    exception
            );

        } catch (JwtException | IllegalArgumentException exception) {

            throw new BadJwtException(
                    "Access token is invalid.",
                    exception
            );
        }
    }

    private void validateRequiredClaims(Claims claims) {

        if (claims.getSubject() == null
                || claims.getIssuedAt() == null
                || claims.getExpiration() == null) {

            throw new BadJwtException(
                    "JWT is missing required claims."
            );
        }

        Object roles = claims.get(JwtConstant.ROLES_CLAIM);

        if (!(roles instanceof Collection<?> roleCollection)
                || roleCollection.isEmpty()
                || !roleCollection.stream().allMatch(
                role -> role instanceof String
                        && !((String) role).isBlank()
        )) {

            throw new BadJwtException(
                    "JWT does not contain valid roles."
            );
        }
    }

    private void convertTimestampClaim(
            Map<String, Object> claims,
            String claimName
    ) {

        Object value = claims.get(claimName);

        if (value instanceof Date date) {

            claims.put(claimName, date.toInstant());

        } else if (value instanceof Number timestamp) {

            claims.put(
                    claimName,
                    Instant.ofEpochSecond(timestamp.longValue())
            );
        }
    }

    private static final class JwtConstant {

        private static final String ROLES_CLAIM = "roles";
    }
}
