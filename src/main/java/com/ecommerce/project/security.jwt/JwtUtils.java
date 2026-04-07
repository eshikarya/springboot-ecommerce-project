package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.key.expiration.time}")
    private int jwtKeyExpirationTime;

    @Value("${jwt.cookie}")
    private String jwtCookie;

    //Logger
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //get JWT from header
//    public String getJwtFromHeader(HttpServletRequest request){
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Token: {}",bearerToken);
//        if (bearerToken!=null && bearerToken.startsWith("Bearer ")){
//            bearerToken = bearerToken.substring(7);
//            return bearerToken;
//        }
//        return null;
//    }

    // get JWT from cookie
    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        return cookie!=null?cookie.getValue():null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }

    public ResponseCookie generateCleanCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,null)
                .path("/api")
                .build();
        return cookie;
    }

    //generate token from username:
    public String generateTokenFromUsername(String username){
        String jwtToken = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+jwtKeyExpirationTime))
                .signWith(generateSigningKey())
                .compact();

        return jwtToken;
    }

    //generate username from token
    public String getUsernameFromToken(String authToken){
        String username = Jwts.parser()
                .verifyWith((SecretKey) generateSigningKey())
                .build()
                .parseSignedClaims(authToken)
                .getPayload().getSubject();
        return username;
    }

    //generate signing key
    public Key generateSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    //validate jwt token
    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) generateSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }
        catch (MalformedJwtException e){
            logger.error("Invalid JWT Token Excpetion: {}",e.getMessage());
        }
        catch (ExpiredJwtException e){
            logger.error("Expired JWT Token Exception: {}",e.getMessage());
        }
        catch (UnsupportedJwtException e){
            logger.error("JWT Token is unsupported: {}",e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error("JWT Claims string is empty",e.getMessage());
        }
        return false;
    }
}
