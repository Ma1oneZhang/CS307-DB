package edu.sustech.cs307.logicalOperator;

import edu.sustech.cs307.system.DBManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.update.UpdateSet;

import java.util.Collections;
import java.util.List;

public class LogicalUpdateOperator extends LogicalOperator {
    private final String tableName;
    private final List<UpdateSet> columns;
    private final List<Expression> expressions;
    private final DBManager dbManager;

    public LogicalUpdateOperator(DBManager dbManager, LogicalOperator child, String tableName, List<UpdateSet> columns,
            List<Expression> expressions) {
        super(Collections.singletonList(child));
        this.tableName = tableName;
        this.columns = columns;
        this.expressions = expressions;
        this.dbManager = dbManager;
    }

    public String getTableName() {
        return tableName;
    }

    public List<UpdateSet> getColumns() {
        return columns;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "UpdateOperator(table=" + tableName + ", columns=" + columns + ", expressions=" + expressions
                + ")\n ├── " + children.get(0);
    }
}
