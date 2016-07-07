package io.kowalski.nekot.filters;

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

@RegistrationKey
@Provider
public class RegistrationFilter implements ContainerRequestFilter {

    private final String registrationCode;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    public RegistrationFilter(@Named("registrationCode") final String registrationCode) {
        this.registrationCode = registrationCode;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {

        final String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        final String token = authorizationHeader.substring("Bearer".length()).trim();

        if (!token.equals(registrationCode)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
