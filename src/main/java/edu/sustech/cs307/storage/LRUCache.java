package edu.sustech.cs307.storage;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    class DLinkedNode {
        long key;
        long value;
        DLinkedNode prev;
        DLinkedNode next;
    }

    private final long capacity;
    private final Map<Long, DLinkedNode> cache = new HashMap<>();
    private DLinkedNode head, tail;

    public LRUCache(long capacity) {
        this.capacity = capacity;
        head = new DLinkedNode();
        tail = new DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public long get(long key) {
        DLinkedNode node = cache.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(long key, long value) {
        DLinkedNode node = cache.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            DLinkedNode newNode = new DLinkedNode();
            newNode.key = key;
            newNode.value = value;
            cache.put(key, newNode);
            addNode(newNode);
            if (cache.size() > capacity) {
                DLinkedNode tail = popTail();
                cache.remove(tail.key);
            }
        }
    }

    private void addNode(DLinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(DLinkedNode node) {
        removeNode(node);
        addNode(node);
    }

    private DLinkedNode popTail() {
        DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }
}