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
	
	public ServerThread(Socket connSocket,common c) {
		this.connSocket = connSocket;
		this.c=c; // Til Web-server opgaven skal denne ikke anvendes
	}
	public void run() {
		try {
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

			// Do the work and the communication with the client here	
			// The following two lines are only an example
			
			String clientSentence = inFromClient.readLine();
			System.out.println(clientSentence);

			makePlayer(clientSentence);
			Server.update();

			while (true) {
				clientSentence = inFromClient.readLine();
				System.out.println(clientSentence);

				Server.update(); // opdater player her, update notificierer bare de andre threads
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}

	private void makePlayer(String name) {
		Pair p = GameLogic.getRandomFreePosition();
		Player player = new Player(name,p,"up");
		Server.addPlayer(player);

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
