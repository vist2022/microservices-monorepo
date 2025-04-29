package daily_farm.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.function.Function;


@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.verification.token.validity}")
    private long verificationTokenValidity;
    
    @Value("${jwt.access.token.validity}")
    private long accessTokenValidity;
    
    @Value("${jwt.refresh.token.validity}")
    private long refreshTokenValidity ;

    private Key getSigningKey() {
    	log.debug("JwtService. Decoding secretKey for signing...");
    	Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); 
        return  key;
    }
    
    public String generatePassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
    
    public String generateVerificationTokenForUpdateEmail(String uuid, String email, String newEmail) {
        log.debug("JwtService. Generating verification token for email update: " + email + " -> " + newEmail);
        log.debug("JwtService. verificationTokenValidity = " + verificationTokenValidity);
    	String token = Jwts.builder()
                .subject(uuid)
                .claim("email", email)
                .claim("newEmail", newEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + verificationTokenValidity))
                
                .signWith(getSigningKey()) 
                .compact();
    	log.debug("JwtService. Generated token: " + token);
        return token;
    }

    public String generateVerificationToken(String uuid, String email) {
        log.debug("JwtService. Generating verification token for: " + email);
        log.debug("JwtService. verificationTokenValidity = " + verificationTokenValidity);
        
        if (verificationTokenValidity <= 0) {
            log.error("JwtService. ERROR: verificationTokenValidity is not set properly!");
        }
        
        log.debug("JwtService. Secret Key: " + (secretKey != null ? "Loaded" : "NOT LOADED"));
        
    	String token = Jwts.builder()
                .subject(uuid)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + verificationTokenValidity))
                
                .signWith(getSigningKey()) 
                .compact();
    	log.debug("JwtService. Generated token: " + token);
        return token;
    }
    public String generateAccessToken(String uuid, String email, String role) {
    	String token =  Jwts.builder()
        		 .subject(uuid)
                 .claim("email", email)
                 .claim("role", role)
                 .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSigningKey()) 
                .compact();
        log.debug("JwtService. Generated access token: " + token);
        return token;
    }

    public String generateRefreshToken(String uuid, String email, String role) {
    	log.debug("JwtService. Generating refresh token for: " + email);
    	String token = Jwts.builder()
        		 .subject(uuid)
                 .claim("email", email)
                 .claim("role", role)
                 .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(getSigningKey()) 
                .compact();
        log.debug("JwtService. Generated refresh token: " + token);
        return token;
    }
    
    public String extractUserId(String token) {
    	log.debug("JwtService. Extracting user ID from token...");
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("deprecation")
	private JwtParser getParser() {
        return Jwts.parser() 
                .setSigningKey(getSigningKey())  
                .build();
    }

    @SuppressWarnings("deprecation")
	private Claims extractAllClaims(String token) {
    	 try {
        return getParser().parseClaimsJws(token).getBody();
    	 } catch (JwtException e) {
             log.error("JwtService. ERROR extracting claims: " + e.getMessage());
             throw e;
         }
    }

    public String extractUserEmail(String token) {
    	log.debug("JwtService. Extracting email from token...");
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    
    public String extractUserRole(String token) {
    	log.debug("JwtService. Extracting role from token...");
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public String extractUserNewEmail(String token) {
    	 log.debug("JwtService. Extracting new email from token...");
        return extractClaim(token, claims -> claims.get("newEmail", String.class));
    }

    public Date extractExpiration(String token) {
    	 log.debug("JwtService. Extracting expiration date from token...");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String email) {
    	 log.debug("JwtService. Validating token for email: " + email);
    	 try {
             String extractedEmail = extractUserEmail(token);
             boolean isExpired = isTokenExpired(token);
             
             log.debug("JwtService. Extracted email: " + extractedEmail);
             log.debug("JwtService. Token is expired: " + isExpired);
        return extractUserEmail(token).equals(email) && !isTokenExpired(token);
    	 } catch (Exception e) {
             log.error("JwtService. ERROR validating token: " + e.getMessage());
             return false;
         }
    }

    public boolean isTokenExpired(String token) {
    	Date expirationDate = extractExpiration(token);
        boolean expired = expirationDate.before(new Date());
        
        log.debug("JwtService. Token expiration date: " + expirationDate);
        log.debug("JwtService. Is token expired? " + expired);
        return expired;
    }
}