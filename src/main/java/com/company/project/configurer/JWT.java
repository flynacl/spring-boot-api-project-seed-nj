package com.company.project.configurer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JWT {
    private static String secretKey = "secret";

    private static final Logger logger = LoggerFactory.getLogger(JWT.class);


    /**
     * 生成JWT
     *
     * @param id        该JWT的ID
     * @param issuer    该JWT的签发者
     * @param subject   该JWT所面向的用户
     * @param ttlMillis 过期时间，单位：毫秒
     * @return JWT字符串
     */
    public static String buildJWT(String id, String issuer, String subject, long ttlMillis) {

        byte[] encodedKey = DatatypeConverter.parseBase64Binary(secretKey);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signedKey = new SecretKeySpec(encodedKey, signatureAlgorithm.getJcaName());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);


        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signedKey);

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        String jwt = builder.compact();
        logger.info("生成jwt为：{}", jwt);

        return jwt;
    }

    /**
     * 解析JWT字符串
     *
     * @param jwt 待解析JWT字符串
     * @return Claims
     */
    public static Claims parseJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(jwt).getBody();

        logger.info("ID：{}", claims.getId());
        logger.info("Subject：{}", claims.getSubject());
        logger.info("Issuer：{}", claims.getIssuer());
        logger.info("IssuedAt：{}", DateFormatUtils.format(claims.getIssuedAt(), "yyyy-MM-dd HH:mm:ss"));
        logger.info("Expiration：{}", DateFormatUtils.format(claims.getExpiration(), "yyyy-MM-dd HH:mm:ss"));

        return claims;
    }
}
