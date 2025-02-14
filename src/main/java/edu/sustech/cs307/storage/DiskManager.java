package edu.sustech.cs307.storage;

public class DiskManager {
    private String currentDir;
    private LRUCache lru;



    public DiskManager(String path, long capacity) {
        currentDir = path;
        lru = new LRUCache(capacity);
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public byte[] readFile(String filename, long offset, long length){
        byte[] fileData = null;

    }


}
