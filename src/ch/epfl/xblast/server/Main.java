package ch.epfl.xblast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;

/**
 * Class main used by the server to send the gameState, receive the actions of
 * the clients'player and compute the next gameState
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public class Main {

    private static final int NB_OF_PLAYER_PER_DEFAULT = 4;
    private static final int BYTES_TO_RECEIVE = 1;

    // Level as shown in the video
    private static final Level INITIAL_LEVEL = Level.LevelWithPlayerAtFourEdges
            .initialDefaultLevel();

    /**
     * The main method opens a channel, sends the gameState to the clients,
     * receives their actions and computes the next gameState
     * 
     * @param number
     *            of players
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args)
            throws IOException, InterruptedException {

        // getting the number of players
        int nbPlayers = args.length > 0 ? Integer.parseInt(args[0])
                : NB_OF_PLAYER_PER_DEFAULT;

        // opening the channel
        DatagramChannel channel = DatagramChannel
                .open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(2016));
        channel.configureBlocking(true);

        // 1st step : the client join the game
        // giving the id : the first client get the id number 0, etc...

        Map<SocketAddress, PlayerID> players = new HashMap<>();
        PlayerID[] ids = PlayerID.values();

        ByteBuffer bufferReceived = ByteBuffer.allocate(BYTES_TO_RECEIVE);
        SocketAddress addressClient;

        // getting the addresses and mapping them to a player
        while (players.keySet().size() != nbPlayers) {
            if (((addressClient = channel.receive(bufferReceived)) != null)) {
                players.putIfAbsent(addressClient, ids[players.size()]);
                bufferReceived.clear();
            }
        }
        // Sending the GameState
        GameState s = new GameState(INITIAL_LEVEL.initialGameState().board(),
                INITIAL_LEVEL.initialGameState().players());

        // Storing of the startTime
        long startTime = System.nanoTime();

        while (!s.isGameOver()) {
            // Serialization
            List<Byte> gameStateSerialized = GameStateSerializer
                    .serialize(INITIAL_LEVEL.boardPainter(), s);

            ByteBuffer bufferToSend = ByteBuffer
                    .allocate(gameStateSerialized.size() + 1);

            bufferToSend.put((byte) 0);
            for (Byte b : gameStateSerialized) {
                bufferToSend.put(b);
            }

            // Send
            for (SocketAddress addressC : players.keySet()) {
                // We also send the id of the player
                bufferToSend.put(0, (byte) players.get(addressC).ordinal())
                        .flip();

                channel.send(bufferToSend, addressC);
            }

            // Computes if we have to sleep
            long timeTillNextTick = ((startTime
                    + (long) (s.ticks() + 1) * Ticks.TICK_NANOSECOND_DURATION
                    - System.nanoTime())) / Time.NS_PER_MS;

            if (timeTillNextTick > 0) {
                Thread.sleep(timeTillNextTick);
            }

            // While the server is sleeping, it gets the actions of the players
            channel.configureBlocking(false);

            Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<>();
            Set<PlayerID> bombDropEvents = new HashSet<>();

            SocketAddress adresseClient;
            while ((adresseClient = channel.receive(bufferReceived)) != null) {

                PlayerID id = players.get(adresseClient);

                bufferReceived.rewind();
                byte currentBuffer = bufferReceived.get();

                // According to the buffer we create a new event
                if (currentBuffer == 0) {
                } else if (currentBuffer == PlayerAction.DROP_BOMB.ordinal()) {
                    bombDropEvents.add(id);

                } else if (currentBuffer == PlayerAction.STOP.ordinal()) {
                    speedChangeEvents.put(id, Optional.empty());

                } else {
                    speedChangeEvents.put(id,
                            Optional.of(Direction.values()[currentBuffer - 1]));
                }
                bufferReceived.clear();
            }
            // and it computes the next gameState
            s = s.next(speedChangeEvents, bombDropEvents);
        }
        // If there is a winner, we print it
        if (s.winner().isPresent()) {
            System.out.println("The winner is " + s.winner());
        }
        channel.close();
    }
}