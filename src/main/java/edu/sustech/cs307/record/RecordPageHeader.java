package edu.sustech.cs307.record;

import io.netty.buffer.ByteBuf;

public class RecordPageHeader {
    static public int SIZE = 8;
    static public final int NO_NEXT_FREE_PAGE = 0x7fffffff;

    public ByteBuf data;

    public int getNextFreePageNo() {
        return data.getInt(0);
    }

    public void setNextFreePageNo(int pageNo) {
        data.setInt(0, pageNo);
    }

    public int getNumberOfRecords() {
        return data.getInt(4);
    }

    public void setNumberOfRecords(int num) {
        data.setInt(4, num);
    }

    public RecordPageHeader(ByteBuf data) {
        this.data = data;
    }
}
