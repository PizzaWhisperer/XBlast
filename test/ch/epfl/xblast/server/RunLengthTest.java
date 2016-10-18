package ch.epfl.xblast.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ch.epfl.xblast.RunLengthEncoder;

public class RunLengthTest {

    @Test
    public void WorksOnEmptyList() {
        assertEquals(new ArrayList<>(),
                RunLengthEncoder.encode(Collections.emptyList()));
    }

    @Test
    public void WorksOnTrialList() {
        assertEquals(new ArrayList<>((byte)1),
                RunLengthEncoder.encode(new ArrayList<>((byte) 1)));
    }

    @Test
    public void WorksOnNonTrialList() {
        List<Byte> list = new LinkedList<>();
        list.add((byte) 1);
        list.addAll(Collections.nCopies(5, (byte) 2));
        list.addAll(Collections.nCopies(2, (byte) 3));
        list.add((byte) 4);
        List<Byte> toTest = RunLengthEncoder.encode(list);

        List<Byte> correct = new LinkedList<>();
        correct.add((byte) 1);
        correct.add((byte) -3);
        correct.add((byte) 2);
        correct.add((byte) 3);
        correct.add((byte) 3);
        correct.add((byte) 4);
        System.out.println(toTest);
        System.out.println(correct);
        assertTrue(toTest.equals(correct));
    }

}
