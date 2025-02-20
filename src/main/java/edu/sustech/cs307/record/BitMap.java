package edu.sustech.cs307.record;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

public class BitMap {
    private static final int BITMAP_WIDTH = 8;
    private static final int BITMAP_HIGHEST_BIT = 0x80;

    // 从地址bm开始的size个字节全部置0
    public static void init(ByteBuf bm) {
        bm.setZero(0, bm.capacity());
    }

    // pos位 置1
    public static void set(ByteBuf bm, int pos) {
        int bucket = getBucket(pos);
        byte bit = getBit(pos);
        bm.setByte(bucket, (byte) (bm.getByte(bucket) | bit));
    }

    // pos位 置0
    public static void reset(ByteBuf bm, int pos) {
        int bucket = getBucket(pos);
        byte bit = getBit(pos);
        bm.setByte(bucket, (byte) (bm.getByte(bucket) & ~bit));
    }

    // 如果pos位是1，则返回true
    public static boolean isSet(ByteBuf bm, int pos) {
        int bucket = getBucket(pos);
        byte bit = getBit(pos);
        return (bm.getByte(bucket) & bit) != 0;
    }

    /**
     * @brief 找下一个为0 or 1的位
     * @param bit false表示要找下一个为0的位，true表示要找下一个为1的位
     * @param bm 要找的起始地址为bm
     * @param maxN 要找的从起始地址开始的偏移为[curr+1,max_n)
     * @param curr 要找的从起始地址开始的偏移为[curr+1,max_n)
     * @return 找到了就返回偏移位置，没找到就返回max_n
     */
    public static int nextBit(boolean bit, ByteBuf bm, int maxN, int curr) {
        for (int i = curr + 1; i < maxN; i++) {
            if (isSet(bm, i) == bit) {
                return i;
            }
        }
        return maxN;
    }

    // 找第一个为0 or 1的位
    public static int firstBit(boolean bit, ByteBuf bm, int maxN) {
        return nextBit(bit, bm, maxN, -1);
    }

    private static int getBucket(int pos) {
        return pos / BITMAP_WIDTH;
    }

    private static byte getBit(int pos) {
        return (byte) (BITMAP_HIGHEST_BIT >>> (pos % BITMAP_WIDTH));
    }
}
