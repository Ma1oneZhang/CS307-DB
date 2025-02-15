package value;


import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.value.ValueComparer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ValueComparerTest {

    @Test
    @DisplayName("整数比较1")
    void testCompareIntegerEqual() throws DBException {
        Value v1 = new Value(10L);
        Value v2 = new Value(10L);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(0);  // 10 == 10
    }

    @Test
    @DisplayName("整数比较2")
    void testCompareIntegerGreaterThan() throws DBException {
        Value v1 = new Value(15L);
        Value v2 = new Value(10L);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(1);  // 15 > 10
    }

    @Test
    @DisplayName("整数比较3")
    void testCompareIntegerLessThan() throws DBException {
        Value v1 = new Value(5L);
        Value v2 = new Value(10L);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(-1);  // 5 < 10
    }

    @Test
    @DisplayName("浮点数比较1")
    void testCompareFloatEqual() throws DBException {
        Value v1 = new Value(10.5);
        Value v2 = new Value(10.5);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(0);  // 10.5 == 10.5
    }

    @Test
    @DisplayName("浮点数比较2")
    void testCompareFloatGreaterThan() throws DBException {
        Value v1 = new Value(15.5);
        Value v2 = new Value(10.5);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(1);  // 15.5 > 10.5
    }

    @Test
    @DisplayName("浮点数比较3")
    void testCompareFloatLessThan() throws DBException {
        Value v1 = new Value(5.5);
        Value v2 = new Value(10.5);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(-1);  // 5.5 < 10.5
    }

    @Test
    @DisplayName("字符串比较1")
    void testCompareStringEqual() throws DBException {
        Value v1 = new Value("apple");
        Value v2 = new Value("apple");

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(0);  // "apple" == "apple"
    }

    @Test
    @DisplayName("字符串比较2")
    void testCompareStringGreaterThan() throws DBException {
        Value v1 = new Value("banana");
        Value v2 = new Value("apple");

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(1);  // "banana" > "apple"
    }

    @Test
    @DisplayName("字符串比较3")
    void testCompareStringLessThan() throws DBException {
        Value v1 = new Value("apple");
        Value v2 = new Value("banana");

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(-1);  // "apple" < "banana"
    }

    @Test
    @DisplayName("V1为Null")
    void testCompareNullV1() throws DBException {
        Value v1 = null;
        Value v2 = new Value(10L);

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(-1);  // v1 is null, so return -1
    }

    @Test
    @DisplayName("V2为Null")
    void testCompareNullV2() throws DBException {
        Value v1 = new Value(10L);
        Value v2 = null;

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(1);  // v2 is null, so return 1
    }

    @Test
    @DisplayName("两者同时为null")
    void testCompareNullBoth() throws DBException {
        Value v1 = null;
        Value v2 = null;

        int result = ValueComparer.compare(v1, v2);

        assertThat(result).isEqualTo(-1);  // Both null, return -1
    }

    @Test
    @DisplayName("不同类型比较")
    void testCompareDifferentTypes() {
        Value v1 = new Value(10L);
        Value v2 = new Value(10.0);

        assertThatThrownBy(() -> ValueComparer.compare(v1, v2))
                .isInstanceOf(DBException.class)
                .hasMessageContaining("WRONG_COMPARISON_TYPE");
    }
}