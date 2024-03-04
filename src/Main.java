import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        final int PORT = 8080;
        String input;

        // open sockets with both players

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server starting, waiting for clients...");

        try {

            // open socket with player 1

            Socket player1 = serverSocket.accept(); // waiting for player 1 to connect the server
            System.out.println("Player one connected");

            // reader - to read moves from player 1 and update player 2
            // writer - to write moves from player 2 to player 1
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            PrintWriter writer1 = new PrintWriter(player1.getOutputStream(), true);

            // open socket with player 2

            Socket player2 = serverSocket.accept(); // waiting for player 2 to connect the server
            System.out.println("Player two connected");
            // reader - to read moves from player 2 and update player 1
            // writer - to write moves from player 1 to player 2
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            PrintWriter writer2 = new PrintWriter(player2.getOutputStream(), true);

            // sending to players who first
            // the first player to connect the server starts

            writer1.println("first");
            writer2.println("second");

            /* get from the first player the board size and send to the second */
            input = reader1.readLine(); // rows
            writer2.println(input);
            input = reader1.readLine(); // cols
            writer2.println(input);

            /* In order to be able to play more than one time, for each game, in the end of it, the server will listen to the answer of first player to connect if he wants to play again */

            do {
                GameLoop(reader1, writer1, reader2, writer2);
                /* Getting answer from player 1 about playing another game, update player 2 and start the game loop again if there is a need */
                input = reader1.readLine();
                writer2.println(input);
            } while (input.contains("yes"));

            // close connections
            player1.close();
            player2.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void GameLoop(BufferedReader reader1, PrintWriter writer1, BufferedReader reader2, PrintWriter writer2)
    {
        boolean isPlay = true; // runs until someone win, or tie

        try {
            /* The game loop */
            while(isPlay)
            {
                String inputLine;

                // get from first and send to second
                inputLine = reader1.readLine(); // row
                writer2.println(inputLine);

                // get status of the game - from player 2
                inputLine = reader2.readLine();
                if (inputLine.contains("game over"))
                {
                    isPlay = false;
                }
                else
                {
                    // get from second and send to first
                    inputLine = reader2.readLine();
                    writer1.println(inputLine);

                    // get the status of the game from player 2 right after his turn
                    inputLine = reader2.readLine();
                    if (inputLine.contains("game over"))
                    {
                        isPlay = false;
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}