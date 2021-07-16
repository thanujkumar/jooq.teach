Make sure schema is created as specified in schema.ddl before running any below command

>     mvn clean compile -Djooq.release=org.jooq.pro
>     mvn clean compile -Djooq.release=org.jooq.pro-java-11
>     mvn clean compile -Djooq.release=org.jooq
>     mvn clean compile -Djooq.release=org.jooq.trial

>     mvn clean package -Djooq.release=org.jooq.trial

### Structure
shared library at *jooq.examples.tools*

### Notes
[Thread Safety](https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/thread-safety/) - org.jooq.Configuration, and by consequence org.jooq.DSLContext, make no thread safety guarantees.