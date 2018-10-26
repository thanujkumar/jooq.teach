package jooq.examples.spring.javaconfig;

import org.jooq.Transaction;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CustomTransactionProvider implements TransactionProvider {

	private static final JooqLogger log = JooqLogger.getLogger(CustomTransactionProvider.class);

	private PlatformTransactionManager txMgr;

	CustomTransactionProvider(PlatformTransactionManager managerTx) {
		this.txMgr = managerTx;
	}

	@Override
	public void begin(TransactionContext ctx) throws DataAccessException {
		log.info("Begin transaction");
		System.out.println("-----------------------------------begin----------------------------------------------------------++");

		// This TransactionProvider behaves like jOOQ's DefaultTransactionProvider,
		// which supports nested transactions using Savepoints
		TransactionStatus tx = txMgr
				.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NESTED));
		ctx.transaction(new CustomTransaction(tx));
	}

	@Override
	public void commit(TransactionContext ctx) {
		log.info("commit transaction");
		System.out.println("---------------------------------commit------------------------------------------------------------++");

		txMgr.commit(((CustomTransaction) ctx.transaction()).tx);
	}

	@Override
	public void rollback(TransactionContext ctx) {
		log.info("rollback transaction");
		System.out.println("-----------------------------------rollback----------------------------------------------------------++");

		txMgr.rollback(((CustomTransaction) ctx.transaction()).tx);
	}

	class CustomTransaction implements Transaction {
		final TransactionStatus tx;

		CustomTransaction(TransactionStatus tx) {
			System.out.println("---------------------------------CustomTransaction------------------------------------------------------------++");
			this.tx = tx;
		}
	}

}
