package ch.epfl.xblast.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import ch.epfl.xblast.PlayerAction;

/**
 * Used to handle the key events
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class KeyboardEventHandler extends KeyAdapter {
    private final Map<Integer, PlayerAction> keyBindings;
    private final Consumer<PlayerAction> sender;

    /**
     * Constructor, checks if the map, linking an Integer (in reality, an event)
     * to a PlayerAction, or the consumer aren't null
     * 
     * @param keyBoardActions
     * @param temporaryName
     */
    public KeyboardEventHandler(Map<Integer, PlayerAction> keyBoardActions,
            Consumer<PlayerAction> consumer) {
        this.keyBindings = new HashMap<>(
                Objects.requireNonNull(keyBoardActions));
        this.sender = Objects.requireNonNull(consumer);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyBindings.containsKey(keyCode)) {
            sender.accept(keyBindings.get(keyCode));
        }
    }

    /**
     * @return the default keyBindings map, which associate a KeyEvent to a
     *         PlayerAction
     */
    public static Map<Integer, PlayerAction> keyBindings() {
        Map<Integer, PlayerAction> kb = new HashMap<>();
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        kb.put(KeyEvent.VK_ENTER, PlayerAction.JOIN_GAME);
        return kb;
    }
}
