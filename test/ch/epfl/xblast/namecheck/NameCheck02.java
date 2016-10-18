package ch.epfl.xblast.namecheck;

import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.Ticks;

/**
 * Classe abstraite utilisant tous les éléments de l'étape 2, pour essayer de
 * garantir que ceux-ci ont le bon nom et les bons types. Attention, ceci n'est
 * pas un test unitaire, et n'a pas pour but d'être exécuté!
 */

abstract class NameCheck02 {
    void checkTicks() {
        int x = Ticks.PLAYER_DYING_TICKS
                + Ticks.PLAYER_INVULNERABLE_TICKS
                + Ticks.BOMB_FUSE_TICKS
                + Ticks.EXPLOSION_TICKS
                + Ticks.WALL_CRUMBLING_TICKS
                + Ticks.BONUS_DISAPPEARING_TICKS;
        System.out.println(x);
    }

    void checkBlock() {
        Block b = Block.FREE;
        b = Block.INDESTRUCTIBLE_WALL;
        b = Block.DESTRUCTIBLE_WALL;
        b = Block.CRUMBLING_WALL;
        boolean d = b.isFree() && b.canHostPlayer() && b.castsShadow();
        System.out.println(b + "/" + d);
    }

    void checkLists() {
        List<Integer> l1 = null;
        List<String> l2 = null;
        List<List<String>> l3 = null;
        List<Integer> l1m = Lists.<Integer>mirrored(l1);
        List<String> l2m = Lists.<String>mirrored(l2);
        List<List<String>> l3m = Lists.<List<String>>mirrored(l3);
        System.out.println("" + l1m + l2m + l3m);
    }

    void checkBoard() {
        List<List<Block>> q = null;
        Board b = Board.ofQuadrantNWBlocksWalled(q);
        b = Board.ofInnerBlocksWalled(q);
        b = Board.ofRows(q);
        List<Sq<Block>> cells = null;
        b = new Board(cells);
        Cell c = null;
        Sq<Block> bs = b.blocksAt(c);
        Block l = b.blockAt(c);
        System.out.println(bs + "/" + l);
    }
}
