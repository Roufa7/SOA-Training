package filtres;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
@Secured @javax.ws.rs.ext.Provider @javax.annotation.Priority(javax.ws.rs.Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
 public void filter(ContainerRequestContext ctx) {
  String h=ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
  try {
   if(h==null || !h.regionMatches(true,0,"Bearer ",0,7)) throw new IllegalArgumentException();
   final String user=security.TokenService.validate(h.substring(7).trim());
   final SecurityContext old=ctx.getSecurityContext();
   ctx.setSecurityContext(new SecurityContext(){
    public java.security.Principal getUserPrincipal(){return () -> user;}
    public boolean isUserInRole(String role){return false;}
    public boolean isSecure(){return old.isSecure();}
    public String getAuthenticationScheme(){return "Bearer";}
   });
  } catch(RuntimeException e) { ctx.abortWith(Response.status(401).header("WWW-Authenticate","Bearer").build()); }
 }
}