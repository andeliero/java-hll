# HyperLogLog++

I wanted to show you this technique because it's really fascinating how 
a simple problem can be tackled with a nifty strategy.

---

## The problem

The **count-distinct** problem (aka *cardinality estimation problem*) is 
the problem of finding the number of distinct elements in a data stream 
with repeated elements. 

---

In general, the complexity in terms of space is Θ(D) because we need to 
remind each unique element(D) to tell if an element is already seen. 

---

In general, the complexity in terms of space is O(n) because we need to 
remind each unique element to tell if an element is already seen.
Unfortunately, this strategy doesn’t scale with humongous cardinalities such as:
- Unique query (Counting the number of unique searches in a search engine)
- Network Monitoring (counting unique source IP addresses)
- Unique Visitors Tracking

---

What if we allocate a constant amount of memory?
![Limitless (2011)](limitless.png)

---

## The algorithm
If you just need an estimation or when the number of unique elements is 
not practical to store them in memory because of the magnitude, then you 
can use **HyperLogLog++**. 
This technique allows estimating the number of unique elements with a 
typical accuracy (standard error) of 2%, using 1.5 kB of memory (but this 
depends on the number of registers). 

In the original paper and in related literature, the term "cardinality" is 
used to mean the number of distinct elements.

---

## Example

Down below, I'll try to give an example to explain the core idea.
Pretend your friend likes to play with a die, he gets a new one and 
starts to play. The day after he tells the biggest number of trailing 1s 
he gets, let's pretend it's 3. 

The probability of this event to happen is:
```
1/6 * 1/6 * 1/6 = 1/(6^3)
```

So the number of games is the inverse
```
1 / (1/6 * 1/6 * 1/6)  = = 6^3
```

So, if we translate this example into an algorithm, the game is the hash 
function and instead of counting the number of trailing 1s. We count the 
number of trailing 0 s, and the number of plays be seen as unique elements.

This is the primitive idea of the Flajolet–Martin algorithm, and it has 
been improved with HyperLogLog++ with a **Correction Factor**, 
**Grouped Averaging** and **Linear Counting** for small cardinalities.

---

## Implementation
The operations are:

### Data Structure

```java
//Initialize the array of 16 383 (2^14-1) elements
private final byte[] registers = new byte[16384];
```

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

---

# Relative Percentage Error

---

## No Optimizations

![no_optimisations.svg](no_optimisations.svg)

---

## With Correction Factor

![with_correction_factor.svg](with_correction_factor.svg)

---

## With Linear Counting

![with_linear_counting.svg](with_linear_counting.svg)

---

## Actual

![Error](error_plot.svg)

Note at the beginning till ~40K linear counting is used, from there on 
HLL++ is used.

---

## Conclusion

The algorithm provides a great approximation of the cardinality with 
a relative percentage error between 6% and -4%. The complexity 
is O(1) in terms of space and O(1) time complexity. 

---

## Credits
![Philippe Flajolet](flajolet_philippe_small.jpg)
