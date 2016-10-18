package ch.epfl.xblast;

import java.util.ArrayList;
import ch.epfl.xblast.Lists;

import org.junit.Assert;
import org.junit.Test;
public class isMirrored {

	@Test
	public void test() {
		ArrayList<Double> array = new ArrayList<Double>();
		for (double i = 0; i < 4; ++i) array.add(i);
		Lists.mirrored(array);
		ArrayList<Double> expected = new ArrayList<Double>();
		expected.add(0.0);
		expected.add(1.0);
		expected.add(2.0);
		expected.add(3.0);
		expected.add(2.0);
		expected.add(1.0);
		expected.add(0.0);
		for(int i = 0 ; i < array.size(); ++i){
			Assert.assertEquals(expected.get(i), array.get(i));
		}
	}

}
