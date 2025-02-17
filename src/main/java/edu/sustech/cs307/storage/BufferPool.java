package edu.sustech.cs307.storage;

import edu.sustech.cs307.exception.DBException;

import java.util.*;

public class BufferPool {

    private final int poolSize;

    // frames
    private ArrayList<Page> pages;

    // PagePosition -> frame_id
    private HashMap<PagePosition, Integer> pageMap;

    private LinkedList<Integer> freeList;

    private DiskManager diskManager;

    private LRUReplacer lruReplacer;

    public BufferPool(int pool_size, DiskManager diskManager) {
        this.poolSize = pool_size;
        this.lruReplacer = new LRUReplacer(pool_size);
        this.freeList = new LinkedList<>();
        for (int i = 0;i < pool_size; i++) {
            freeList.add(i);
        }
        this.pageMap = new HashMap<>();
        this.pages = new ArrayList<>();
        for (int i = 0;i < pool_size; i++) {
            Page page = new Page();
            pages.add(page);
        }
        this.diskManager = diskManager;
    }

    public static void MarkPageDirty(Page page) {
        page.dirty = true;
    }

    /**
     * @description: 从buffer pool获取需要的页。
     *              如果页表中存在position（说明该page在缓冲池中），并且pin_count++。
     *              如果页表不存在position（说明该page在磁盘中），则找缓冲池victim page，将其替换为磁盘中读取的page，pin_count置1。
     * @return {Page*} 若获得了需要的页则将其返回，否则返回nullptr
     * @param {PageId} page_id 需要获取的页的PageId
     */
    public Page FetchPage(PagePosition position) throws DBException {
        Integer frame_id = pageMap.get(position);
        if (frame_id != null) {
            Page page = pages.get(frame_id);
            page.pin_count ++;
            if (page.pin_count == 1) {
                lruReplacer.Pin(frame_id);
            }
            return page;
        } else {
            frame_id = find_victim_page();
            if (frame_id == -1) {
                return null;
            }
            Page page = pages.get(frame_id);
            update_page(page, position, frame_id);
            diskManager.ReadPage(page, position.filename, position.offset, Page.DEFAULT_PAGE_SIZE);
            page.pin_count ++;
            if (page.pin_count == 1) {
                lruReplacer.Pin(frame_id);
            }
            return page;
        }
    }

    /**
     * @description: 取消固定pin_count>0的在缓冲池中的page
     * @return {bool} 如果目标页的pin_count<=0则返回false，否则返回true
     * @param {position} position 目标page的position
     * @param {bool} is_dirty 若目标page应该被标记为dirty则为true，否则为false
     */
    public boolean unpin_page(PagePosition position, boolean is_dirty) {
        Integer frame_id = pageMap.get(position);
        if (frame_id != null) {
            Page page = pages.get(frame_id);
            if (page.pin_count == 0) {
                return false;
            }
            page.pin_count --;
            if (page.pin_count == 0) {
                lruReplacer.Unpin(frame_id);
            }
            page.dirty |= is_dirty;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @description: 将目标页写回磁盘，不考虑当前页面是否正在被使用
     * @return {bool} 成功则返回true，否则返回false(只有page_table_中没有目标页时)
     * @param {PageId} page_id 目标页的page_id，不能为INVALID_PAGE_ID
     */
    public boolean FlushPage(PagePosition position) throws DBException {
        Integer frame_id = pageMap.get(position);
        if (frame_id != null) {
            Page page = pages.get(frame_id);
            diskManager.FlushPage(page);
            page.dirty = false;
            return true;
        } else {
            return false;
        }
    }

    public Page NewPage(String filename) throws DBException {
        int frame_id = find_victim_page();
        if (frame_id == -1) {
            return null;
        }
        int new_page_offset = diskManager.allocatePage(filename);
        Page page = pages.get(frame_id);

        PagePosition position = new PagePosition(filename, new_page_offset);
        update_page(page, position, frame_id);

        diskManager.FlushPage(page);
        page.pin_count ++;

        if (page.pin_count == 1) {
            lruReplacer.Pin(frame_id);
        }
        return page;
    }

    public boolean DeletePage(PagePosition position) throws DBException {
        Integer frame_id = pageMap.get(position);
        if (frame_id != null) {
            Page page = pages.get(frame_id);
            if (page.pin_count > 0) {
                return false;
            }
            if (page.dirty) {
                diskManager.FlushPage(page);
                page.dirty = false;
            }
            pages.remove(frame_id);
            pageMap.remove(position);
            freeList.add(frame_id);
            // pin count must be 0
            return true;
        } else {
            return false;
        }

    }

    public void FlushAllPages(String filename) throws DBException {
        for (Map.Entry<PagePosition, Integer> entry : this.pageMap.entrySet()) {
            PagePosition position = entry.getKey();
            Integer frame_id = entry.getValue();
            if (position.filename.equals(filename)) {
                Page page = pages.get(frame_id);
                diskManager.FlushPage(page);
                page.dirty = false;
            }
        }
    }

    public void DeleteAllPages(String filename) throws DBException {
        ArrayList<PagePosition> positions = new ArrayList<>();
        for (Map.Entry<PagePosition, Integer> entry : this.pageMap.entrySet()) {
            if (entry.getKey().filename.equals(filename)) {
                positions.add(entry.getKey());
            }
        }
        for (PagePosition position : positions) {
            DeletePage(position);
        }
    }

    // return -1 when the buffer pool is not free
    // however, in our test, that's never been happened
    private int find_victim_page() throws DBException {
        if (!freeList.isEmpty()) {
            return freeList.removeFirst();
        } else {
            int frame_id = lruReplacer.Victim();
            if (frame_id != -1) {
                Page page = pages.get(frame_id);
                if (page.dirty) {
                    diskManager.FlushPage(page);
                }
            }
            return frame_id;
        }
    }

    private void update_page(Page page, PagePosition new_position, int frame_id) throws DBException {
        if(page.dirty) {
            diskManager.FlushPage(page);
        }
        // remove old one
        pageMap.remove(page.position);
        // add new one
        pageMap.put(new_position, frame_id);
        Arrays.fill(page.data, (byte) 0);

        page.position = new_position;
        page.pin_count = 0;
        page.dirty = false;
    }
}
