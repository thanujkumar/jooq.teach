package jooq.examples.spring.jdbctemplate;

import oracle.ucp.UniversalConnectionPoolAdapter;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.*;
import org.springframework.context.event.ContextClosedEvent;

public class ContextInitializeAndShutdownListener implements ApplicationContextAware, ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ContextInitializeAndShutdownListener.class);
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Application context initialized {}", applicationContext);
        context = applicationContext;
        try {
            UniversalConnectionPoolManager poolManager = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
            poolManager.createConnectionPool((UniversalConnectionPoolAdapter) applicationContext.getBean("dataSource"));
            poolManager.startConnectionPool(((PoolDataSource) applicationContext.getBean("dataSource")).getConnectionPoolName());
            log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.info("++++++++++++++++++++"+poolManager.getConnectionPool(((PoolDataSource) applicationContext.getBean("dataSource")).getConnectionPoolName()).getStatistics());
            log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (UniversalConnectionPoolException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextClosedEvent) {
            UniversalConnectionPoolManager ucpm = context.getBean("ucpm", UniversalConnectionPoolManager.class);
            try {
                for (String poolName : ucpm.getConnectionPoolNames()) {
                    log.info(ucpm.getConnectionPool(poolName).getStatistics().toString());
                    ucpm.destroyConnectionPool(poolName);
                }
            } catch (UniversalConnectionPoolException e) {
                log.error("error while destroying oracle ucp ", e);
            }
        }
    }
}
