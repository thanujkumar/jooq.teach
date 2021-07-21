<p align="center">
  <a href="https://github.com/thanujkumar/jooq.teach/actions?query=workflow%3Amaster_ci+branch%3Amaster">
    <img alt="Github Actions Build Status" src="https://img.shields.io/github/workflow/status/thanujkumar/jooq.teach/CI?label=master&style=flat-square%22"></a>
</p>

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