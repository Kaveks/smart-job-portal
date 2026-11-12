
package com.kaveks.userservice.config;

import com.kaveks.commonweb.security.jwt.JjwtJwtDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Configuration
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair(
            RsaKeyGenerator rsaKeyGenerator,
            @Value("${app.security.jwt.private-key-path}")
            String privateKeyPath,
            @Value("${app.security.jwt.public-key-path}")
            String publicKeyPath
    ) throws IOException, GeneralSecurityException {

        return rsaKeyGenerator.loadOrGenerate(
                privateKeyPath,
                publicKeyPath
        );
    }

    @Bean
    public RSAPublicKey jwtPublicKey(KeyPair jwtKeyPair) {

        return (RSAPublicKey) jwtKeyPair.getPublic();
    }

    @Bean
    public JwtProvider jwtProvider(
            KeyPair jwtKeyPair,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.access-token-ttl}")
            Duration accessTokenTtl
    ) {

        return new JwtProvider(
                jwtKeyPair,
                issuer,
                accessTokenTtl
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(
            RSAPublicKey jwtPublicKey,
            @Value("${app.security.jwt.issuer}") String issuer
    ) {

        return new JjwtJwtDecoder(
                jwtPublicKey,
                issuer
        );
    }
}
