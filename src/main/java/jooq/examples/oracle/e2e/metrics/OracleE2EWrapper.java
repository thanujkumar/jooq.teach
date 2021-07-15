package jooq.examples.oracle.e2e.metrics;

//https://docs.oracle.com/cd/B14117_01/java.101/b10979/endtoend.htm
//https://github.com/thanujkumar/oracle.end.to.end/blob/master/src/main/java/ora/end/to/end/OracleE2EMetrics_Plain_Connection.java
//19c - https://docs.oracle.com/en/database/oracle/oracle-database/19/jjdbc/JDBC-DMS-Metrics.html#GUID-601B7FA6-A11A-4927-A0AD-77AB6F5CF896
//21c - https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/JDBC-DMS-Metrics.html#GUID-601B7FA6-A11A-4927-A0AD-77AB6F5CF896
/*
Note:There is another kind of metrics called end-to-end metrics. End-to-end metrics are used for tagging application
activity from the entry into the application code through JDBC to the database and back.
JDBC supports the following end-to-end metrics:

Action
ClientId
ExecutionContextId
Module
State

For earlier releases, to work with the preceding metrics, you could use the setEndToEndMetrics and
getEndToEndMetrics methods of the oracle.jdbc.OracleConnection interface. However, starting from Oracle Database
12c Release 1 (12.1), these methods have been deprecated. Oracle recommends to use the setClientInfo and getClientInfo
methods instead of the setEndToEndMetrics and getEndToEndMetrics methods.

In Oracle Database 10g, Oracle Java Database Connectivity (JDBC) supports end-to-end metrics.
In Oracle Database 12c Release 1 (12.1), an application can set the end-to-end metrics directly only when it does
not use a DMS-enabled JAR files. But, if the application uses a DMS-enabled JAR file, the end-to-end metrics can be
set only through DMS.

WARNING:Oracle strongly recommends using DMS metrics, if the application uses a DMS-enabled JAR file
 */
//https://asktom.oracle.com/pls/apex/f?p=100:11:0::::P11_QUESTION_ID:865497961356
/* To flush sql from shared library
 >  alter system flush shared_pool;
 >  select a.name, b.value from v$statname a, v$mystat b where a.statistic# = b.statistic# and lower(a.name) like '%cursor ca%';
 >  select * from v$open_cursor;
 */

/* To disable, will be overridden when statement cache is enabled from UCP settings setMaxStatement (implicit and explicit caching)
 >  alter system set session_cached_cursors=0 scope=spfile;
 >  startup force;
 */

//https://asanga-pradeep.blogspot.com/2014/02/session-cached-cursors-and-jdbc.html
/*
 > select module, action, elapsed_time, cpu_time, executions, fetches, parse_calls, optimizer_cost, LAST_LOAD_TIME, sql_text from v$sql where module='JOOQDATA';
 > select * from V$ACTIVE_SESSION_HISTORY where module='JOOQDATA';
 > show parameters session;
*/


import oracle.jdbc.internal.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public class OracleE2EWrapper extends DelegatingDataSource {

    private static final Logger log = LoggerFactory.getLogger(OracleE2EWrapper.class);
    //OCSID.<all properties>
    private static String OracleKeyName = OracleConnection.OCSID_NAMESPACE + OracleConnection.CLIENT_INFO_KEY_SEPARATOR;

    //describe v$sql has sizes
    //https://docs.oracle.com/cd/B14117_01/java.101/b10979/endtoend.htm
    private static final int ACTION_LENGTH = 64; //32
    private static final int CLIENTID_LENGTH = 64;
    private static final int ECID_LENGTH = 64;
    private static final int MODULE_LENGTH = 64; //48


    public OracleE2EWrapper(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setEndToEndParameters(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setEndToEndParameters(connection);
        return connection;
    }

    private void setEndToEndParameters(Connection connection) {
        //Convention that all JUnit5 test method ends with TestMe
        StackWalker walker = StackWalker.getInstance();
        String Class_Method = "UNKNOWN";

//        Optional<String> foundName = walker.walk(frames ->
//                frames.map(StackWalker.StackFrame::getMethodName)
//                        .filter(s -> s.toLowerCase().contains("testme")).findFirst());

        Optional<StackTraceElement> traceElement = walker.walk(frames -> frames.map(StackWalker.StackFrame::toStackTraceElement)
                .filter(s -> s.toString().toLowerCase().contains("testme")).findFirst());
        traceElement.get().getMethodName();

        if (traceElement.isPresent()) {
            Class_Method = getClassAndMethod(traceElement.get());
        }
        try {
            // oracle.jdbc.internal.OracleConnection
            OracleConnection oracleConnection = connection.unwrap(OracleConnection.class);
            oracleConnection.setClientInfo(OracleKeyName + OracleConnection.OCSID_MODULE_KEY, "JOOQDATA");
            oracleConnection.setClientInfo(OracleKeyName + OracleConnection.OCSID_ACTION_KEY, left(Class_Method, ACTION_LENGTH));
        } catch (SQLException e) {
            log.error("Unable to set oracle e2e metrics", e);
        }
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        } else if (len < 0) {
            return "";
        } else {
            return str.length() <= len ? str : str.substring(0, len);
        }
    }

    private String getClassAndMethod(StackTraceElement element) {
        String methodName = element.getMethodName();
        String className = element.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        return className + "." + methodName;
    }
}
