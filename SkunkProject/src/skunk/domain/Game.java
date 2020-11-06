package skunk.domain;

import java.util.ArrayList;
import java.util.Arrays;


public class Game {

	private boolean isStarted = false; 
	private boolean isCompleted = false;
	private int numberOfTurns; 
	private boolean turnInProgress = false; 
	private ArrayList<Player> players = new ArrayList(); 
	

	
	public String getStatus() {
		if(isStarted == false && isCompleted == false) { 
			return "The Game has not started yet";
		}
		if(isStarted == true && isCompleted == false) {
			return "The Game is Afoot!";
		}
		else {
			return "The Game has ended";
		}
		
	}

	public void startGame() {
		this.isStarted = true;
		
	}

	public void completeGame() {
		this.isCompleted = true;
		
		
	}

	//Start a new turn IF a game is in progress
	public void startNewTurn() {
		if(this.isStarted == true) { 
			turnInProgress = true;
			numberOfTurns ++; 
		}
		else
			turnInProgress = false; 
		
		
	}

	public int getNumberOfTurns() {
		// TODO Auto-generated method stub
		return numberOfTurns;
	}

	public boolean getTurnStatus() {
		// TODO Auto-generated method stub
		return turnInProgress;
	}

	public void addPlayer(String playerName) {
		if(this.isStarted == false) {
			Player player = new Player(playerName);
			players.add(player);
		}
		
		
	}

	//returns the number of players in the array list
	public int getNumberOfPlayers() {
		// TODO Auto-generated method stub
		int numberOfPlayers = players.size(); 
		return numberOfPlayers;
	}

	public String printPlayers() {
		// TODO Auto-generated method stub
		return players.toString();
	}

	public void clearAllPlayers() {
		// TODO Auto-generated method stub
		players.clear();
		
	}

	//method to return whose turn it currently is
	public Player whoseTurn() {
		int index = (numberOfTurns-1)%(players.size()); 
		
		Player currentPlayer = players.get(index);
		return currentPlayer;
		
	}

}
