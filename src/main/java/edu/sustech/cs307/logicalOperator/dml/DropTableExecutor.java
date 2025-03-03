package edu.sustech.cs307.logicalOperator.dml;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.system.DBManager;
import net.sf.jsqlparser.statement.drop.Drop;
import org.pmw.tinylog.Logger;

public class DropTableExecutor implements DMLExecutor {
    Drop dropStmt;
    DBManager dbManager;

    public DropTableExecutor(Drop drop, DBManager dbManager) {
        this.dropStmt = drop;
        this.dbManager = dbManager;
    }

    @Override
    public void execute() throws DBException {
        String table = dropStmt.getName().getName();
        dbManager.dropTable(table);
        Logger.info("Dropped table: {}", table);
    }

}
