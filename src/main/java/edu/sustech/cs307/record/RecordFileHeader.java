package edu.sustech.cs307.record;

import io.netty.buffer.ByteBuf;

public class RecordFileHeader {

    ByteBuf header;

    public int getRecordSize() {
        return header.getInt(0);
    }

    public void setRecordSize(int recordSize) {
         header.setInt(0, recordSize);
    }

    public int getNumberOfPages() {
        return header.getInt(4);
    }

    public void setNumberOfPages(int numberOfPages) {
        header.setInt(4, numberOfPages);
    }

    public int getNumberOfRecordsPrePage() {
        return header.getInt(8);
    }

    public void setNumberOfRecordsPrePage(int numberOfRecordsPrePage) {
        header.setInt(8, numberOfRecordsPrePage);
    }

    public int getFirstFreePage() {
        return header.getInt(12);
    }

    public void setFirstFreePage(int firstFreePage) {
        header.setInt(12, firstFreePage);
    }

    public int getBitMapSize() {
        return header.getInt(16);
    }

    public void setBitMapSize(int bitMapSize) {
        header.setInt(16, bitMapSize);
    }

    public RecordFileHeader(ByteBuf header){
        this.header = header;
    }
}
