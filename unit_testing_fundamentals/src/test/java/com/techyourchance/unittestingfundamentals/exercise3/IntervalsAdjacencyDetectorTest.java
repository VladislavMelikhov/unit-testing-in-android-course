package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(15, 20);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1BeforeAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(10, 15);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2OnStart_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(7, 15);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1IsContainedWithinInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(1, 15);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1IsContainedWithinInterval2AdjacentOnStart_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(5, 15);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1IsContainedWithinInterval2AdjacentOnEnd_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(1, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(7, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2AdjacentOnStart_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(5, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1ContainsInterval2AdjacentOnEnd_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(7, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2OnEnd_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(1, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1AfterAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(1, 3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1EqualsInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 10);
        Interval interval2 = new Interval(5, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
}