package edu.sustech.cs307.value;

import java.nio.ByteBuffer;

public class Value {
    public Object value;
    public ValueType type;

    public Value(Object value, ValueType type) {
        this.value = value;
        this.type = type;
    }

    public Value(Long value) {
        this.value = value;
        type = ValueType.INTEGER;
    }

    public Value(Double value) {
        this.value = value;
        type = ValueType.FLOAT;
    }

    public Value(String value) {
        this.value = value;
        type = ValueType.CHAR;
    }

    public byte[] ToByte() {
        return switch (type) {
            case INTEGER -> {
                ByteBuffer buffer1 = ByteBuffer.allocate(8);
                buffer1.putLong((long) value);
                yield buffer1.array();
            }
            case FLOAT -> {
                ByteBuffer buffer2 = ByteBuffer.allocate(8);
                buffer2.putDouble((double) value);
                yield buffer2.array();
            }
            case CHAR -> {
                yield ((String) value).getBytes();
            }
            default -> throw new RuntimeException("Unsupported value type: " + type);
        };
    }

    public static Value FromByte(byte[] bytes, ValueType type) {
        return switch (type) {
            case INTEGER -> {
                ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
                yield new Value(buffer1.getLong());
            }
            case FLOAT -> {
                ByteBuffer buffer2 = ByteBuffer.wrap(bytes);
                yield new Value(buffer2.getDouble());
            }
            case CHAR -> {
                String s = new String(bytes, 0, bytes.length);
                yield new Value(s);
            }
            default -> throw new RuntimeException("Unsupported value type: " + type);
        };

    }
}
