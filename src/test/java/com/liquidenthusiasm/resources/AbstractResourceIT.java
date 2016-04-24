package com.liquidenthusiasm.resources;

import java.sql.Connection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.CovenApplication;
import com.liquidenthusiasm.CovenConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public abstract class AbstractResourceIT {

    @ClassRule
    public static final DropwizardAppRule<CovenConfiguration> RULE =
        new DropwizardAppRule<CovenConfiguration>(CovenApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    private static final Logger log = LoggerFactory.getLogger(AbstractResourceIT.class);

    static HttpAuthenticationFeature auth;

    static Client client;

    static ManagedDataSource ds;

    @BeforeClass
    public static void resetDatabase() throws Exception {
        auth = HttpAuthenticationFeature.universal("kevinhobbs@gmail.com", "unknown");
        client = new JerseyClientBuilder(RULE.getEnvironment())
            .build("test client");
        client.register(auth);
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 60000);

        ds = RULE.getConfiguration().getDataSourceFactory().build(
            RULE.getEnvironment().metrics(), "migrations");
        try (Connection connection = ds.getConnection()) {
            Liquibase migrator = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
            migrator.dropAll();
            migrator.update("");
        }
    }

    @Before
    public void clearTables() throws Exception {
        System.out.println("### Deleting all data from covens table");
        ds.getConnection().createStatement().execute("delete from covens");
    }

    @Before
    public void clearAuthenticationCache() {
        log.info("### Invalidating all authenticated users");
        ((CovenApplication) RULE.getApplication()).getCachingAuthenticator().invalidateAll();
    }

    protected WebTarget targetUrl(String path) {
        return client.target(buildUrl(path));
    }

    protected String buildUrl(String path) {
        return String.format("http://localhost:%d/app/" + path, RULE.getLocalPort());
    }
}
