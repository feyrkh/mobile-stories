package com.liquidenthusiasm;

import org.skife.jdbi.v2.DBI;

import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.auth.CovenAuthenticator;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.AvailableActionsResource;
import com.liquidenthusiasm.resources.CovenResource;
import com.liquidenthusiasm.resources.LogoutResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CovenApplication extends Application<CovenConfiguration> {

    CachingAuthenticator<BasicCredentials, Coven> cachingAuthenticator;

    DBIFactory factory;

    DBI jdbi;

    private ActionRepo actionRepo;

    public static void main(String[] args) throws Exception {
        new CovenApplication().run(args);
    }

    @Override
    public String getName() {
        return "coven";
    }

    public CachingAuthenticator<BasicCredentials, Coven> getCachingAuthenticator() {
        return cachingAuthenticator;
    }

    @Override
    public void initialize(Bootstrap<CovenConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<CovenConfiguration>() {

            @Override
            public DataSourceFactory getDataSourceFactory(CovenConfiguration config) {
                return config.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(CovenConfiguration config, Environment environment) {
        // Databases
        factory = new DBIFactory();
        jdbi = factory.build(environment, config.getDataSourceFactory(), "db");
        final CovenDao covenDao = jdbi.onDemand(CovenDao.class);

        // Authentication
        CovenAuthenticator auth = new CovenAuthenticator(covenDao);
        cachingAuthenticator = new CachingAuthenticator<BasicCredentials, Coven>(
            environment.metrics(), auth,
            config.getAuthenticationCachePolicy());
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Coven.class));

        environment.jersey().register(new AuthDynamicFeature(
            new BasicCredentialAuthFilter.Builder<Coven>()
                .setAuthenticator(cachingAuthenticator)
                .buildAuthFilter()));

        // Actions
        addActions();

        // Resources
        environment.jersey().register(new CovenResource(covenDao));
        environment.jersey().register(new AvailableActionsResource(actionRepo));
        environment.jersey().register(new LogoutResource());
    }

    private void addActions() {
        actionRepo = new ActionRepo();

    }
}
