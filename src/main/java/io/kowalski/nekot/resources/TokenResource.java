package io.kowalski.nekot.resources;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.hazelcast.core.HazelcastInstance;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.kowalski.nekot.filters.JWT;
import io.kowalski.nekot.filters.RegistrationKey;
import io.kowalski.nekot.models.Registration;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class TokenResource {

    private final HazelcastInstance hazelcast;
    private final String secretKey;

    @Inject
    public TokenResource(final HazelcastInstance hazelcast, @Named("secretKey") final String secretKey) {
        this.hazelcast = hazelcast;
        this.secretKey = secretKey;
    }

    @RegistrationKey
    @POST
    @Path("/register")
    public String registerApplication(final Registration registration) {
        return Jwts.builder().setIssuer("Nekok").setSubject(registration.getApplicationName())
                .claim("canReq", Boolean.TRUE).setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @JWT
    @POST
    @Path("/token")
    public String createJWT(@Context final HttpHeaders headers) {

        final String jwtSub = headers.getRequestHeader("jwtSub").get(0);

        return Jwts.builder().setIssuer(jwtSub)
                .setId(UUID.randomUUID().toString()).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

}
