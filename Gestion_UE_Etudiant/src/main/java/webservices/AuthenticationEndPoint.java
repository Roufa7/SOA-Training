package webservices;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import entities.Credentials;
@Path("authentification") @Produces(MediaType.TEXT_PLAIN)
public class AuthenticationEndPoint {
 @POST @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 public Response form(@FormParam("username") String u,@FormParam("password") String p){return login(u,p);}
 @POST @Consumes(MediaType.APPLICATION_JSON)
 public Response json(Credentials c){return login(c==null?null:c.getUsername(),c==null?null:c.getPassword());}
 private Response login(String u,String p){
  String expectedUser=System.getenv("SOA_USERNAME"), expectedPass=System.getenv("SOA_PASSWORD");
  if(expectedUser==null || expectedPass==null) return Response.status(503).entity("Configure SOA_USERNAME and SOA_PASSWORD").build();
  if(!expectedUser.equals(u)||p==null||!java.security.MessageDigest.isEqual(expectedPass.getBytes(java.nio.charset.StandardCharsets.UTF_8),p.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
   return Response.status(403).build();
  return Response.ok(security.TokenService.issue(u)).header("Cache-Control","no-store").build();
 }
}