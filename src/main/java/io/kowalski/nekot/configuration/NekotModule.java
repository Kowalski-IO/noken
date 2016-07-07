package io.kowalski.nekot.configuration;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;

import io.kowalski.nekot.filters.JWTFilter;
import io.kowalski.nekot.filters.RegistrationFilter;
import io.kowalski.nekot.resources.TokenResource;

public class NekotModule extends DropwizardAwareModule<NekotConfig> {

    private final HazelcastInstance hazelcast;

    public NekotModule() {
        hazelcast = Hazelcast.newHazelcastInstance();
        hazelcast.getConfig().setProperty("hazelcast.logging.type", "slf4j");
    }

    @Override
    public void configure(final Binder binder) {

        binder.bind(TokenResource.class);
        binder.bind(RegistrationFilter.class);
        binder.bind(JWTFilter.class);

        binder.bind(String.class).annotatedWith(Names.named("secretKey")).toInstance(getConfiguration().getSecretKey());
        binder.bind(String.class).annotatedWith(Names.named("registrationCode")).toInstance(getConfiguration().getRegistrationCode());

        binder.bind(HazelcastInstance.class).toInstance(hazelcast);

    }

}
