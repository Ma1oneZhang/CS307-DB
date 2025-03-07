package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.tuple.TableTuple;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.meta.TableMeta;
import edu.sustech.cs307.record.RecordFileHandle;
import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.record.RID;
import edu.sustech.cs307.record.RecordPageHandle;
import edu.sustech.cs307.record.BitMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SeqScanOperator implements PhysicalOperator {
    private String tableName;
    private DBManager dbManager;
    private TableMeta tableMeta;
    private RecordFileHandle fileHandle;
    private Record currentRecord;
    private TableTuple tupleType;
    private int currentPageNum;
    private int currentSlotNum;
    private int totalPages;
    private int recordsPerPage;
    private boolean isOpen = false;


    public SeqScanOperator(String tableName, DBManager dbManager) {
        this.tableName = tableName;
        this.dbManager = dbManager;
        try {
            this.tableMeta = dbManager.getMetaManager().getTable(tableName);
        } catch (DBException e) {
            // Handle exception properly, maybe log or rethrow
            e.printStackTrace();
        }
        this.tupleType = new TableTuple(tableName, tableMeta);
    }

    @Override
    public boolean hasNext() {
        if (!isOpen) return false;
        try {
            // Check if current page and slot are valid, and if there are more records
            if (currentPageNum <= totalPages) {
                while (currentPageNum <= totalPages) {
                    RecordPageHandle pageHandle = fileHandle.FetchPageHandle(currentPageNum);
                    while (currentSlotNum < recordsPerPage) {
                        if (BitMap.isSet(pageHandle.bitmap, currentSlotNum)) {
                            return true; // Found next record
                        }
                        currentSlotNum++;
                    }
                    currentPageNum++;
                    currentSlotNum = 0; // Reset slot num for new page
                }
            }
        } catch (DBException e) {
            e.printStackTrace(); // Handle exception properly
        }
        return false; // No more records
    }


    @Override
    public void Begin() {
        try {
            fileHandle = dbManager.getRecordManager().OpenFile(tableName);
            totalPages = fileHandle.getFileHeader().getNumberOfPages();
            recordsPerPage = fileHandle.getFileHeader().getNumberOfRecordsPrePage();
            currentPageNum = 1; // Start from first page
            currentSlotNum = 0; // Start from first slot
            isOpen = true;
        } catch (DBException e) {
            e.printStackTrace(); // Handle exception properly
            isOpen = false;
        }
    }

    @Override
    public void Next() {
        if (!isOpen) return;
        try {
            if (hasNext()) { // Advance to the next record
                RecordPageHandle pageHandle = fileHandle.FetchPageHandle(currentPageNum);
                RID rid = new RID(currentPageNum, currentSlotNum);
                currentRecord = fileHandle.GetRecord(rid);
                currentSlotNum++;
                if (currentSlotNum >= recordsPerPage) {
                    currentPageNum++;
                    currentSlotNum = 0;
                }
            } else {
                currentRecord = null;
            }
        } catch (DBException e) {
            e.printStackTrace(); // Handle exception properly
            currentRecord = null;
        }
    }


    @Override
    public Tuple Current() {
        if (!isOpen || currentRecord == null) {
            return null;
        }
        return new TableTuple(tableName, tableMeta); // Create new TableTuple for each record
    }

    @Override
    public void Close() {
        if (!isOpen) return;
        try {
            dbManager.getRecordManager().CloseFile(fileHandle);
        } catch (DBException e) {
            e.printStackTrace(); // Handle exception properly
        }
        fileHandle = null;
        currentRecord = null;
        isOpen = false;
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        return tableMeta.columns_list;
    }

    public Record getCurrentRecord() {
        return currentRecord; // Add getter for currentRecord for debugging/testing
    }
}
