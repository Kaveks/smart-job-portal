
package com.kaveks.userservice.config;

import com.kaveks.commonlib.domain.UserRole;
import io.jsonwebtoken.Jwts;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JwtProvider {

    private final java.security.PrivateKey privateKey;
    private final String issuer;
    private final Duration accessTokenTtl;
    private final String keyId;

    public JwtProvider(
            java.security.KeyPair keyPair,
            String issuer,
            Duration accessTokenTtl
    ) {

        this.privateKey = keyPair.getPrivate();
        this.issuer = issuer;
        this.accessTokenTtl = accessTokenTtl;
        this.keyId = generateKeyId(
                (RSAPublicKey) keyPair.getPublic()
        );
    }

    public String generateAccessToken(
            Long userId,
            UserRole role
    ) {

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);

        return Jwts.builder()

                .header()
                .keyId(keyId)
                .and()

                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))

                .claim(
                        JwtConstant.ROLES_CLAIM,
                        List.of(role.name())
                )

                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String getRsaKeyId() {
        return keyId;
    }

    private String generateKeyId(
            RSAPublicKey publicKey
    ) {

        try {

            String modulus = encodeUnsigned(
                    publicKey.getModulus()
            );

            String exponent = encodeUnsigned(
                    publicKey.getPublicExponent()
            );

            // RFC 7638 canonical RSA JWK representation.
            String canonicalJwk = "{\"e\":\""
                    + exponent
                    + "\",\"kty\":\"RSA\",\"n\":\""
                    + modulus
                    + "\"}";

            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(canonicalJwk.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(digest);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "Unable to generate RSA key identifier.",
                    exception
            );
        }
    }

    private String encodeUnsigned(BigInteger value) {

        byte[] bytes = value.toByteArray();

        if (bytes[0] == 0) {
            bytes = java.util.Arrays.copyOfRange(
                    bytes,
                    1,
                    bytes.length
            );
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
