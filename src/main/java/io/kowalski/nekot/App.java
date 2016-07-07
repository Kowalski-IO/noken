package io.kowalski.nekot;

import com.hubspot.dropwizard.guicier.GuiceBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.kowalski.nekot.configuration.NekotConfig;
import io.kowalski.nekot.configuration.NekotModule;

public class App extends Application<NekotConfig> {

    public App() {

    }

    public static void main(final String[] args) {
        try {
            new App().run(args);
        } catch (final Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public final void initialize(final Bootstrap<NekotConfig> bootstrap) {
        final GuiceBundle<NekotConfig> guiceBundle = GuiceBundle.defaultBuilder(NekotConfig.class)
                .modules(new NekotModule()).build();

        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public final void run(final NekotConfig configuration, final Environment environment)
            throws Exception, RuntimeException {

    }

}
