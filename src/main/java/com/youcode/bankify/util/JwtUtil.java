package com.youcode.bankify.util;

import com.youcode.bankify.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private  String SECRET_KEY ;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, user.getUsername(), 1000 * 60 * 60 );
        if(token == null || token.split("\\.").length != 3){
            throw new RuntimeException("Invalid token");
        }
        return token;
    }

    public String generateRefreshToken(User user) {
        Map<String , Object> claims = new HashMap<>();
        return createToken(claims, user.getUsername(), 1000 * 60 * 60 * 24 * 30);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        try{
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
            return token;
        } catch (Exception e){
            System.err.println("Error while generating token: "+e.getMessage());
            throw new RuntimeException("Error  generating JWT", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e){
            System.err.println("Malformed JWT Token: " +token);
            throw new RuntimeException("Malformed JWT Token", e);
        } catch (Exception e){
            System.err.println("Error extracting claims: "+e.getMessage());
            throw new RuntimeException("Error extracting claims", e);
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
