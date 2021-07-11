package jooq.examples.executor;

import jooq.examples.generated.tables.records.AuthorRecord;
import jooq.examples.oracle.InitializeOracleSettings;
import jooq.examples.tools.QueryPerformanceListener;
import jooq.examples.tools.QueryStatisticsListener;
import org.jooq.ExecuteType;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;

import static jooq.examples.generated.Tables.AUTHOR;

public class JooqForCRUD extends InitializeOracleSettings {

    public static void main(String[] args) throws Exception {
        createPool(); //initialize pool so datasource is initialized
        DataSourceConnectionProvider connectionProvider = new DataSourceConnectionProvider(getDataSource());

        Settings settings = new Settings();

        DefaultConfiguration config = new DefaultConfiguration();
        config.set(settings);
        config.setSQLDialect(SQLDialect.ORACLE20C);
        config.setConnectionProvider(connectionProvider);
        config.setExecuteListenerProvider(new DefaultExecuteListenerProvider[]{new DefaultExecuteListenerProvider(new QueryStatisticsListener())
                , new DefaultExecuteListenerProvider(new QueryPerformanceListener())});

        DefaultDSLContext dsl = new DefaultDSLContext(config);

        //Test whether select 1 from dual works;
        Result result = dsl.selectOne().fetch();
        System.out.println(result);
        System.out.println(result.getValue(0, "one")); //result.field(0) to get alias column name


        // Fetch an author
        AuthorRecord author = dsl.fetchOne(AUTHOR, AUTHOR.ID.eq(1));
        System.out.println(author);

        for (ExecuteType type : ExecuteType.values()) {
            System.out.printf("%s, %s \n", type.name(), QueryStatisticsListener.STATISTICS.get(type) + " executions");
        }

        destroyPool();
    }
}
