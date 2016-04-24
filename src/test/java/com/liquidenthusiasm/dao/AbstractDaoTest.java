package com.liquidenthusiasm.dao;

import org.junit.BeforeClass;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class AbstractDaoTest {

    private static Environment env;

    protected static DBI dbi;

    private static Handle handle;

    private static Liquibase liquibase;

    @BeforeClass
    public static void setupClass() throws LiquibaseException {
        env = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null);
        dbi = new DBIFactory().build(env, getDataSourceFactory(), "test");
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
        dbi.registerMapper(new JodaDateTimeMapper(Optional.absent()));
        handle = dbi.open();
        liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(handle.getConnection()));
        resetDb();
    }

    public static void resetDb() {
        try {
            liquibase.dropAll();
            liquibase.update("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static DataSourceFactory getDataSourceFactory() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass("org.h2.Driver");
        dataSourceFactory.setUrl("jdbc:h2:mem:testDb");
        dataSourceFactory.setUser("sa");
        dataSourceFactory.setPassword("");
        return dataSourceFactory;
    }

    public static DBI getDbi() {
        return dbi;
    }

    public static Environment getEnvironment() {
        return env;
    }

}
