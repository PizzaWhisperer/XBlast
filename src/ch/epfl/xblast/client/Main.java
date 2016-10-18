package ch.epfl.xblast.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;

/**
 * Class main used by the client to receive the gameState, print it and send
 * their actions to the server
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public class Main {

    private static final String DEFAULT_SERVEUR = "localhost";
    // Maximum size of the serialized gameState +1 for the id
    private final static int MAX_SIZE = 410;
    private final static int BYTE_TO_SEND = 1;
    private static XBlastComponent xBlastComponent;

    /**
     * The main method used by the client to join the game,
     * 
     * @param server
     *            address
     * @throws IOException
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public static void main(String[] args) throws IOException,
            InterruptedException, InvocationTargetException {

        // by default we play on our computer
        String hostName = args.length > 0 ? args[0] : DEFAULT_SERVEUR;

        try (DatagramChannel channel = DatagramChannel
                .open(StandardProtocolFamily.INET)) {

            SocketAddress serverAddress;
            SocketAddress addressToConnect = new InetSocketAddress(hostName,
                    2016);
            ByteBuffer bufferReceived = ByteBuffer.allocate(MAX_SIZE);
            ByteBuffer bufferToSend = ByteBuffer.allocate(BYTE_TO_SEND);

            channel.configureBlocking(false);

            // While we don't receive anything from the server, we sent our will
            // to join the game and wait
            while ((serverAddress = channel.receive(bufferReceived)) == null) {
                bufferToSend.put((byte) PlayerAction.JOIN_GAME.ordinal())
                        .flip();
                channel.send(bufferToSend, addressToConnect);
                Thread.sleep(Time.MS_PER_S);
                bufferToSend.clear();
            }

            // We open a window to print the gameState
            final SocketAddress serverTemp = serverAddress;
            SwingUtilities.invokeAndWait(() -> createUi(channel, serverTemp));

            channel.configureBlocking(true);

            // Infinite loop to receive the gameState
            while (true) {
                List<Byte> list = new ArrayList<>();

                bufferReceived.flip();
                // First byte is our ID, attributed by the server
                final PlayerID myPlayerID = PlayerID.values()[bufferReceived
                        .get()];

                // The remaining are the gameState
                while (bufferReceived.hasRemaining()) {
                    list.add(bufferReceived.get());
                }
                GameState s = GameStateDeserializer.deserializeGameState(list);
                // We set our gameState, which calls repaint
                SwingUtilities.invokeLater(
                        () -> xBlastComponent.setGameState(s, myPlayerID));
                bufferReceived.clear();
                channel.receive(bufferReceived);
            }
        }
    }

    /**
     * This method print the current gameState, listen to the keyboard actions,
     * and send them to the server given the channel
     * 
     * @param channel
     * @param xBlastComponent
     * @param serverAddress
     * @param bufferToSend
     * @throws IOException
     */
    private static void createUi(DatagramChannel channel,
            SocketAddress serverAddress) {
        JFrame frame = new JFrame("XBlast");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        xBlastComponent = new XBlastComponent();

        ByteBuffer bufferToSend = ByteBuffer.allocate(BYTE_TO_SEND);

        // Used if the player attempts to do an action (like dropping a bomb...)
        Consumer<PlayerAction> c = (PlayerAction) -> {
            bufferToSend.clear();
            bufferToSend.put((byte) PlayerAction.ordinal()).flip();

            try {
                channel.send(bufferToSend, serverAddress);
            } catch (IOException e) {
                throw new Error();
            }

        };
        // We add a KeyListener to our component
        xBlastComponent.addKeyListener(new KeyboardEventHandler(
                KeyboardEventHandler.keyBindings(), c));
        frame.getContentPane().add(xBlastComponent, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        xBlastComponent.requestFocusInWindow();
    }
}