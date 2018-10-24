package jooq.examples.oracle;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

public class InitializeOracleSettings {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
	static String password = "jooqdata";
	static String user = "jooqdata";
	static UniversalConnectionPoolManager ucpm;
	protected static PoolXADataSource pool;

	protected static void createPool() throws Exception {
	
		ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
	
		// ucpm.setLogLevel(Level.FINEST);
	
		pool = PoolDataSourceFactory.getPoolXADataSource(); // XA
		pool.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
		pool.setURL(url);
		pool.setUser(user);
		pool.setPassword(password);
		pool.setInitialPoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setFastConnectionFailoverEnabled(true);
	
		pool.setValidateConnectionOnBorrow(true);
	}

	public InitializeOracleSettings() {
		super();
	}

}