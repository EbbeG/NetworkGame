package server;
import game.Pair;
import game.Player;

import java.net.*;
import java.io.*;
public class ServerThread extends Thread{
	Socket connSocket;
	DataOutputStream outToClient;
	Player player;
	
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}
	public void run() {
		try {
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));


			
			String clientSentence = inFromClient.readLine();
			System.out.println(clientSentence);

			makePlayer(clientSentence);

			while (true) {
				clientSentence = inFromClient.readLine();
				System.out.println(clientSentence);



				if (clientSentence.equals("up")) {
					Server.playerMoved(0, -1, "up", player);
				} else if (clientSentence.equals("down")) {
					Server.playerMoved(0, 1, "down", player);
				} else if (clientSentence.equals("right")) {
					Server.playerMoved(1, 0, "right", player);
				} else if (clientSentence.equals("left")) {
					Server.playerMoved(-1, 0, "left", player);
				} else { // player left
					Server.playerLeft(player, this);
					break;
				}

			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}

	private void makePlayer(String name) {
		Pair p = Server.getRandomFreePosition();
		player = new Player(name,p,"up");
		Server.addPlayer(player);
		Server.update();

	}

	public void update(String json) {
		try {
			outToClient.writeBytes(json + "\n");
			System.out.println(json + " from serverThread");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
