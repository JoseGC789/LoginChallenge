package com.josegc.loginchallenge.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class PasswordService {

  private static final String CIPHER_ALGO = "AES/GCM/NoPadding"; // Change to RSA in the future
  private static final String KDF_ALGO = "PBKDF2WithHmacSHA256";
  private static final int AES_KEY_BITS = 256;
  private static final int GCM_IV_LENGTH_BYTES = 12;
  private static final int GCM_TAG_LENGTH_BITS = 128;
  private static final int PBKDF2_ITERATIONS = 65_536;

  private final PasswordEncoder passwordEncoder;

  @Value("${app.security.encryption.secret}")
  private final String encryptionSecret;

  @SneakyThrows
  String protect(String password, String salt) {
    SecretKey key = deriveKeyFromPassword(encryptionSecret.toCharArray(), salt.getBytes());
    return encrypt(key, salt.getBytes(), passwordEncoder.encode(password + salt));
  }

  @SneakyThrows
  boolean verify(String password, String salt, String encoded) {
    SecretKey key = deriveKeyFromPassword(encryptionSecret.toCharArray(), salt.getBytes());
    return passwordEncoder.matches(password + salt, decrypt(key, encoded));
  }

  private static SecretKey deriveKeyFromPassword(char[] password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, AES_KEY_BITS);
    SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALGO);
    byte[] keyBytes = skf.generateSecret(spec).getEncoded();
    return new SecretKeySpec(keyBytes, "AES");
  }

  private static String encrypt(SecretKey key, byte[] salt, String plaintext)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          InvalidAlgorithmParameterException,
          InvalidKeyException,
          IllegalBlockSizeException,
          BadPaddingException {
    byte[] iv = new byte[GCM_IV_LENGTH_BYTES];

    Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
    cipher.init(Cipher.ENCRYPT_MODE, key, spec);

    byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

    ByteBuffer bb =
        ByteBuffer.allocate(1 + (salt == null ? 0 : salt.length) + iv.length + cipherText.length);
    bb.put((byte) (salt == null ? 0 : salt.length));
    if (salt != null) {
      bb.put(salt);
    }
    bb.put(iv);
    bb.put(cipherText);
    return Base64.getEncoder().encodeToString(bb.array());
  }

  private static String decrypt(SecretKey key, String base64Envelope)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          IllegalBlockSizeException,
          BadPaddingException,
          InvalidAlgorithmParameterException,
          InvalidKeyException {
    byte[] all = Base64.getDecoder().decode(base64Envelope);
    ByteBuffer bb = ByteBuffer.wrap(all);

    int saltLen = Byte.toUnsignedInt(bb.get());
    byte[] salt;

    if (saltLen > 0) {
      salt = new byte[saltLen];
      bb.get(salt);
    }

    byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
    bb.get(iv);

    byte[] cipherText = new byte[bb.remaining()];
    bb.get(cipherText);

    Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
    GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
    cipher.init(Cipher.DECRYPT_MODE, key, spec);
    byte[] plain = cipher.doFinal(cipherText);
    return new String(plain, StandardCharsets.UTF_8);
  }
}
