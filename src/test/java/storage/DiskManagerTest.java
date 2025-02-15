package storage;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.storage.DiskManager;
import edu.sustech.cs307.storage.Page;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiskManagerTest {
    @TempDir
    static Path tempDir;
    private DiskManager diskManager;
    private static final String TEST_FILE = "test_file.dat";
    private static final String NON_EXISTENT_FILE = "no_such_file.dat";
    private static final int PAGE_SIZE = Page.DEFAULT_PAGE_SIZE;

    @BeforeEach
    void setUp() {
        diskManager = new DiskManager(tempDir.toString());
    }

    @Test
    @DisplayName("创建新文件应成功")
    void createNewFile() throws Exception {
        diskManager.CreateFile(TEST_FILE);

        Path filePath = tempDir.resolve(TEST_FILE);
        assertThat(filePath)
                .exists()
                .isRegularFile();
    }

    @Test
    @DisplayName("创建嵌套目录文件")
    void createNestedFile() throws Exception {
        String nestedFile = "nested/dir/test_file.dat";
        diskManager.CreateFile(nestedFile);

        Path filePath = tempDir.resolve(nestedFile);
        assertThat(filePath)
                .exists()
                .isRegularFile();
    }

    @Test
    @DisplayName("获取文件大小")
    void getFileSize() throws Exception {
        // 创建测试文件并写入数据
        byte[] data = new byte[1024];
        Path filePath = tempDir.resolve(TEST_FILE);
        Files.write(filePath, data);

        long size = diskManager.GetFileSize(TEST_FILE);
        assertThat(size).isEqualTo(1024);
    }

    @Test
    @DisplayName("读取有效页")
    void readValidPage() throws Exception {
        // 准备测试数据
        byte[] expected = new byte[PAGE_SIZE];
        for (int i = 0; i < PAGE_SIZE; i++) {
            expected[i] = (byte) (i % 256);
        }
        Path filePath = tempDir.resolve(TEST_FILE);
        Files.write(filePath, expected);

        // 执行读取
        Page page = diskManager.ReadPage(TEST_FILE, 0, PAGE_SIZE);

        assertThat(page.data)
                .containsExactly(expected);
        assertThat(page.filename).isEqualTo(TEST_FILE);
        assertThat(page.file_offset).isZero();
    }

    @Test
    @DisplayName("写入并刷新页")
    void flushPage() throws Exception {
        // 准备测试页
        Page page = new Page();
        for (int i = 0; i < PAGE_SIZE; i++) {
            page.data[i] = (byte) (i % 128);
        }
        page.filename = TEST_FILE;
        page.file_offset = 0;

        // 写入文件
        diskManager.CreateFile(TEST_FILE);
        diskManager.FlushPage(page);

        // 验证写入内容
        Path filePath = tempDir.resolve(TEST_FILE);
        byte[] fileContent = Files.readAllBytes(filePath);
        assertThat(fileContent)
                .hasSize(PAGE_SIZE)
                .containsExactly(page.data);
    }

    @Test
    @DisplayName("读取越界应抛出异常")
    void readOutOfBounds() throws Exception {
        diskManager.CreateFile(TEST_FILE);

        assertThrows(DBException.class, () -> {
            diskManager.ReadPage(TEST_FILE, PAGE_SIZE + 1, PAGE_SIZE);
        });
    }

    @Test
    @DisplayName("写入不存在的文件应抛出异常")
    void writeToNonExistentFile() {
        Page page = new Page();
        page.filename = NON_EXISTENT_FILE;

        assertThrows(DBException.class, () -> {
            diskManager.FlushPage(page);
        });
    }

    @Test
    @DisplayName("部分写入测试")
    void partialWriteTest() throws Exception {
        // 准备超出单页的数据
        byte[] largeData = new byte[PAGE_SIZE * 2];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }

        // 写入两个页
        diskManager.CreateFile(TEST_FILE);

        Page page1 = new Page();
        System.arraycopy(largeData, 0, page1.data, 0, PAGE_SIZE);
        page1.filename = TEST_FILE;
        page1.file_offset = 0;
        diskManager.FlushPage(page1);

        Page page2 = new Page();
        System.arraycopy(largeData, PAGE_SIZE, page2.data, 0, PAGE_SIZE);
        page2.filename = TEST_FILE;
        page2.file_offset = PAGE_SIZE;
        diskManager.FlushPage(page2);

        // 验证完整数据
        Path filePath = tempDir.resolve(TEST_FILE);
        byte[] fileContent = Files.readAllBytes(filePath);
        assertThat(fileContent)
                .hasSize(PAGE_SIZE * 2)
                .containsExactly(largeData);
    }
}