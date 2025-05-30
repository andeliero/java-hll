package io.anders.hll;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HyperLogLog {

    /**
     * Precision parameter (number of bits used to determine the register index).
     * Common values are between 4 and 16. Higher p means more registers (m)
     * and better accuracy, but also more memory usage.
     * `m = 2^p`
     */
    private final int p = 14;

    /**
     * Number of registers. Must be a power of 2.
     * `m = 2^p`
     */
    private final int m = 16384;

    private final double alpha = 0.7213 / (1 + 1.079 / m); //0.72125250052

    //Initialize the array of 16 383 (2^14-1) elements
    private final byte[] registers = new byte[16384];

    public HyperLogLog() {
    }

    /**
     * Hashes an element to a 64-bit long.
     * Using Object.hashCode() is generally poor for HLL distribution.
     * Consider MurmurHash3, FNV, or other non-cryptographic hashes.
     *
     * @param element The element to hash.
     * @return A 64-bit hash value.
     */
    long hashElement(String element) {
        return Hashing.murmur3_128().hashString(element, StandardCharsets.UTF_8).asLong();
    }


    /**
     * Get the first 14 bit of the hash in input.
     *
     * @param hash -
     * @return -
     */
    public short getIndex(long hash) {
        return (short) ((hash >>> 50) & ((long) 16383));
    }

    /**
     * Get firsts 50 bits of the hash in input.
     *
     * @param hash -
     * @return -
     */
    public long getValue(long hash) {
        return hash & 1125899906842623L;
    }

    /**
     * Get the number of leading zeros from the value in input.
     *
     * @param value -
     * @return -
     */
    public byte leadingZeros(long value) {
        byte zeros = 0;
        byte i = 49;
        while (i > -1 && (value >>> i & 1) == 0) {
            zeros++;
            i--;
        }
        return zeros;
    }

    /**
     * Add the element to the counter
     *
     * @param element -
     */
    public void add(String element) {
        // Calculate hash
        long hash = hashElement(element);
        // Take the first 14 bits to address the register
        short registerIndex = getIndex(hash);
        // Take the last 50 bits
        long value = getValue(hash);
        // Calculate the rank value for the target register
        byte rank = (byte) (leadingZeros(value) + 1);
        if (rank > registers[registerIndex]) {
            registers[registerIndex] = rank;
        }
    }

    /**
     * Estimates the cardinality (number of unique elements) seen so far.
     *
     * @return Number of unique elements estimate.
     */
    public long count() {
        double sum = 0;
        int zeroRegisters = 0;
        // Calculate the harmonic mean of the register values
        for (byte register: registers) {
            sum += Math.pow(2, -register);
            if (register == 0) {
                zeroRegisters++;
            }
        }
        // Raw estimate
        double estimate = alpha * m * m / sum;
        // Apply corrections for small cardinalities
        if (estimate <= 2.5 * m && zeroRegisters > 0) {
            // Linear counting
            estimate = m * Math.log((double) m / zeroRegisters);
        }
        return Math.round(estimate);
    }

    /**
     * Merges another HyperLogLog instance into this one.
     *
     * @param that The other HyperLogLog instance to merge.
     */
    public void merge(HyperLogLog that) {
        for (int i = 0; i < 16384; i++) {
            if (this.registers[i] < that.registers[i])
                this.registers[i] = that.registers[i];
        }
    }

}
