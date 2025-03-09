package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.meta.TableMeta;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.value.Value;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.List;
import java.util.ArrayList;

public class InsertOperator implements PhysicalOperator {
    private final String data_file;
    private final List<String> columnNames;
    private final List<Value> values;
    private final DBManager dbManager;
    private int columnSize;
    public InsertOperator(String data_file, List<String> columnNames, List<Value> values, DBManager dbManager) {
        this.data_file = data_file;
        this.columnNames = columnNames;
        this.values = values;
        this.dbManager = dbManager;
        this.columnSize = columnNames.size();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void Begin() {
        try {
            String dataFile = data_file;
            var fileHandle = dbManager.getRecordManager().OpenFile(dataFile);

            // Serialize values to ByteBuf
            ByteBuf buffer = Unpooled.buffer();
            for (int i = 0;i < values.size();i ++) {
                buffer.writeBytes(values.get(i).ToByte());
                if(i != 0 && (i + 1) % columnSize == 0) {
                    fileHandle.InsertRecord(buffer);
                    buffer.clear();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to insert record: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void Next() {
        // TODO: Implement Next
    }

    @Override
    public Tuple Current() {
        // TODO: Implement Current
        return null;
    }

    @Override
    public void Close() {
        // TODO: Implement Close
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        return new ArrayList<>();
    }

    public void reset() {
        // nothing to do
    }

    public Tuple getNextTuple() {
        return null;
    }
}
