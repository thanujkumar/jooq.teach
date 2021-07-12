package jooq.examples.spring.jdbctemplate;

import org.jooq.Transaction;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class JdbcTemplateTransactionProvider implements TransactionProvider {

    //Just delegates to underlying sl4j
    private static final JooqLogger log = JooqLogger.getLogger(JdbcTemplateTransactionProvider.class);


    @Autowired
    DataSourceTransactionManager transactionManager;

    @Override
    public void begin(TransactionContext ctx) throws DataAccessException {
        log.info("**Begin transaction**");
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.withDefaults()));
        ctx.transaction(new JdbcTemplateTransaction(transactionStatus));
    }

    @Override
    public void commit(TransactionContext ctx) throws DataAccessException {
        log.info("**commit transaction**");
        transactionManager.commit(((JdbcTemplateTransaction) ctx.transaction()).tx);
    }

    @Override
    public void rollback(TransactionContext ctx) throws DataAccessException {
        log.info("**rollback transaction**");
        transactionManager.rollback(((JdbcTemplateTransaction) ctx.transaction()).tx);
    }
}

class JdbcTemplateTransaction implements Transaction {
    final TransactionStatus tx;

    JdbcTemplateTransaction(TransactionStatus tx) {
        this.tx = tx;
    }
}