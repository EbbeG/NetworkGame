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
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                String serverResponse = inFromServer.readLine();
                System.out.println(serverResponse + " from clientthread");

                if (!serverResponse.contains("gems")) {
                    List<Player> players = objectMapper.readValue(serverResponse, new TypeReference<>() {});

                    System.out.println("Players from clientthread");
                    System.out.println(players);

                    Gui.updateFromServer(players);
                } else { // gem json
                    String json = serverResponse.substring(4); // cut of the "gems" part
                    List<Pair> gemLocations = objectMapper.readValue(json, new TypeReference<>() {});
                    Gui.updateGems(gemLocations);


                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
