package edu.sustech.cs307.tuple;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.meta.TableMeta;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.value.ValueType;

import java.util.ArrayList;

import edu.sustech.cs307.meta.ColumnMeta;

import io.netty.buffer.ByteBuf;

public class TableTuple extends Tuple {
    private String tableName;
    private TableMeta tableMeta;

    public TableTuple(String tableName, TableMeta tableMeta) {
        this.tableName = tableName;
        this.tableMeta = tableMeta;
    }

    @Override
    public Value getValue(Record record, TabCol tabCol) {
        // TODO: throw a exception
        if (!tabCol.getTableName().equals(tableName)) {
            return null;
        }
        ColumnMeta columnMeta = tableMeta.getColumnMeta(tabCol.getColumnName());
        if (columnMeta == null) {
            return null;
        }
        int offset = columnMeta.getOffset();
        int len = columnMeta.getLen();
        // Use GetColumnValue to get the value based on offset and len
        ByteBuf columnValueBuf = record.GetColumnValue(offset, len); // Use the record passed to getValue
        // Convert ByteBuf to Value (assuming you have a method for this)
        Value value = convertByteBufToValue(columnValueBuf, columnMeta.type);
        return value;
    }

    private Value convertByteBufToValue(ByteBuf byteBuf, ValueType columnType) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String valueStr = new String(bytes);

        if (columnType == ValueType.INTEGER) {
            return new Value((long) Integer.parseInt(valueStr.trim()));
        } else if (columnType == ValueType.CHAR) {
            return new Value(valueStr.trim());
        } else if (columnType == ValueType.FLOAT) {
            return new Value((double) Double.parseDouble(valueStr.trim()));
        } else {
            // TODO: throw an exception
            return null; // Or throw an exception for unsupported types
        }
    }

    @Override
    public TabCol[] getTupleSchema() {
        ArrayList<TabCol> result = new ArrayList<TabCol>();
        // [this.tableMeta.getColumns().size()];
        this.tableMeta.getColumns().values().stream().forEach(columnMeta -> {
            TabCol tabCol = new TabCol(columnMeta.tableName, columnMeta.name);
            result.add(tabCol);
        });
        return (TabCol[]) result.toArray();

    }
}
