package com.tixon.ringselector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by tikhon.osipov on 07.12.2016
 */

@RunWith(JUnit4.class)
public class EdgePositionsTest {
    @Test
    public void testEdgePositions() throws Exception {
        int[] test = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 57, 58, 59};
        for(int i = 0; i < test.length; i++) {
            System.out.println("number="+test[i]+", prev="+numberPrev(test[i])+", next="+numberNext(test[i]));
        }
    }

    private int numberNext(int number) {
        return ((number+3))%60;
    }

    private int numberPrev(int number) {
        int result = ((number-3))%60;
        if(result < 0) result += 60;
        return result;
    }
}
