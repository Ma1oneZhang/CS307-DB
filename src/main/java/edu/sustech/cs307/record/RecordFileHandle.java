package edu.sustech.cs307.record;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.storage.BufferPool;
import edu.sustech.cs307.storage.DiskManager;
import edu.sustech.cs307.storage.Page;
import edu.sustech.cs307.storage.PagePosition;
import io.netty.buffer.ByteBuf;

public class RecordFileHandle {
    DiskManager diskManager;
    BufferPool bufferPool;
    String filename;
    RecordFileHeader fileHeader;

    public RecordFileHandle(DiskManager diskManager, BufferPool bufferPool, String filename, RecordFileHeader header) throws DBException {
        this.diskManager = diskManager;
        this.bufferPool = bufferPool;
        this.filename = filename;

        // Read the header from the file
        // We just wanna get the number of pages
        // just let page free
        Page page = new Page();
        diskManager.ReadPage(page, filename, 0, Page.DEFAULT_PAGE_SIZE);
        fileHeader = new RecordFileHeader(page.data);

        diskManager.filePages.put(filename, fileHeader.getNumberOfPages());
    }

    public RecordFileHeader getFileHeader() {
        return fileHeader;
    }

    public String getFilename() {
        return filename;
    }

    public boolean IsRecord(RID rid) throws DBException {
        RecordPageHandle page_handle = FetchPageHandle(rid.pageNum);
        return BitMap.isSet(page_handle.bitmap, rid.slotNum);
    }

    public Record GetRecord(RID rid) throws DBException {
        RecordPageHandle handle = FetchPageHandle(rid.pageNum);
        Record record = new Record(handle.getSlot(rid.slotNum));
        bufferPool.unpin_page(handle.page.position, false);
        return record;
    }

    public RID InsertRecord(ByteBuf buf) throws DBException {
        RecordPageHandle pageHandle = create_page_handle();
        int slotNum = BitMap.firstBit(false, pageHandle.bitmap, fileHeader.getNumberOfRecordsPrePage());
        // array must less than the number of records per page
        if (slotNum == fileHeader.getNumberOfRecordsPrePage()) {
            throw new RuntimeException("THE FILE IS DAMAGED, PLEASE DELETE THE DIR AND RUN IT AGAIN");
        }

        ByteBuf slot = pageHandle.getSlot(slotNum).clear();
        slot.writeBytes(buf, 0, fileHeader.getRecordSize());
        BitMap.set(pageHandle.bitmap, slotNum);
        pageHandle.pageHdr.setNumberOfRecords(pageHandle.pageHdr.getNumberOfRecords() + 1);

        if (pageHandle.pageHdr.getNumberOfRecords() == fileHeader.getNumberOfRecordsPrePage()) {
            fileHeader.setFirstFreePage(pageHandle.pageHdr.getNextFreePageNo());
        }

        bufferPool.unpin_page(pageHandle.page.position, true);

        return new RID(pageHandle.page.getPageID(), slotNum);
    }

    public void DeleteRecord(RID rid) throws DBException {
        RecordPageHandle pageHandle = FetchPageHandle(rid.pageNum);
        BitMap.reset(pageHandle.bitmap, rid.slotNum);
        pageHandle.pageHdr.setNumberOfRecords(pageHandle.pageHdr.getNumberOfRecords() - 1);
        bufferPool.unpin_page(pageHandle.page.position, true);
    }

    public void UpdateRecord(RID rid, ByteBuf buf) throws DBException {
        RecordPageHandle pageHandle = FetchPageHandle(rid.pageNum);
        ByteBuf slot = pageHandle.getSlot(rid.slotNum);
        slot.clear();
        slot.writeBytes(buf);
        bufferPool.unpin_page(pageHandle.page.position, true);
    }

    public RecordPageHandle FetchPageHandle(int pageId) throws DBException {
        if (pageId > fileHeader.getNumberOfPages()) {
            throw new RuntimeException(String.format("%s: pageId %d is out of range", filename, pageId));
        }
        PagePosition pagePosition = new PagePosition(filename, pageId * Page.DEFAULT_PAGE_SIZE);
        Page page = bufferPool.FetchPage(pagePosition);
        if (page == null) {
            throw new RuntimeException(String.format("%s: pageId %d is out of range", filename, pageId));
        }
        return new RecordPageHandle(fileHeader, page);
    }

    public RecordPageHandle CreateNewPageHandle() throws DBException {
        Page newPage = bufferPool.NewPage(filename);
        RecordPageHandle pageHandle = new RecordPageHandle(fileHeader, newPage);

        // Initialize the page
        BitMap.init(pageHandle.bitmap);
        pageHandle.pageHdr.setNumberOfRecords(0);
        pageHandle.pageHdr.setNextFreePageNo(RecordPageHeader.NO_NEXT_FREE_PAGE);

        // Update the file header
        fileHeader.setNumberOfPages(fileHeader.getNumberOfPages() + 1);
        fileHeader.setFirstFreePage(newPage.getPageID());

        return pageHandle;
    }



    private RecordPageHandle create_page_handle() throws DBException {
        if (fileHeader.getFirstFreePage() == RecordPageHeader.NO_NEXT_FREE_PAGE) {
            return CreateNewPageHandle();
        } else {
            return FetchPageHandle(fileHeader.getFirstFreePage());
        }
    }

    private void release_page_handle(RecordPageHandle handle) {
        handle.pageHdr.setNextFreePageNo(fileHeader.getFirstFreePage());
        fileHeader.setFirstFreePage(handle.page.getPageID());
    }
}
