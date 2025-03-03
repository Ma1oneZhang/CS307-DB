package edu.sustech.cs307.tuple;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.value.Value;

/**
 * JoinTuple 类表示两个元组的连接结果。
 * 它包含两个元组（leftTuple 和 rightTuple）以及连接后的列信息（tabCol）。
 */
public class JoinTuple extends Tuple {
    private Tuple leftTuple;
    private Tuple rightTuple;
    private TabCol[] tupleSchema;

    public JoinTuple(Tuple leftTuple, Tuple rightTuple, TabCol[] tabCol) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
        this.tupleSchema = tabCol;
    }

    /**
     * 获取指定记录中对应列的值。
     * 首先尝试从左侧元组中获取值，如果左侧值为 null，则从右侧元组中获取值。
     *
     * @param record 要查询的记录
     * @param tabCol 要获取值的列
     * @return 返回对应列的值，如果两侧元组均无值，则返回 null
     */
    @Override
    public Value getValue(Record record, TabCol tabCol) {
        Value leftValue = leftTuple.getValue(record, tabCol);
        if (leftValue != null) {
            return leftValue;
        }
        return rightTuple.getValue(record, tabCol);
    }

    /**
     * 获取当前元组的模式（列信息）。
     *
     * @return 返回一个包含列信息的 TabCol 数组。
     */
    @Override
    public TabCol[] getTupleSchema() {
        return tupleSchema;
    }
}
