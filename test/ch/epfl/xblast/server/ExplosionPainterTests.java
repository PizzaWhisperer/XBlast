package ch.epfl.xblast.server;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.painter.ExplosionPainter;

public class ExplosionPainterTests {
	Bomb b = new Bomb(PlayerID.PLAYER_1, new Cell(3,3), 65, 4);
	Bomb c = new Bomb(PlayerID.PLAYER_1, new Cell(3,3), 64, 4);
	
	@Test
	public void byteForBombTest(){
		byte expectedWhite = 21;
		byte expectedBlack = 20;
		assertEquals("Expected was : " + expectedWhite + "but was : " + ExplosionPainter.byteForBomb(c),expectedWhite, ExplosionPainter.byteForBomb(c));
		assertEquals(expectedBlack, ExplosionPainter.byteForBomb(b));
		
	}
	
	@Test
	public void byteForBlastTest(){
		byte expectedByte = 13;
		assertEquals(expectedByte, ExplosionPainter.byteForBlast(true, true, false, true));
	}
	
}
