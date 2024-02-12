package org.sn.socialnetwork.security_and_config;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.context.annotation.Bean;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Converter
public class FieldEncryptor implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY = "Oz19Bc71h6BTs5b1QjPfGCG37wIY1Wt21eA6tOE8RM8=";
    private static final String IV = "XxYdsKr26NBgqDdvcFrZZw==";

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, createSecretKey(), new IvParameterSpec(Base64.getDecoder().decode(IV)));
            byte[] encrypted = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, createSecretKey(), new IvParameterSpec(Base64.getDecoder().decode(IV)));
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(original);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Key createSecretKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(KEY), ALGORITHM);
    }
}
