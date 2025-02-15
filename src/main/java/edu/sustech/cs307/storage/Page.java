package edu.sustech.cs307.storage;

public class Page {
    public final static int DEFAULT_PAGE_SIZE = 4 * 1024;
    public byte[] data;
    public String filename;
    public long file_offset;

    public Page() {
        data = new byte[DEFAULT_PAGE_SIZE];
        filename = "null";
    }
}
