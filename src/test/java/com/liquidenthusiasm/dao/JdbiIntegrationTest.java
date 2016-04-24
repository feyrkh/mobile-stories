package com.liquidenthusiasm.dao;

import org.junit.After;
import org.junit.Before;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Configuration;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public abstract class JdbiIntegrationTest {

    private DBI dbi;

    private Handle handle;

    private Liquibase liquibase;

    private ObjectMapper objectMapper = new ObjectMapper();

    protected abstract DatabaseConfiguration getDatabaseConfiguration();

    protected abstract void setUpDataAccessObjects();

    @Before
    public void setUpDatabase() throws Exception {
        IntegrationTestConfiguration configuration = new IntegrationTestConfiguration(getDatabaseConfiguration());
        Environment environment = new Environment("test", objectMapper, null, null, null);
        dbi = new DBIFactory().build(environment, (PooledDataSourceFactory) configuration.getDatabaseConfiguration(), "test");
        handle = dbi.open();
        migrateDatabase();
        setUpDataAccessObjects();
    }

    @After
    public void tearDown() throws Exception {
        liquibase.dropAll();
        handle.close();
    }

    protected <SqlObjectType> SqlObjectType onDemandDao(Class<SqlObjectType> sqlObjectType) {
        return dbi.onDemand(sqlObjectType);
    }

    private void migrateDatabase() throws LiquibaseException {
        liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(handle.getConnection()));
        liquibase.update((String) null);
    }

    private class IntegrationTestConfiguration extends Configuration {

        private DatabaseConfiguration databaseConfiguration;

        public IntegrationTestConfiguration(DatabaseConfiguration databaseConfiguration) {
            this.databaseConfiguration = databaseConfiguration;
        }

        public DatabaseConfiguration getDatabaseConfiguration() {
            return databaseConfiguration;
        }
    }
}