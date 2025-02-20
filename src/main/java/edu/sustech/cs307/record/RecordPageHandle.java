package edu.sustech.cs307.record;

import edu.sustech.cs307.storage.Page;
import io.netty.buffer.ByteBuf;


public class RecordPageHandle {
    public final RecordFileHeader fileHdr;
    public final Page page;
    public final RecordPageHeader pageHdr;
    public final ByteBuf bitmap;
    public final ByteBuf slots;

    public RecordPageHandle(RecordFileHeader fileHdr, Page page) {
        this.fileHdr = fileHdr;
        this.page = page;
        this.pageHdr = new RecordPageHeader(page.data.slice(0, RecordPageHeader.SIZE));
        this.bitmap = page.data.slice(RecordPageHeader.SIZE, fileHdr.getBitMapSize());
        this.slots = page.data.slice(RecordPageHeader.SIZE + fileHdr.getBitMapSize(), Page.DEFAULT_PAGE_SIZE - (RecordPageHeader.SIZE + fileHdr.getBitMapSize()));
    }

    public ByteBuf getSlot(int slotNo) {
        return slots.slice(slotNo * fileHdr.getRecordSize(), fileHdr.getRecordSize());
    }
}

