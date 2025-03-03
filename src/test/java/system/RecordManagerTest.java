package system;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.record.RecordFileHandle;
import edu.sustech.cs307.record.RecordFileHeader;
import edu.sustech.cs307.record.RecordPageHeader;
import edu.sustech.cs307.storage.BufferPool;
import edu.sustech.cs307.storage.DiskManager;
import edu.sustech.cs307.storage.Page;
import edu.sustech.cs307.system.RecordManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecordManagerTest {

    private RecordManager recordManager;
    private DiskManager diskManager;
    private BufferPool bufferPool;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        diskManager = new DiskManager(tempDir.toString(), new HashMap<>());
        bufferPool = new BufferPool(10, diskManager);
        recordManager = new RecordManager(diskManager, bufferPool);
    }

    @Test
    @DisplayName("测试文件创建")
    void testCreateFile() throws DBException {
        String filename = "testFile";
        int recordSize = 100;

        recordManager.CreateFile(filename, recordSize);

        Page page = new Page();
        diskManager.ReadPage(page, filename, 0, Page.DEFAULT_PAGE_SIZE);
        RecordFileHeader recordFileHeader = new RecordFileHeader(page.data);

        assertThat(recordFileHeader.getRecordSize()).isEqualTo(recordSize);
        assertThat(recordFileHeader.getNumberOfPages()).isEqualTo(1);
        assertThat(recordFileHeader.getFirstFreePage()).isEqualTo(RecordPageHeader.NO_NEXT_FREE_PAGE);
        assertThat(recordFileHeader.getBitMapSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("测试无效记录大小的文件创建")
    void testCreateFileWithInvalidRecordSize() {
        String filename = "testFile";
        int invalidRecordSize = -1;

        assertThatThrownBy(() -> recordManager.CreateFile(filename, invalidRecordSize))
                .isInstanceOf(DBException.class)
                .hasMessageContaining("INVALID_TABLE_WIDTH");
    }

    @Test
    @DisplayName("测试文件删除")
    void testDeleteFile() throws DBException {
        String filename = "testFile";
        int recordSize = 100;

        recordManager.CreateFile(filename, recordSize);
        recordManager.DeleteFile(filename);

        assertThatThrownBy(() -> diskManager.ReadPage(new Page(), filename, 0, Page.DEFAULT_PAGE_SIZE))
                .isInstanceOf(DBException.class);
    }

    @Test
    @DisplayName("测试文件打开")
    void testOpenFile() throws DBException {
        String filename = "testFile";
        int recordSize = 100;

        recordManager.CreateFile(filename, recordSize);
        RecordFileHandle recordFileHandle = recordManager.OpenFile(filename);

        assertThat(recordFileHandle).isNotNull();
        assertThat(recordFileHandle.getFilename()).isEqualTo(filename);
    }

    @Test
    @DisplayName("测试文件关闭")
    void testCloseFile() throws DBException {
        String filename = "testFile";
        int recordSize = 100;

        recordManager.CreateFile(filename, recordSize);
        RecordFileHandle recordFileHandle = recordManager.OpenFile(filename);
        recordManager.CloseFile(recordFileHandle);

    }
}
