package edu.sustech.cs307.logicalOperator;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;
import edu.sustech.cs307.meta.TabCol;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogicalProjectOperator extends LogicalOperator {

    private final List<SelectItem<?>> selectItems;
    private final LogicalOperator input;

    public LogicalProjectOperator(LogicalOperator child, List<SelectItem<?>> selectItems) {
        super(Collections.singletonList(child));
        this.input = child;
        this.selectItems = selectItems;
    }

    public LogicalOperator getInput() {
        return input;
    }

    public List<TabCol> getOutputSchema() throws DBException {
        List<TabCol> outputSchema = new ArrayList<>();
        for (SelectItem<?> selectItem : selectItems) {
            if(selectItem.getExpression() instanceof Column column){
                outputSchema.add(new TabCol(column.getTableName(), column.getColumnName()));
            } else if(selectItem.getExpression() instanceof AllColumns column) {
                outputSchema.add(new TabCol("*", "*"));
            }
            else {
                throw new DBException(ExceptionTypes.NotSupportedOperation(selectItem.getExpression()));
            }
        }
        return outputSchema;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String nodeHeader = "ProjectOperator(selectItems=" + selectItems + ")";
        String[] childLines = input.toString().split("\\R");

        // 当前节点
        sb.append(nodeHeader);

        // 子节点处理
        if (childLines.length > 0) {
            sb.append("\n└── ").append(childLines[0]);
            for (int i = 1; i < childLines.length; i++) {
                sb.append("\n    ").append(childLines[i]);
            }
        }

        return sb.toString();
    }

}
