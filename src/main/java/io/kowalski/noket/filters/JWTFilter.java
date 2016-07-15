package io.kowalski.noket.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.net.HttpHeaders;
import com.hazelcast.core.HazelcastInstance;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@JWT
@Provider
public class JWTFilter implements ContainerRequestFilter {

    private final HazelcastInstance hazelcast;
    private final String secretKey;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    public JWTFilter(final HazelcastInstance hazelcast, @Named("secretKey") final String secretKey) {
        this.hazelcast = hazelcast;
        this.secretKey = secretKey;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {

        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        final String token = authorizationHeader.substring("Bearer".length()).trim();

        Claims claims = null;

        try {
            claims = Jwts.parser().requireIssuer("Nekok").require("canReq", true)
                    .setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (final JwtException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if (claims != null) {
            requestContext.getHeaders().add("jwtSub", claims.getSubject());
        }

    }
}
