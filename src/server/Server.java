package server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.*;

import java.net.*;
import java.util.*;

public class Server {
	public static List<Player> players = new ArrayList<>();
	public static List<ServerThread> serverThreads = new ArrayList<>();
	public static List<Pair> gems = new ArrayList<>();
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);

		Timer gemSpawnTimer = new Timer();
		gemSpawnTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Pair gemLocation = getRandomFreePosition();
				gems.add(gemLocation);
				Server.gemUpdate();
			}
		}, 20000, 5000);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ServerThread serverThread = new ServerThread(connectionSocket);
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
			} else {
				player.addPoints(1);
			}

			Pair newpos = new Pair(x+delta_x,y+delta_y);
			player.setLocation(newpos);
			if (gemTaken(newpos)) {
				player.addPoints(50);
				Server.gemUpdate();
			}
		}

		Server.update();
	}

	private static boolean gemTaken(Pair newpos) {
		for (Pair gem : gems) {
			if (gem.getX() == newpos.getX() && gem.getY() == newpos.getY()) {
				gems.remove(gem);
				return true;
			}
		}
		return false;
	}



	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public synchronized static Pair getRandomFreePosition()
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
				for (Pair gem : gems) {
					if (gem.getX() == x && gem.getY()==y) // gem på placering
						foundfreepos = false;
				}

			}
		}
		Pair p = new Pair(x,y);
		return p;
	}

	public synchronized static void playerLeft(Player player, ServerThread serverThread) {
		serverThreads.remove(serverThread);
		players.remove(player);
		Server.update();
	}

	private synchronized static void gemUpdate() {
		try {
			String json = "gems" + objectMapper.writeValueAsString(gems);
			System.out.println("gem JSON " + json);
			for (ServerThread serverThread : serverThreads) {
				serverThread.update(json);
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}
}
