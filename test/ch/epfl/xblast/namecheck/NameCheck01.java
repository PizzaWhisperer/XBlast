package ch.epfl.xblast.namecheck;

import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.SubCell;

/**
 * Classe abstraite utilisant tous les éléments de l'étape 1, pour essayer de
 * garantir que ceux-ci ont le bon nom et les bons types. Attention, ceci n'est
 * pas un test unitaire, et n'a pas pour but d'être exécuté!
 */

abstract class NameCheck01 {
    void checkDirection() {
        Direction d = Direction.N;
        d = Direction.E;
        d = Direction.S;
        d = Direction.W;
        if (d.isHorizontal() || d.isParallelTo(d))
            d = d.opposite();
    }

    void checkCell() {
        int c = Cell.COLUMNS;
        int r = Cell.ROWS;
        int t = Cell.COUNT;
        List<Cell> l = Cell.ROW_MAJOR_ORDER;
        l.get(c + r + t);
        l = Cell.SPIRAL_ORDER;
        l.get(c + r + t);
        Cell d = new Cell(c, r);
        c = d.x() + d.y() + d.rowMajorIndex();
        d = d.neighbor(Direction.N);
    }

    void checkSubCell() {
        SubCell c = SubCell.centralSubCellOf(new Cell(0,0));
        c = new SubCell(0, 0);
        int t = c.x() + c.y() + c.distanceToCentral();
        if (t < 10 && c.isCentral())
            c = c.neighbor(Direction.N);
        else {
            Cell cc = c.containingCell();
            System.out.println(cc);
        }
    }
}
