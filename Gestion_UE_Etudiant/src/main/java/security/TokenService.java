package security;
import io.jsonwebtoken.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;
public final class TokenService {
 private static final byte[] KEY = new byte[64];
 private static final Map<String,Claims> ISSUED = new ConcurrentHashMap<>();
 static { new SecureRandom().nextBytes(KEY); }
 public static String issue(String user) {
  Date now = new Date(), expiry = new Date(now.getTime()+900000L);
  String token = Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(user)
   .setIssuer("soa-training").setIssuedAt(now).setExpiration(expiry)
   .signWith(SignatureAlgorithm.HS512,KEY).compact();
  ISSUED.entrySet().removeIf(e -> e.getValue().getExpiration().before(now));
  ISSUED.put(token,Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody());
  return token;
 }
 public static String validate(String token) {
  Jws<Claims> parsed=Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
  Claims c=parsed.getBody();
  if (!"HS512".equals(parsed.getHeader().getAlgorithm()) || !"soa-training".equals(c.getIssuer())
    || c.getExpiration()==null || !c.getExpiration().after(new Date()) || !ISSUED.containsKey(token))
   throw new IllegalArgumentException("Invalid token");
  return c.getSubject();
 }
}