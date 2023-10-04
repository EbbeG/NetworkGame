package server;
import game.GameLogic;
import game.Pair;
import game.Player;

import java.net.*;
import java.io.*;
public class ServerThread extends Thread{
	Socket connSocket;
	common c;
	DataOutputStream outToClient;
	Player player;
	
	public ServerThread(Socket connSocket,common c) {
		this.connSocket = connSocket;
		this.c=c; // Til Web-server opgaven skal denne ikke anvendes
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

				// move collision logic here or move it to server?

				if (clientSentence.equals("up")) {
					Server.playerMoved(0, -1, "up", player);
				} else if (clientSentence.equals("down")) {
					Server.playerMoved(0, 1, "down", player);
				} else if (clientSentence.equals("right")) {
					Server.playerMoved(1, 0, "right", player);
				} else if (clientSentence.equals("left")) {
					Server.playerMoved(-1, 0, "left", player);
				}

			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}

	private void makePlayer(String name) {
		Pair p = GameLogic.getRandomFreePosition();
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
