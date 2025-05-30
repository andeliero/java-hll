package io.anders.hll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class HyperLogLogTest {

    HyperLogLog hll = new HyperLogLog();

    @Test
    public void calculate_trailing_zeros() {
        {
            byte actual = hll.leadingZeros(1125899906842623L);
            assertEquals(0L, actual);
        }
        {
            byte actual = hll.leadingZeros(562949953421310L);
            assertEquals(1L, actual);
        }
        {
            byte actual = hll.leadingZeros(0L);
            assertEquals(50L, actual);
        }
        {
            byte actual = hll.leadingZeros(1048575L);
            assertEquals(30L, actual);
        }
    }

    @Test
    public void calculate_register_index() {
        {
            long hash = -1L;
            short actual = hll.getIndex(hash);
            assertEquals((short) 16383, actual);
        }
        {
            long hash = 1125899906842623L;
            short actual = hll.getIndex(hash);
            assertEquals((short) 0, actual);
        }
        {
            long hash = 1125899906842624L;
            short actual = hll.getIndex(hash);
            assertEquals((short) 1, actual);
        }
    }

    @Test
    public void calculate_register_value() {
        {
            long hash = -1L;
            long actual = hll.getValue(hash);
            assertEquals(1125899906842623L, actual);
        }
        {
            long hash = 0L;
            long actual = hll.getValue(hash);
            assertEquals(0L, actual);
        }
        {
            long hash = 1125899906842624L;
            long actual = hll.getValue(hash);
            assertEquals(0L, actual);
        }
        {
            long hash = 1125899906842625L;
            long actual = hll.getValue(hash);
            assertEquals(1L, actual);
        }
    }

    @Test
    public void calculate_hash() {
        long actual = hll.hashElement("20");
        System.out.println("HASH " + Long.toBinaryString(actual));
        System.out.println("SIZE " + Long.toBinaryString(actual).length());
    }
}
