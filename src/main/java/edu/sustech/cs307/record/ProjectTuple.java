package edu.sustech.cs307.record;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.value.Value;
import java.util.List;

/**
 * ProjectTuple 类用于表示一个投影元组，它从输入元组中提取指定列的值。
 * 该类扩展了 Tuple 类，并提供了获取列值和元组模式的方法。
 */
public class ProjectTuple extends Tuple {
    private final List<TabCol> schema;
    private final Tuple inputTuple;
    private TabCol[] tupleSchema;

    public ProjectTuple(Tuple inputTuple, List<TabCol> schema) {
        this.schema = schema;
        this.inputTuple = inputTuple;
    }

    /**
     * 根据给定的记录和列信息，从输入元组中获取对应的值。
     * 如果指定的列在投影列表中，则返回该列的值；否则返回 null。
     *
     * @param record 要查询的记录
     * @param tabCol 要获取值的列信息
     * @return 指定列的值，如果列不在投影列表中则返回 null
     */
    @Override
    public Value getValue(Record record, TabCol tabCol) {
        for (TabCol projectColumn : schema) {
            if (projectColumn.equals(tabCol)) {
                return inputTuple.getValue(record, tabCol); // Get value from input tuple
            }
        }
        // TODO: throw error
        return null; // Column not in projection list
    }

    /**
     * 获取当前元组的模式（Schema）。
     *
     * @return 返回一个包含列信息的 TabCol 数组，表示元组的结构。
     */
    @Override
    public TabCol[] getTupleSchema() {
        return tupleSchema;
    }
}
