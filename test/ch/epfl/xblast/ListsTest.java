package ch.epfl.xblast;

import ch.epfl.xblast.Lists;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * This test tests the class Lists.
 * 
 * @author Maire Cedric (259314)
 * @author Délèze Benjamin (259992)
 *
 */

public class ListsTest {
    
    @Test (expected = IllegalArgumentException.class)
    public void mirroredThrowExceptionOnEmptyList() {
        Lists.mirrored(new ArrayList<Integer>());
    }
    
    @Test
    public void mirroredNormalTest() {
        List<Integer> testList = new ArrayList<Integer>(
                Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> resultList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 4, 3, 2, 1));
        
        testList = Lists.mirrored(testList);
        
        assertEquals(resultList, testList);
    }
    
    @Test
    public void mirroredOneElementTest() {
        List<Integer> testList = new ArrayList<Integer>(
                Arrays.asList(1));
        List<Integer> resultList = new ArrayList<Integer>(Arrays.asList(1));
        
        testList = Lists.mirrored(testList);
        
        assertEquals(resultList, testList);
    }
    
    @Test
    public void mirroredStringTest() {
        List<String> testList = new ArrayList<String>(
                Arrays.asList("a", "b", "c", "d"));
        List<String> resultList = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "c", "b", "a"));
        
        testList = Lists.mirrored(testList);
        
        assertEquals(resultList, testList);
    }
}