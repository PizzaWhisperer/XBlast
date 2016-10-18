package ch.epfl.xblast.server;

import static org.junit.Assert.*;

import org.junit.Test;
import ch.epfl.xblast.*;
public class BonusTests {

	

	@Test
	public void isAppliedToPlayer(){
		Player p = new Player(PlayerID.PLAYER_1, 2, new Cell(2,2), 3, 3);
		Bonus bonusBomb = Bonus.INC_BOMB;
		Bonus bonusRange = Bonus.INC_RANGE;
		p = bonusBomb.applyTo(p);
		p = bonusRange.applyTo(p);
		assertTrue(p.maxBombs() == 4 && p.bombRange() == 4);
	}
	
	
}
