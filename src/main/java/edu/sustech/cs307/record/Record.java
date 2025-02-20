package edu.sustech.cs307.record;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Record {
    ByteBuf  data;
    int      size;

    public Record(ByteBuf data, int size){
        this.data = data;
        this.size = size;
    }

    public Record() {
        this.data = null;
        this.size = 0;
    }

    public Record(Record record) {
        this.data = Unpooled.buffer(record.data.capacity()).writeBytes(record.data);
        this.size = record.size;
    }

    public Record(int size){
        this.data = Unpooled.buffer(size);
        this.size = size;
    }

    public Record(ByteBuf data){
        this.data = data;
        this.size = data.capacity();
    }

    public void SetData(ByteBuf data){
        this.data = data.copy();
    }

    public void Deserialize(ByteBuf data){
        this.data = data.copy();
        this.size = data.capacity();
    }

    public ByteBuf Serialize(){
        return this.data.copy();
    }

    public ByteBuf GetColumnValue(int offset, int len) {
        return this.data.slice(offset, len);
    }

    public void SetColumnValue(int offset, byte[] columnValue) {
        this.data.writerIndex(offset).writeBytes(columnValue);
        this.data.resetWriterIndex();
    }

    public ByteBuf getReadOnlyData() {
        return this.data.asReadOnly();
    }
}
