package jooq.examples.spring.javaconfig;

import jooq.examples.tools.QueryPerformanceListener;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Level;

//https://www.petrikainulainen.net/programming/jooq/using-jooq-with-spring-configuration/
//TODO - This is not recommended approach - read above link
@Configuration
@ComponentScan("jooq.examples.spring.javaconfig")
@EnableTransactionManagement
@PropertySource("classpath:jooq-spring-nonxa.properties")
public class PersistenceContext {

    @Autowired
    Environment env;

    @Bean
    public DataSource dataSource() throws SQLException {
        //PoolXADataSource pool = PoolDataSourceFactory.getPoolXADataSource(); // XA
        //pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");//XA
        PoolDataSource pool = PoolDataSourceFactory.getPoolDataSource(); //non XA
        pool.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");//non XA
        pool.setURL(env.getRequiredProperty("db.url"));
        pool.setUser(env.getRequiredProperty("db.username"));
        pool.setPassword(env.getRequiredProperty("db.password"));
        pool.setInitialPoolSize(env.getProperty("db.initial.poolsize", int.class, 5));
        pool.setMaxPoolSize(env.getProperty("db.max.poolsize", int.class, 10));
        pool.setFastConnectionFailoverEnabled(true);

        pool.setValidateConnectionOnBorrow(true);

        return pool;
    }

    @Bean
    public UniversalConnectionPoolManager universalConnectionPoolManager() throws UniversalConnectionPoolException {
        UniversalConnectionPoolManager ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
        ucpm.setLogLevel(Level.INFO);
        return ucpm;
    }

    @Bean
    public TransactionAwareDataSourceProxy transactionAwareDataSource(DataSource dataSource) throws SQLException {
        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) throws SQLException {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) throws SQLException {
        return new DataSourceConnectionProvider(transactionAwareDataSource(dataSource));
    }

    @Bean
    public JooQToSpringExceptionTransformer jooqToSpringExceptionTransformer() {
        return new JooQToSpringExceptionTransformer();
    }

   //  Configure jOOQ's TransactionProvider as a proxy to Spring's transaction manager
    @Bean
    public TransactionProvider jooqTransactionProvider(DataSource dataSource) throws SQLException {
        return new CustomTransactionProvider(transactionManager(dataSource));
    }

    @Bean
    public DefaultConfiguration configuration(DataSource dataSource) throws SQLException {
        DefaultConfiguration jooqConfig = new DefaultConfiguration();
        jooqConfig.setConnectionProvider(connectionProvider(dataSource));
        jooqConfig.setTransactionProvider(jooqTransactionProvider(dataSource));
        jooqConfig.setSQLDialect(SQLDialect.valueOf(env.getProperty("jooq.sql.dialect", SQLDialect.ORACLE20C.getName())));
        jooqConfig.setExecuteListenerProvider(new DefaultExecuteListenerProvider(jooqToSpringExceptionTransformer()),
                new DefaultExecuteListenerProvider(new QueryPerformanceListener()));


        return jooqConfig;
    }

    @Bean
    public DefaultDSLContext dsl(DataSource dataSource) throws SQLException {
        return new DefaultDSLContext(configuration(dataSource));
    }


}
