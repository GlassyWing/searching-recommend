package org.manlier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.BitSet;

@RunWith(JUnit4.class)
public class ArithmeticTest {

    @Test
    public void test01() {
        BitSet bits = new BitSet(3);
        bits.set(0, true);
        bits.set(1, true);
        bits.set(2, false);
        System.out.println(bits.cardinality());
        BitSet target = BitSet.valueOf(new long[]{7});
        bits.and(target);
        System.out.println(bits.cardinality());
    }
}
