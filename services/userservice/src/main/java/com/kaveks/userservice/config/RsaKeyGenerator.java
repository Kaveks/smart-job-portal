
package com.kaveks.userservice.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

@Component
public class RsaKeyGenerator {

    private static final int KEY_SIZE = 2048;

    private static final Set<PosixFilePermission> DIRECTORY_PERMISSIONS =
            PosixFilePermissions.fromString("rwx------");

    private static final Set<PosixFilePermission> PRIVATE_KEY_PERMISSIONS =
            PosixFilePermissions.fromString("rw-------");

    private static final Set<PosixFilePermission> PUBLIC_KEY_PERMISSIONS =
            PosixFilePermissions.fromString("rw-r--r--");

    public KeyPair loadOrGenerate(
            String privateKeyPath,
            String publicKeyPath
    ) throws IOException, GeneralSecurityException {

        Path privatePath = Path.of(privateKeyPath)
                .toAbsolutePath()
                .normalize();

        Path publicPath = Path.of(publicKeyPath)
                .toAbsolutePath()
                .normalize();

        Path privateDirectory = privatePath.getParent();
        Path publicDirectory = publicPath.getParent();

        if (privateDirectory == null
                || !privateDirectory.equals(publicDirectory)
                || privatePath.equals(publicPath)) {

            throw new IllegalArgumentException(
                    "Private and public keys must be different files "
                            + "within the same directory."
            );
        }

        Files.createDirectories(privateDirectory);

        setPermissions(
                privateDirectory,
                DIRECTORY_PERMISSIONS
        );

        boolean privateExists = Files.exists(privatePath);
        boolean publicExists = Files.exists(publicPath);

        // Load existing keys.
        if (privateExists && publicExists) {

            setPermissions(
                    privatePath,
                    PRIVATE_KEY_PERMISSIONS
            );

            setPermissions(
                    publicPath,
                    PUBLIC_KEY_PERMISSIONS
            );

            KeyPair existingKeyPair = loadKeyPair(
                    privatePath,
                    publicPath
            );

            validateKeyPair(existingKeyPair);

            return existingKeyPair;
        }

        // Never generate a new pair when one key already exists.
        if (privateExists || publicExists) {

            throw new IllegalStateException(
                    "Incomplete RSA key pair detected. "
                            + "Refusing to generate or overwrite keys. "
                            + "Restore the missing key or inspect "
                            + "the key directory."
            );
        }

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");

        generator.initialize(KEY_SIZE);

        KeyPair keyPair = generator.generateKeyPair();

        boolean privateCreated = false;
        boolean publicCreated = false;

        try {

            writePem(
                    privatePath,
                    "PRIVATE KEY",
                    keyPair.getPrivate().getEncoded(),
                    PRIVATE_KEY_PERMISSIONS
            );

            privateCreated = true;

            writePem(
                    publicPath,
                    "PUBLIC KEY",
                    keyPair.getPublic().getEncoded(),
                    PUBLIC_KEY_PERMISSIONS
            );

            publicCreated = true;

            validateKeyPair(keyPair);

            return keyPair;

        } catch (IOException | GeneralSecurityException exception) {

            // Only delete files created by this execution.
            cleanupCreatedFile(
                    privatePath,
                    privateCreated,
                    exception
            );

            cleanupCreatedFile(
                    publicPath,
                    publicCreated,
                    exception
            );

            throw exception;
        }
    }

    private KeyPair loadKeyPair(
            Path privatePath,
            Path publicPath
    ) throws IOException, GeneralSecurityException {

        String privateContent = Files.readString(
                privatePath,
                StandardCharsets.UTF_8
        );

        String publicContent = Files.readString(
                publicPath,
                StandardCharsets.UTF_8
        );

        byte[] privateBytes = decodePem(
                privateContent,
                "PRIVATE KEY"
        );

        byte[] publicBytes = decodePem(
                publicContent,
                "PUBLIC KEY"
        );

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPrivateKey privateKey =
                (RSAPrivateKey) keyFactory.generatePrivate(
                        new PKCS8EncodedKeySpec(privateBytes)
                );

        RSAPublicKey publicKey =
                (RSAPublicKey) keyFactory.generatePublic(
                        new X509EncodedKeySpec(publicBytes)
                );

        return new KeyPair(publicKey, privateKey);
    }

    private byte[] decodePem(
            String content,
            String type
    ) {

        String base64 = content
                .replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(base64);
    }

    private void writePem(
            Path path,
            String type,
            byte[] encoded,
            Set<PosixFilePermission> permissions
    ) throws IOException {

        String base64 = Base64.getMimeEncoder(
                64,
                new byte[]{'\n'}
        ).encodeToString(encoded);

        String pem = "-----BEGIN " + type + "-----\n"
                + base64
                + "\n-----END " + type + "-----\n";

        Path temporaryFile = createSecureTemporaryFile(
                path.getParent(),
                permissions
        );

        try {

            Files.writeString(
                    temporaryFile,
                    pem,
                    StandardCharsets.UTF_8
            );

            // No REPLACE_EXISTING.
            // Existing keys must never be silently overwritten.
            Files.move(temporaryFile, path);

        } finally {

            Files.deleteIfExists(temporaryFile);
        }
    }

    private Path createSecureTemporaryFile(
            Path directory,
            Set<PosixFilePermission> permissions
    ) throws IOException {

        FileAttribute<Set<PosixFilePermission>> attributes =
                PosixFilePermissions.asFileAttribute(permissions);

        Path temporaryFile;

        try {

            // Apply permissions when the file is created.
            temporaryFile = Files.createTempFile(
                    directory,
                    ".jwt-key-",
                    ".tmp",
                    attributes
            );

        } catch (UnsupportedOperationException exception) {

            // Fallback for filesystems without POSIX attributes.
            temporaryFile = Files.createTempFile(
                    directory,
                    ".jwt-key-",
                    ".tmp"
            );

            setPermissions(temporaryFile, permissions);
        }

        return temporaryFile;
    }

    private void validateKeyPair(
            KeyPair keyPair
    ) throws GeneralSecurityException {

        byte[] challenge =
                "smart-job-portal-key-validation"
                        .getBytes(StandardCharsets.UTF_8);

        Signature signer = Signature.getInstance("SHA256withRSA");

        signer.initSign(keyPair.getPrivate());
        signer.update(challenge);

        byte[] signature = signer.sign();

        Signature verifier =
                Signature.getInstance("SHA256withRSA");

        verifier.initVerify(keyPair.getPublic());
        verifier.update(challenge);

        if (!verifier.verify(signature)) {

            throw new GeneralSecurityException(
                    "RSA public and private keys do not match."
            );
        }
    }

    private void setPermissions(
            Path path,
            Set<PosixFilePermission> permissions
    ) throws IOException {

        try {

            Files.setPosixFilePermissions(
                    path,
                    permissions
            );

        } catch (UnsupportedOperationException ignored) {

            // POSIX permissions are unavailable on this filesystem.
            // Use appropriate operating-system ACLs instead.
        }
    }

    private void cleanupCreatedFile(
            Path path,
            boolean created,
            Exception originalException
    ) {

        if (!created) {
            return;
        }

        try {

            Files.deleteIfExists(path);

        } catch (IOException cleanupException) {

            originalException.addSuppressed(cleanupException);
        }
    }
}
