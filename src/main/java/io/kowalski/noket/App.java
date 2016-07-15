package io.kowalski.noket;

import com.hubspot.dropwizard.guicier.GuiceBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.kowalski.noket.configuration.NoketConfig;
import io.kowalski.noket.configuration.NoketModule;

public class App extends Application<NoketConfig> {

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
    public final void initialize(final Bootstrap<NoketConfig> bootstrap) {
        final GuiceBundle<NoketConfig> guiceBundle = GuiceBundle.defaultBuilder(NoketConfig.class)
                .modules(new NoketModule()).build();

        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public final void run(final NoketConfig configuration, final Environment environment)
            throws Exception, RuntimeException {

    }

}
