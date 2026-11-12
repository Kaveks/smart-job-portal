
package com.kaveks.commonweb.security.jwt;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaPublicKeyLoader {

    public RSAPublicKey load(
            String publicKeyPath
    ) throws IOException, GeneralSecurityException {

        String content = Files.readString(
                Path.of(publicKeyPath)
        );

        String base64 = content
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(base64);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(
                new X509EncodedKeySpec(decoded)
        );
    }
}
