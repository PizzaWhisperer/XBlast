package ch.epfl.xblast.namecheck;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Bomb;
import ch.epfl.xblast.server.GameState;

/**
 * Classe abstraite utilisant tous les éléments de l'étape 5, pour essayer de
 * garantir que ceux-ci ont le bon nom et les bons types. Attention, ceci n'est
 * pas un test unitaire, et n'a pas pour but d'être exécuté!
 */

abstract class NameCheck05 {
    void checkGameState(GameState s,
            Map<PlayerID, Optional<Direction>> speedChangeEvents,
            Set<PlayerID> bombDropEvents) {
        Map<Cell, Bomb> b = s.bombedCells();
        Set<Cell> l = s.blastedCells();
        s = s.next(speedChangeEvents, bombDropEvents);
        System.out.println("b:" + b + ", l: " + l);
    }
}
