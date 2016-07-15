package io.kowalski.noket.configuration;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;

import io.kowalski.noket.filters.JWTFilter;
import io.kowalski.noket.filters.RegistrationFilter;
import io.kowalski.noket.resources.NoketResource;

public class NoketModule extends DropwizardAwareModule<NoketConfig> {

    private final static HazelcastInstance hazelcast;

    static {

        final MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(new NoketMapStore());
        mapStoreConfig.setWriteDelaySeconds(5);

        final XmlConfigBuilder configBuilder = new XmlConfigBuilder();
        final Config config = configBuilder.build();
        final MapConfig mapConfig = config.getMapConfig(NoketConstants.REVOKED_TOKEN_MAP_NAME);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        hazelcast = Hazelcast.newHazelcastInstance(config);
        hazelcast.getConfig().setProperty("hazelcast.logging.type", "slf4j");

    }

    public NoketModule() {

    }

    @Override
    public void configure(final Binder binder) {

        binder.bind(NoketResource.class);
        binder.bind(RegistrationFilter.class);
        binder.bind(JWTFilter.class);

        binder.bind(String.class).annotatedWith(Names.named("secretKey")).toInstance(getConfiguration().getSecretKey());
        binder.bind(String.class).annotatedWith(Names.named("registrationCode")).toInstance(getConfiguration().getRegistrationCode());

        binder.bind(HazelcastInstance.class).toInstance(hazelcast);

    }

}
