package server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.*;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public synchronized static void playerMoved(int delta_x, int delta_y, String direction, Player player) {
		player.setDirection(direction);
		int x = player.getXpos(), y = player.getYpos();

		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') { // wall
			player.addPoints(-1);
		}
		else {
			// collision detection
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
				player.addPoints(10);
				//update the other player
				p.addPoints(-10);
				Pair pa = getRandomFreePosition();
				p.setLocation(pa);
			} else
				player.addPoints(1);

			Pair newpos = new Pair(x+delta_x,y+delta_y);
			player.setLocation(newpos);
		}

		Server.update();
	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public static Pair getRandomFreePosition()
	// finds a random new position which is not wall
	// and not occupied by other players
	{
		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden
						foundfreepos = false;
				}

			}
		}
		Pair p = new Pair(x,y);
		return p;
	}

	public synchronized static void playerLeft(Player player, ServerThread serverThread) {
		// need to remove serverthread???
		players.remove(player);
		Server.update();
	}
}
