package security;

import io.jsonwebtoken.*;
import java.lang.reflect.Field;
import java.util.*;
import junit.framework.TestCase;

public class TokenServiceTest extends TestCase {
    public void testIssuedTokenAndLifetime() {
        String token = TokenService.issue("student");
        assertEquals("student", TokenService.validate(token));
    }

    @SuppressWarnings("unchecked")
    public void testExpiredTokenWithCorrectSignatureIsRejected() throws Exception {
        Field keyField = TokenService.class.getDeclaredField("KEY");
        keyField.setAccessible(true);
        byte[] key = (byte[]) keyField.get(null);
        Date expiry = new Date(System.currentTimeMillis() - 60000);
        Claims claims = Jwts.claims().setSubject("student").setIssuer("soa-training")
                .setIssuedAt(new Date(expiry.getTime() - 900000)).setExpiration(expiry);
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, key).compact();
        Field issuedField = TokenService.class.getDeclaredField("ISSUED");
        issuedField.setAccessible(true);
        ((Map<String, Claims>) issuedField.get(null)).put(token, claims);
        final java.util.concurrent.atomic.AtomicReference<javax.ws.rs.core.Response> aborted = new java.util.concurrent.atomic.AtomicReference<>();
        javax.ws.rs.container.ContainerRequestContext context = (javax.ws.rs.container.ContainerRequestContext)
            java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{javax.ws.rs.container.ContainerRequestContext.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getHeaderString")) return "Bearer " + token;
                    if (method.getName().equals("abortWith")) { aborted.set((javax.ws.rs.core.Response)args[0]); return null; }
                    return null;
                });
        new filtres.AuthenticationFilter().filter(context);
        assertNotNull(aborted.get());
        assertEquals(401, aborted.get().getStatus());
        assertEquals("Bearer", aborted.get().getHeaderString("WWW-Authenticate"));
        try {
            TokenService.validate(token);
            fail("Expired token accepted");
        } catch (ExpiredJwtException expected) {
            assertEquals("student", expected.getClaims().getSubject());
        }
    }

    public void testAlteredSignatureIsRejected() {
        String token = TokenService.issue("student");
        int index = token.lastIndexOf('.') + 1;
        String altered = token.substring(0,index) + (token.charAt(index)=='A'?'B':'A') + token.substring(index+1);
        try { TokenService.validate(altered); fail("Altered signature accepted"); }
        catch (SignatureException expected) { }
    }
}
