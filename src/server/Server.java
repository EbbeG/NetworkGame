package server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.GameLogic;
import game.Pair;
import game.Player;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
	public static List<Player> players = new ArrayList<>();
	public static List<ServerThread> serverThreads = new ArrayList<>();
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		common c = new common("eksempel");
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ServerThread serverThread = new ServerThread(connectionSocket,c);
			serverThreads.add(serverThread);

			serverThread.start();
		}
	}

//	public /*synchronized*/ static void makePlayer(String name) {
//		Pair p = GameLogic.getRandomFreePosition();
//		Player player = new Player(name,p,"up");
//		players.add(player);
//		System.out.println(players);
//
//	}

	public synchronized static void addPlayer(Player player) {
		players.add(player);
		System.out.println(players);
	}

	public synchronized static void update() {
		try {
			String json = objectMapper.writeValueAsString(players);
			System.out.println(json + "From server");
			for (ServerThread serverThread : serverThreads) {
				serverThread.update(json);
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}
}
