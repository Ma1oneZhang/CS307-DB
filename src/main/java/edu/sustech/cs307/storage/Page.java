package edu.sustech.cs307.storage;

public class Page {
    public final static int DEFAULT_PAGE_SIZE = 4 * 1024;
    public byte[] data;
    public PagePosition position = new PagePosition("null", 0);
    public boolean dirty;
    public int pin_count = 0;

    public Page() {
        data = new byte[DEFAULT_PAGE_SIZE];
    }
}
