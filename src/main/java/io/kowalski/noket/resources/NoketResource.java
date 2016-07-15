package io.kowalski.noket.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.kowalski.noket.configuration.NoketConstants;
import io.kowalski.noket.filters.JWT;
import io.kowalski.noket.filters.RegistrationKey;
import io.kowalski.noket.models.Registration;
import io.kowalski.noket.models.RevokedToken;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class NoketResource {

    private final HazelcastInstance hazelcast;
    private final String secretKey;

    @Inject
    public NoketResource(final HazelcastInstance hazelcast, @Named("secretKey") final String secretKey) {
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

    @GET
    @Path("/revoked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> revoked() {
        final IMap<String, RevokedToken> revokedTokens = hazelcast.getMap(NoketConstants.REVOKED_TOKEN_MAP_NAME);
        return new ArrayList<String>(revokedTokens.keySet());
    }

    @JWT
    @POST
    @Path("/tokens")
    public String issue(@HeaderParam("jwtSub") final String issuer) {
        return Jwts.builder().setIssuer(issuer).setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @JWT
    @DELETE
    @Path("/tokens/{id}")
    public void delete(@HeaderParam("jwtSub") final String issuer, @PathParam("id") final String jwt) {
        final Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();

        long evictAfter = 0;

        if (claims.getExpiration() != null) {
            evictAfter = (claims.getExpiration().getTime() - new Date().getTime()) / 1000;
        } else {
            evictAfter = -1;
        }

        final IMap<String, RevokedToken> revokedTokens = hazelcast.getMap(NoketConstants.REVOKED_TOKEN_MAP_NAME);

        if (evictAfter > 0) {
            final RevokedToken token = new RevokedToken(issuer, claims.getId(), claims.getExpiration());
            revokedTokens.put(claims.getId(), token, evictAfter, TimeUnit.SECONDS);
        } else if (evictAfter == -1) {
            final RevokedToken token = new RevokedToken(issuer, claims.getId(), null);
            revokedTokens.put(claims.getId(), token);
        }

    }

    @JWT
    @POST
    @Path("/auth")
    public boolean auth(final String jwt, @HeaderParam("jwtSub") final String issuer) {
        try {
            final Claims claims = Jwts.parser().requireIssuer("Nekok").setSigningKey(secretKey).parseClaimsJws(jwt)
                    .getBody();

            if (hazelcast.getSet("revoked-tokens-".concat(issuer)).contains(claims.getId())) {
                return false;
            }
        } catch (final JwtException e) {
            return false;
        }

        return true;
    }

}
