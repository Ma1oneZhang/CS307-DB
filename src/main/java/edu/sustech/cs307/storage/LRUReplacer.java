package edu.sustech.cs307.storage;

import java.util.*;

public class LRUReplacer {

    private final int maxSize;
    private final Set<Integer> pinnedFrames = new HashSet<>();
    private final Set<Integer> LRUHash = new HashSet<>();
    private final LinkedList<Integer> LRUList = new LinkedList<>();

    public LRUReplacer(int numPages) {
        this.maxSize = numPages;
    }

    // 从LRU中移除最不常用的页面
    public int Victim() {
        if (LRUList.isEmpty()) {
            return -1;
        } else {
            LRUHash.remove(LRUList.getFirst());
            return LRUList.removeFirst();
        }
    }

    // 固定指定frame
    public void Pin(int frameId) {
        // already been pinned
        if (!LRUHash.contains(frameId)) {
            if (!pinnedFrames.contains(frameId) ) {
                if (pinnedFrames.size() + LRUList.size() == maxSize) {
                    throw new RuntimeException("REPLACER IS FULL");
                } else {
                    pinnedFrames.add(frameId);
                    return;
                }
            }
        }
        LRUList.remove(Integer.valueOf(frameId));
        LRUHash.remove(frameId);
        pinnedFrames.add(frameId);
    }

    // 取消固定，允许frame被淘汰
    public void Unpin(int frameId) {
        // already unpinned
        if (LRUHash.contains(frameId)) {
            return; // 如果frame已经在缓存中，不做任何操作
        }
        // not unpin yet
        if (!pinnedFrames.contains(frameId)) {
            throw new RuntimeException("UNPIN PAGE NOT FOUND");
        } else {
            pinnedFrames.remove(frameId);
        }
        LRUList.addLast(frameId); // 添加到链表末尾
        LRUHash.add(frameId); // 将frame加入哈希表
    }

    // 获取当前LRU缓存中的可淘汰页面数量
    public int size() {
        return LRUList.size() + pinnedFrames.size();
    }
}