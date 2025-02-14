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

public class DiskManager {
    private final String currentDir;

    public DiskManager(String path) {
        currentDir = path;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public Page ReadPage(String filename, long offset, long length) throws DBException {
        // 拼接实际文件路径
        String real_path = currentDir + "/" + filename;
        Page page = new Page();
        try (FileInputStream fis = new FileInputStream(real_path)) {
            long seeked = fis.skip(offset);
            if (seeked < offset) {
                throw new DBException(ExceptionTypes.BadIOError(
                        String.format("Seek out of range, offset: %d, length: %d", seeked, length)));
            }
            // 直接将文件中一个页面大小的数据读取到 page.data 中
            if (fis.read(page.data) != Page.DEFAULT_PAGE_SIZE) {
                throw new DBException(ExceptionTypes.BadIOError(
                        String.format("Bad file at offset: %d, length: %d", seeked, length)));
            }
            page.file_offset = offset;
            page.filename = filename;
            return page;
        } catch (IOException e) {
            throw new DBException(ExceptionTypes.BadIOError(e.getMessage()));
        }
    }

    public void FlushPage(Page page) throws DBException {
        // 拼接实际文件路径
        String real_path = currentDir + "/" + page.filename;

        // 检查文件是否存在，如果不存在抛出异常
        if (!Files.exists(Path.of(real_path))) {
            throw new DBException(ExceptionTypes.BadIOError("Flush file not found"));
        }
        // 使用 RandomAccessFile 和 FileChannel 来定位并写入数据
        try (RandomAccessFile raf = new RandomAccessFile(real_path, "rw");
             FileChannel channel = raf.getChannel()) {
            // 定位到对应的文件偏移位置
            channel.position(page.file_offset);
            // 使用 ByteBuffer.wrap 避免额外的数据拷贝
            ByteBuffer buffer = ByteBuffer.wrap(page.data);
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
            } catch (IOException e) {
                throw new DBException(ExceptionTypes.BadIOError(e.getMessage()));
            }
        }
    }
}