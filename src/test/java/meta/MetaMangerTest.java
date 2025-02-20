package meta;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.meta.*;
import edu.sustech.cs307.value.ValueType;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


class MetaManagerTest {
    private MetaManager metaManager;
    private static final String TEST_META_FILE = "meta_data.json";

    @BeforeEach
    void setUp() throws DBException {
        metaManager = new MetaManager();
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_META_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testCreateAndRetrieveTable() throws DBException {
        TableMeta tableMeta = new TableMeta("users");
        tableMeta.addColumn(new ColumnMeta("id", ValueType.INTEGER));
        tableMeta.addColumn(new ColumnMeta("name", ValueType.CHAR));

        metaManager.createTable("users", tableMeta);
        TableMeta retrievedMeta = metaManager.getTable("users");

        assertNotNull(retrievedMeta);
        assertEquals("users", retrievedMeta.tableName);
        assertTrue(retrievedMeta.getColumns().containsKey("id"));
        assertTrue(retrievedMeta.getColumns().containsKey("name"));
    }

    @Test
    void testDropTable() throws DBException {
        TableMeta tableMeta = new TableMeta("orders");
        metaManager.createTable("orders", tableMeta);
        metaManager.dropTable("orders");
        assertNull(metaManager.getTable("orders"));
    }

    @Test
    void testAddAndDropColumn() throws DBException {
        TableMeta tableMeta = new TableMeta("products");
        metaManager.createTable("products", tableMeta);

        metaManager.addColumnInTable("products", new ColumnMeta("price", ValueType.FLOAT));
        assertTrue(metaManager.getTable("products").getColumns().containsKey("price"));

        metaManager.dropColumnInTable("products", "price");
        assertFalse(metaManager.getTable("products").getColumns().containsKey("price"));
    }

    @Test
    void testTableAlreadyExistsException() throws DBException {
        TableMeta tableMeta = new TableMeta("customers");
        metaManager.createTable("customers", tableMeta);
        assertThrows(DBException.class, () -> metaManager.createTable("customers", tableMeta));
    }

    @Test
    void testTableDoesNotExistException() {
        assertThrows(DBException.class, () -> metaManager.dropTable("non_existent_table"));
    }

    @Test
    void testColumnAlreadyExistsException() throws DBException {
        TableMeta tableMeta = new TableMeta("employees");
        metaManager.createTable("employees", tableMeta);
        ColumnMeta column = new ColumnMeta("salary", ValueType.FLOAT);
        metaManager.addColumnInTable("employees", column);
        assertThrows(DBException.class, () -> metaManager.addColumnInTable("employees", column));
    }

    @AfterEach
    void printAllTableNames() {
        System.out.println(this.metaManager.getTableNames());
    }
}
