package com.liquidenthusiasm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.action.function.*;
import com.liquidenthusiasm.action.vars.VarRepo;
import com.liquidenthusiasm.auth.CookieCredentialAuthFilter;
import com.liquidenthusiasm.auth.CovenCookieAuthenticator;
import com.liquidenthusiasm.dao.*;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.*;
import com.liquidenthusiasm.resources.views.CreateAccountView;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class CovenApplication extends Application<CovenConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(CovenResource.class);

    CachingAuthenticator<String, Coven> cachingAuthenticator;

    DBIFactory factory;

    DBI jdbi;

    private static boolean startingUp;

    public static void main(String[] args) throws Exception {
        try {
            new CovenApplication().run(args);
        } catch (Exception e) {
            log.error("Unexpected exception on startup.", e);
            System.exit(1);
        }
    }

    @Override
    public String getName() {
        return "coven";
    }

    public CachingAuthenticator<String, Coven> getCachingAuthenticator() {
        return cachingAuthenticator;
    }

    @Override
    public void initialize(Bootstrap<CovenConfiguration> bootstrap) {
        startingUp = true;
        bootstrap.addBundle(new MigrationsBundle<CovenConfiguration>() {

            @Override
            public DataSourceFactory getDataSourceFactory(CovenConfiguration config) {
                return config.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new ViewBundle<CovenConfiguration>() {

            @Override
            public Map<String, Map<String, String>> getViewConfiguration(CovenConfiguration config) {
                return config.getViewRendererConfiguration();
            }
        });

        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(CovenConfiguration config, Environment environment) throws IOException {
        // Databases
        factory = new DBIFactory();
        jdbi = factory.build(environment, config.getDataSourceFactory(), "db");
        final CovenDao covenDao = jdbi.onDemand(CovenDao.class);
        final PersonDao personDao = jdbi.onDemand(PersonDao.class);
        final StoryDao storyDao = jdbi.onDemand(StoryDao.class);
        final PropertyDao propertyDao = jdbi.onDemand(PropertyDao.class);

        // Authentication
        CovenCookieAuthenticator auth = new CovenCookieAuthenticator(covenDao);
        cachingAuthenticator = new CachingAuthenticator<>(
            environment.metrics(), auth,
            config.getAuthenticationCachePolicy());
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Coven.class));

        CookieCredentialAuthFilter<Coven> authFilter = new CookieCredentialAuthFilter<>("loginCookie", cachingAuthenticator);
        environment.jersey().register(new AuthDynamicFeature(authFilter));

        // Variables
        refreshVariableDefinitions();

        // Functions
        refreshStoryFunctions();

        // Actions
        refreshActions();

        // Register daos
        Daos.storyDao = storyDao;
        Daos.covenDao = covenDao;
        Daos.personDao = personDao;
        Daos.propertyDao = propertyDao;

        // Resources
        CovenResource covenResource = new CovenResource();
        environment.jersey().register(covenResource);
        AvailableActionsResource availableActionsResource = new AvailableActionsResource();
        environment.jersey().register(availableActionsResource);
        environment.jersey().register(new CreateAccountView());
        environment.jersey().register(new LoginResource(covenResource, cachingAuthenticator));
        environment.jersey().register(new EditorResource());
        environment.jersey().register(new ViewResource(availableActionsResource));

        startingUp = false;
    }

    private void refreshStoryFunctions() {
        StoryFunctionRepo sfr = buildStoryFunctionRepo();
        try {
            sfr.put("randomPersonName", new RandomPersonNameFunc());
            sfr.put("registerStudent", new RegisterStudentFunc());
            sfr.put("add", new AddFunc());
            sfr.put("set", new SetFunc());
            Daos.functionRepo = sfr;
        } catch (Exception e) {
            log.error("Unexpected error refreshing the story function repo", e);
            if (startingUp) {
                throw e;
            }
        }
    }

    private StoryFunctionRepo buildStoryFunctionRepo() {
        return new StoryFunctionRepo();
    }

    public static void refreshVariableDefinitions() {
        VarRepo varRepo = new VarRepo();
        try {
            FileUtils.iterateFiles(new File("vars"), new String[] { "json" }, true).forEachRemaining((f) -> {
                try {
                    varRepo.loadVars(f.getPath());
                } catch (Exception e) {
                    log.error("Exception while loading {}", f.getPath(), e);
                    throw new RuntimeException(e);
                }
            });
            Daos.varRepo = varRepo;
        } catch (Exception e) {
            log.error("Unexpected error refreshing the action repo", e);
            if (startingUp) {
                throw e;
            }
        }

    }

    public static void refreshActions() throws IOException {
        ActionRepo actionRepo = new ActionRepo();
        try {
            FileUtils.iterateFiles(new File("story"), new String[] { "json" }, true).forEachRemaining((f) -> {
                try {
                    actionRepo.addAction(f.getPath());
                } catch (IOException e) {
                    log.error("Exception while loading {}", f.getPath(), e);
                }
            });
            Daos.actionRepo = actionRepo;
        } catch (Exception e) {
            log.error("Unexpected error refreshing the action repo", e);
            if (startingUp) {
                throw e;
            }
        }
    }
}
