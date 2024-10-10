package com.example.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "fccd7e38c4689c7616a702eab984278b5b9c4ef181e8174ce0d85a184fe4454a";

    private Key getSignKey(){
        byte[] keyInBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyInBytes);
    }

    public String generateJwt(UserDetails user) {
        return this.generateJwt(new HashMap<>(), user);
    }

    public String generateJwt(Map<String, Object> additionalClaims, UserDetails user) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +1000 * 60 * 60 ))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUserName(String jwt) {
        return this.extractOneClaim(jwt, Claims::getSubject);
    }

    private Claims extractAllClaims(String jwt){
        return Jwts.parserBuilder().setSigningKey(this.getSignKey()).build().parseClaimsJws(jwt).getBody();
    }

    private <T> T extractOneClaim(String jwt, Function<Claims, T> resolver) {
        final Claims claims = this.extractAllClaims(jwt);
        return resolver.apply(claims);
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails) {
        String username = this.extractUserName(jwt);
        return (username.equals(userDetails.getUsername())   );
    }

    private boolean isJwtExpired(String jwt) {
        return this.getExpirationDate(jwt).before(new Date());
    }

    private Date getExpirationDate(String jwt) {
        return this.extractOneClaim(jwt, Claims::getExpiration);
    }
}
