package game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class ClientThread extends Thread {

    BufferedReader inFromServer;
    ObjectMapper objectMapper = new ObjectMapper();

    public ClientThread(BufferedReader inFromServer) {
        this.inFromServer = inFromServer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
                String serverResponse = inFromServer.readLine();
                System.out.println(serverResponse + " from clientthread");

                List<Player> players = objectMapper.readValue(serverResponse, new TypeReference<>(){});

                System.out.println("Players from clientthread");
                System.out.println(players);

                Gui.updateFromServer(serverResponse); // pass serverUpdate as a list of players

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
