# HyperLogLog++

I wanted to show you this technique because it's really fascinating how 
a simple problem can be tackled with a nifty strategy.

## The problem

The **count-distinct** problem (aka *cardinality estimation problem*) is 
the problem of finding the number of distinct elements in a data stream 
with repeated elements.

In general, the complexity in terms of space is Θ(D) because we need to 
remind each unique element(D) to tell if an element is already seen.

In general, the complexity in terms of space is O(n) because we need to 
remind each unique element to tell if an element is already seen.
Unfortunately, this strategy doesn’t scale with humongous cardinalities such as:
- Unique query (Counting the number of unique searches in a search engine)
- Network Monitoring (counting unique source IP addresses)
- Unique Visitors Tracking

## The algorithm
If you just need an estimation or when the number of unique elements is 
not practical to store them in memory because of the magnitude, then you 
can use **HyperLogLog++**.

This technique allows estimating the number of unique elements with a 
relative percentage error of 6% ~ -4%, using 16 KB of memory (but this 
depends on the number of registers).

In the original paper and in related literature, the term "cardinality" is 
used to mean the number of distinct elements.

## Example

I'll try to give an example to explain the core idea.
Pretend the person in front of you likes to play with a die, and they played the whole day.
They tell you the biggest number of trailing 1s that they got so far is 3.
We as observers, we won’t be able to tell the precise number of plays,
but we can infer an approximation calculating the probability of that event.

The probability of this event to happen is:
```
1/6 * 1/6 * 1/6 = 1/(6^3)
```

So the expected number of games to see that event is the inverse
```
1 / (1/6 * 1/6 * 1/6)  = = 6^3
```

So, if we transpose this idea into an algorithm, we get that:
* The dice game → Hash function 
* Trailing 1s counting → Trailing 0s counting
* Probability of die yielding a 1 (1/6) → Probability of a bit yielding 0 (1/2)
* The number of plays → Cardinality.

This is the primitive idea of the Flajolet–Martin algorithm, and it has 
been improved with HyperLogLog++ with a **Correction Factor**, 
**Grouped Averaging** and **Linear Counting** for small cardinalities.

## Implementation

### Data Structure

```java
//Initialize the array of 16 383 (2^14-1) elements
private final byte[] registers = new byte[16384];
```

The registers are initialized to 0, and it can be addressed with 14 bits.
Mind that `byte` is enough to count 50 leading zeros.

### Add 
1. Calculate 64-bit hash of the input;
2. Take the first 14 bits of the hash and use it to index the register;
3. Take the remaining 50 bits of the hash and calculate how many leading zeros;
4. Compare with the previous value in the register and store the bigger one.

### Count
1. Calculate the harmonic mean of the register values;
2. Apply a correction factor;
3. Calculate the number of empty registers;
4. Fallback to linear counting for small cardinalities;

### Merge
1. For every register pair of the same index retain the register with the bigger value.

# Relative Percentage Error

## No Optimizations

![no_optimisations.svg](no_optimisations.svg)

## With Correction Factor

![with_correction_factor.svg](with_correction_factor.svg)

## With Linear Counting

![with_linear_counting.svg](with_linear_counting.svg)

![Error](error_plot.svg)

Note at the beginning till ~40K linear counting is used, from there on 
HLL++ is used.

## Credits
![Philippe Flajolet](flajolet_philippe_small.jpg)
