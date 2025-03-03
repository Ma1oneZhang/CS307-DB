package edu.sustech.cs307.logicalOperator.dml;

import edu.sustech.cs307.system.DBManager;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;

public class ShowTableExecutor implements DMLExecutor {

    ShowTablesStatement showTablesStatement;
    DBManager dbManager;
    public ShowTableExecutor(ShowTablesStatement showTablesStatement, DBManager dbManager){
        this.showTablesStatement = showTablesStatement;
        this.dbManager = dbManager;
    }
    @Override
    public void execute() {
        dbManager.showTables();
    }
    
}
