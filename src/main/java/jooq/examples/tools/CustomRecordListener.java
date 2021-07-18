package jooq.examples.tools;

import org.jooq.RecordContext;
import org.jooq.impl.DefaultRecordListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Place to override/manipulate values, TODO can this be used to set created_ts and version when insert statement is executed?
public class CustomRecordListener extends DefaultRecordListener {
    private static final Logger log = LoggerFactory.getLogger(CustomRecordListener.class);

    @Override
    public void loadStart(RecordContext ctx) {
        log.info("=========loadStart========");
        super.loadStart(ctx);
    }

    @Override
    public void loadEnd(RecordContext ctx) {
        log.info("=========loadEnd========");
        super.loadEnd(ctx);
    }

    @Override
    public void insertStart(RecordContext ctx) {
        log.info("=========insertStart========");
        super.insertStart(ctx);
    }

    @Override
    public void insertEnd(RecordContext ctx) {
        log.info("=========insertEnd========");
        super.insertEnd(ctx);
    }

    @Override
    public void storeStart(RecordContext ctx) {
        log.info("=========storeStart========");
        super.storeStart(ctx);
    }

    @Override
    public void storeEnd(RecordContext ctx) {
        log.info("=========storeEnd========");
        super.storeEnd(ctx);
    }
    //Lot more
}