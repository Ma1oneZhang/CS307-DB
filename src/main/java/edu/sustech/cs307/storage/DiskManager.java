package edu.sustech.cs307.storage;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DiskManager {
    private final String currentDir;
    public Map<String, Integer> filePages;

    public DiskManager(String path, Map<String, Integer> fileOffsets) {
        this.currentDir  = path;
        this.filePages = fileOffsets;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void ReadPage(Page page, String filename, int offset, long length) throws DBException {
        // 拼接实际文件路径
        String real_path = currentDir + "/" + filename;
        try (FileInputStream fis = new FileInputStream(real_path)) {
            long seeked = fis.skip(offset);
            if (seeked < offset) {
                throw new DBException(ExceptionTypes.BadIOError(
                        String.format("Seek out of range, offset: %d, length: %d", seeked, length)));
            }
            // 直接将文件中一个页面大小的数据读取到 page.data 中
            if (fis.read(page.data.array()) != Page.DEFAULT_PAGE_SIZE) {
                throw new DBException(ExceptionTypes.BadIOError(
                        String.format("Bad file at offset: %d, length: %d", seeked, length)));
            }
            page.position.offset = offset;
            page.position.filename = filename;
        } catch (IOException e) {
            throw new DBException(ExceptionTypes.BadIOError(e.getMessage()));
        }
    }

    public void FlushPage(Page page) throws DBException {
        // 拼接实际文件路径
        String real_path = currentDir + "/" + page.position.filename;

        // 使用 RandomAccessFile 和 FileChannel 来定位并写入数据
        try (RandomAccessFile raf = new RandomAccessFile(real_path, "rw");
             FileChannel channel = raf.getChannel()) {
            // 定位到对应的文件偏移位置
            channel.position(page.position.offset);
            // 使用 ByteBuffer.wrap 避免额外的数据拷贝
            ByteBuffer buffer = ByteBuffer.wrap(page.data.array());
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            // 强制刷新到磁盘
            channel.force(true);
        } catch (IOException e) {
            throw new DBException(ExceptionTypes.BadIOError(e.getMessage()));
        }
    }

    public long GetFileSize(String filename) {
        String real_path = currentDir + "/" + filename;
        File file = new File(real_path);
        return file.length();
    }

    public void CreateFile(String filename) throws DBException {
        String real_path = currentDir + "/" + filename;
        File file = new File(real_path);
        if (!file.exists()) {
            try {
                // 如果上级目录不存在则创建
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (!file.createNewFile()) {
                    throw new DBException(ExceptionTypes.BadIOError("File creation failed: " + real_path));
                }
                this.filePages.put(filename, 0);
            } catch (IOException e) {
                throw new DBException(ExceptionTypes.BadIOError(e.getMessage()));
            }
        }
    }

    // return file start;
    public Integer allocatePage(String filename) throws DBException {
        Integer offset = this.filePages.get(filename);
        if (offset == null) {
            throw new DBException(ExceptionTypes.BadIOError(String.format("File not exists, %s", filename)));
        }
        this.filePages.put(filename, offset + 1);
        return offset;
    }
}