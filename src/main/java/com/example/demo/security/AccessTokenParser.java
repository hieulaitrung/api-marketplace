package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AccessTokenParser {
    private String publicKey;

    public AccessTokenParser(String publicKey) {
        this.publicKey = publicKey;
    }

    public Claims getClaims(String token) {
        try {
            PublicKey key = decodePublicKey(pemToDer(publicKey));
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] pemToDer(String pem) {
        return Base64.getDecoder().decode(pem);
    }

    private PublicKey decodePublicKey(byte[] der) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
