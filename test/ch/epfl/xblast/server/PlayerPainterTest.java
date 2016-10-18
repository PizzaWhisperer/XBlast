package ch.epfl.xblast.server;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;
import ch.epfl.xblast.server.painter.PlayerPainter;

public class PlayerPainterTest {

    @Test
    public void byteForPlayerId() {
        PlayerID id = PlayerID.PLAYER_1;
        int lives = 3;
        State state = State.VULNERABLE;

        Direction dir = Direction.N;
        int tick = 2;

        LifeState lifeState = new LifeState(lives, state);
        Sq<DirectedPosition> dp = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));

        Player playerTest1 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        id = PlayerID.PLAYER_2;
        Player playerTest2 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        id = PlayerID.PLAYER_3;
        Player playerTest3 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        id = PlayerID.PLAYER_4;
        Player playerTest4 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        assertEquals(0, PlayerPainter.byteForPlayer(tick, playerTest1));
        assertEquals(20, PlayerPainter.byteForPlayer(tick, playerTest2));
        assertEquals(40, PlayerPainter.byteForPlayer(tick, playerTest3));
        assertEquals(60, PlayerPainter.byteForPlayer(tick, playerTest4));
    }

    @Test
    public void byteForPlayerInvBlink() {
        PlayerID id = PlayerID.PLAYER_1;

        int lives = 5;
        State state = State.INVULNERABLE;

        Direction dir = Direction.N;

        int tick = 1;

        LifeState lifeState = new LifeState(lives, state);
        Sq<DirectedPosition> dp = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));

        Player playerTest1 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        // white
        assertEquals(80, PlayerPainter.byteForPlayer(tick, playerTest1));
        id = PlayerID.PLAYER_2;
        Player playerTest2 = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        // white (should be the same)
        assertEquals(80, PlayerPainter.byteForPlayer(tick, playerTest2));
        tick = 2;
        // not white
        assertEquals(0, PlayerPainter.byteForPlayer(tick, playerTest1));
    }
    
    @Test
    public void byteForPlayerDying(){
        
        PlayerID id = PlayerID.PLAYER_1;

        int lives = 5;
        State state = State.DYING;

        Direction dir = Direction.N;

        int tick = 1;

        LifeState lifeState1 = new LifeState(lives, state);
        lives = 1;
        LifeState lifeState2 = new LifeState(lives, state);
        Sq<DirectedPosition> dp = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));
        //Losing a life
        Player playerTest1 = new Player(id, Sq.constant(lifeState1), dp, 3, 3);
        assertEquals(12, PlayerPainter.byteForPlayer(tick, playerTest1));
        //dying
        Player playerTest2 = new Player(id, Sq.constant(lifeState2), dp, 3, 3);
        assertEquals(13, PlayerPainter.byteForPlayer(tick, playerTest2));
    }
    
    @Test
    public void byteForPlayerDirection(){
        PlayerID id = PlayerID.PLAYER_1;

        int lives = 5;
        State state = State.VULNERABLE;

        Direction dir = Direction.N;

        int tick = 2;

        LifeState lifeState = new LifeState(lives, state);
        Sq<DirectedPosition> dpN = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));
       dir = Direction.E;
        Sq<DirectedPosition> dpE = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));
        dir = Direction.S;
        Sq<DirectedPosition> dpS = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));
        dir = Direction.W;
        Sq<DirectedPosition> dpW = DirectedPosition
                .stopped(new DirectedPosition(new SubCell(24, 24), dir));
        
        Player playerTestN = new Player(id, Sq.constant(lifeState), dpN, 3, 3);
        assertEquals(0, PlayerPainter.byteForPlayer(tick, playerTestN));
        Player playerTestE = new Player(id, Sq.constant(lifeState), dpE, 3, 3);
        assertEquals(3, PlayerPainter.byteForPlayer(tick, playerTestE));
        Player playerTestS = new Player(id, Sq.constant(lifeState), dpS, 3, 3);
        assertEquals(6, PlayerPainter.byteForPlayer(tick, playerTestS));
        Player playerTestW = new Player(id, Sq.constant(lifeState), dpW, 3, 3);
        assertEquals(9, PlayerPainter.byteForPlayer(tick, playerTestW));
    }
    
    @Test
    public void byteForPlayerMoving(){
        PlayerID id = PlayerID.PLAYER_1;

        int lives = 5;
        State state = State.VULNERABLE;

        Direction dir = Direction.E;


        LifeState lifeState = new LifeState(lives, state);
        Sq<DirectedPosition> dp = DirectedPosition
                .moving(new DirectedPosition(new SubCell(24, 24), dir));
        Player playerTest = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        assertEquals(3, PlayerPainter.byteForPlayer(4, playerTest));
        
        dp = dp.tail();
        playerTest = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        assertEquals(4, PlayerPainter.byteForPlayer(5, playerTest));
        
        dp = dp.tail();
        playerTest = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        assertEquals(3, PlayerPainter.byteForPlayer(6, playerTest));
        
        dp = dp.tail();
        playerTest = new Player(id, Sq.constant(lifeState), dp, 3, 3);
        assertEquals(5, PlayerPainter.byteForPlayer(7, playerTest));
    }

}
