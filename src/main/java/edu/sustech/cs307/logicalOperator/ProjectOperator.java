package edu.sustech.cs307.logicalOperator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.Collections;
import java.util.List;

public class ProjectOperator extends LogicalOperator {

    private final List<SelectItem<?>> selectItems;

    public ProjectOperator(LogicalOperator child, List<SelectItem<?>> selectItems) {
        super(Collections.singletonList(child));
        this.selectItems = selectItems;
    }

    public List<SelectItem<?>> getSelectItems() {
        return selectItems;
    }

    @Override
    public String toString() {
        return "ProjectOperator(selectItems=" + selectItems + ")\n ├── " + children.get(0);
    }

}
