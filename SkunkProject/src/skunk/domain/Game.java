package skunk.domain;

import java.util.ArrayList;

import edu.princeton.cs.introcs.StdOut;


public class Game {

	private Player currentPlayer;
	private int playerIndex;
	private boolean isStarted;
	private boolean isCompleted;
	private boolean isLastRound;
	private boolean turnInProgress;
	private int turnsRemainingInFinalRound;
	private Kitty kitty;
	private ArrayList<Roll> rolls;
	private ArrayList<Player> players;
	private ArrayList<Turn> turns;
	private static final String GAME_RULES = "Rules of Skunk\r\n" + 
			"\r\n" + 
			"DIRECTIONS FOR PLAYING\r\n" + 
			"\r\n" + 
			"The object of the game is to accumulate a score of 100 points or more. A score is made by rolling the dice\r\n" +
			"and combining the points on the two dice.\r\n\r\n" + 
			"For example: A 4 and 5 would be 9 points - if the player decides to take another roll of the dice and\r\n" + 
			"turns up a 3 and 5 (8 points), he would then have an accumulated total of 17 points for the two rolls.\r\n" + 
			"The player has the privilege of continuing to shake to increase his score or of passing the dice to wait\r\n" +
			"for the next series, thus preventing the possibility of rolling a Skunk and losing his score.\r\n" + 
			"\r\n" + 
			"PENALTIES:\r\n" + 
			"\r\n" + 
			"A skunk in any series voids the score for that series only and draws a penalty of 1 chip placed in the \"kitty,\" and loss of dice.\r\n" + 
			"\r\n" + 
			"A skunk and a deuce voids the score for that series only and draws a penalty of 2 chips placed in the \"kitty,\" and loss of dice.\r\n" + 
			"\r\n" + 
			"TWO skunks void the ENTIRE accumulated score and draws a penalty of 4 chips placed in the \"kitty,\" and loss of dice. Player must again start to score from scratch.\r\n" + 
			"\r\n" + 
			"Any number can play. [Assume at least two players!] The suggested number of chips to start is 50.\r\n" +
			"There are sufficient chips in the box to allow 8 players to start with 50 chips by placing a par value of \"one\" on white chips, 5 for 1 on red chips and 10 for 1 on the blue chips.\r\n" + 
			"\r\n" + 
			"The first player to accumulate a total of 100 or more points can continue to score as many points over 100 as he believes is needed to win.\r\n" +
			"When he decides to stop, his total score is the \"goal.\" Each succeeding player receives one more chance to better the goal and end the game.\r\n" + 
			"\r\n" + 
			"The winner of each game collects all chips in \"kitty\" and in addition five chips from each losing player or 10 chips from any player without a score.\r\n";
	
	public Game() {
		this.kitty = new Kitty();
		this.rolls = new ArrayList<Roll>();
		this.players = new ArrayList<Player>(); 
		this.turns = new ArrayList<Turn>();
		this.isStarted = false;
		this.isCompleted = false;
		this.isLastRound = false;
		this.playerIndex = -1;
	}

	public void setPlayerName(Player player, String name) {
		player.setName(name);
	}
	
	public String getPlayerName(Player player) {
		return player.getName();
	}
	
	public Player getPlayer(int index) {
		return players.get(index);
	}
	
	/*
	 * Rolls dice, and checks if a skunk, deuce, or double is rolled and distributes
	 * chips from the player to the kitty accordingly.
	 */
	public void rollAndUpdateScores() {
		Roll roll = getCurrentRoll();
		Player player = getCurrentPlayer();
		Turn turn = getCurrentTurn();
		
		roll.rollDiceCheckAndRecord();
		if (roll.isSkunk()) {
			turn.clearScore();
			player.removeChips(1);
			kitty.addChips(1);
		} else if (roll.isDeuce()) {
			turn.clearScore();
			player.removeChips(2);
			kitty.addChips(2);
		} else if (roll.isDouble()) {
			turn.clearScore();
			player.clearPoints();
			player.removeChips(4);
			kitty.addChips(4);
		} else {
			turn.increaseScore(roll.getLastDiceRoll());
		}
	}
	
	// Returns a string that provides a log of the rolls for the current turn.
	public String getRollsForTurn(Roll roll) {
		String s = "";
		ArrayList<int[]> rollHistory = roll.getRollHistory();
		for (int i = 0; i < rollHistory.size(); i++) {
			int[] tempArray = rollHistory.get(i);
			s += "Roll #" + (i + 1) 
					+ " => " + Integer.toString(tempArray[0]) 
					+ " + " + Integer.toString(tempArray[1]) + "\n";
		}
		return s;
	}
	
	// Adds turn score to the current player's point total.
	public void endTurn() {
		Turn currentTurn = getCurrentTurn();
		currentTurn.endTurn();
	}
	
	/*
	 * Checks to see if the current player has reached 100 points. Implement this
	 * method at the end of each turn.
	 */	
	public boolean checkForFinalRound() {
		Player currentPlayer = getCurrentPlayer();
		if (currentPlayer.getPoints() >= 100 && !isLastRound) {
			StdOut.println("Final round has started! Try to beat " + currentPlayer.getName() + "'s score!\n"
					+ "Score to beat: " + currentPlayer.getPoints() + "\n");
			isLastRound = true;
			turnsRemainingInFinalRound = players.size() - 1;
			return isLastRound;
		}
		return isLastRound;
	}
	
	// Returns the player with the highest point total
	public Player getWinnerSoFar() {
		Player winnerSoFar = players.get(0);
		for (Player player : players) {
			if (player.getPoints() > winnerSoFar.getPoints()) {
				winnerSoFar = player;
			}
		}
		return winnerSoFar;
	}
	
	// Distributes chips in Kitty to the winning player.
	public void giveKittyChipsToWinner() {
		Player winner = getWinnerSoFar();
		int purse = kitty.getChips();
		winner.addChips(purse);
		kitty.removeChips(purse);
		StdOut.println("Kitty (" + purse + " chips) => " + winner.getName());
	}
	
	/*
	 * Accounts for if a player runs out of chips. Winner receives whatever chips
	 * the losing player has left. Distributes chips from losing players to the winning player
	 * based on the game rules.
	 */	
	public void giveLoserChipsToWinner() {
		Player winner = getWinnerSoFar();
		for (Player player : players) {
			if (player == winner) {
				continue;
			} else {
				if (player.getPoints() == 0) {
					if (player.getChips() < 10) {
						StdOut.println(player.getName() + " (" + player.getChips() + " chips) => " + winner.getName());
						winner.addChips(player.getChips());
						player.removeChips(player.getChips());
					} else {
						StdOut.println(player.getName() + " (10 chips) => " + winner.getName());
						winner.addChips(10);
						player.removeChips(10);
					}
				} else {
					if (player.getChips() < 5) {
						StdOut.println(player.getName() + " (" + player.getChips() + " chips) => " + winner.getName());
						winner.addChips(player.getChips());
						player.removeChips(player.getChips());
					} else {
						StdOut.println(player.getName() + " (5 chips) => " + winner.getName());
						winner.addChips(5);
						player.removeChips(5);
					}
				}
			}
		}
	}
	
	/*
	 * Changes isCompleted global variable to true when turns in final round
	 * reaches zero. Method to be used in SkunkApp via controller to decrease
	 * final round counter when a new turn starts.
	 */	
	public boolean checkForEndOfGame() {
		if (isLastRound) {
			if (turnsRemainingInFinalRound == 0) {
				completeGame();
				return isCompleted;
			}
			turnsRemainingInFinalRound--;
		}
		return isCompleted;
	}

	public Boolean rollIsSkunk(Roll roll) {
		return roll.isSkunk();
	}
	
	public Boolean rollIsDeuce(Roll roll) {
		return roll.isDeuce();
	}
	
	public Boolean rollIsDouble(Roll roll) {
		return roll.isDouble();
	}
	
	public String getRollToString(Roll roll) {
		return roll.toString();
	}


	public int getPlayerScore(Player player) {
		return player.getPoints();
	}


	public int getTurnScore(Turn turn) {
		return turn.getScore();
	}


	public int getPlayerChips(Player player) {
		return player.getChips();
	}


	public int getKittyChips() {
		return kitty.getChips();
	}
	
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

	// Start a new turn IF a game is in progress
	public void startNewTurn() {
		if(this.isStarted == true) { 
			turnInProgress = true; 
			currentPlayer = this.determineCurrentPlayer();
			Turn myTurn = new Turn(this.currentPlayer);
			Roll myRoll = new Roll();
			turns.add(myTurn); 
			rolls.add(myRoll);
		}
		else
			turnInProgress = false; 
	}

	public int getNumberOfTurns() {
		return turns.size();
	}

	public boolean getTurnStatus() {
		return turnInProgress;
	}
	
	
	public void addPlayer(String playerName) {
		if (this.isStarted == false) {
			Player player = new Player(playerName);
			players.add(player);
		}		
	}

	// Returns the number of players in the array list
	public int getNumberOfPlayers() {
		int numberOfPlayers = players.size(); 
		return numberOfPlayers;
	}

	public String printPlayers() {
		return players.toString();
	}

	public void clearAllPlayers() {
		players.clear();
	}

	/*
	 * Method to return whose turn it currently is. Making this method private
	 * so it can't be called outside of the game program. Will solve a
	 * duplication issue
	 */	
	private Player determineCurrentPlayer() {
		int numPlayers = players.size(); 
		
		if(this.playerIndex == -1 && numPlayers > 0) { 
			playerIndex = 0;
			currentPlayer = players.get(playerIndex);
		}
		else if (this.playerIndex != -1 && playerIndex < numPlayers && numPlayers > 0) {
			currentPlayer = players.get(playerIndex);
		}
		else if (this.playerIndex >= numPlayers && numPlayers > 0) { 
			playerIndex = playerIndex - numPlayers;
			currentPlayer = players.get(playerIndex);
		}
		
		playerIndex++;
		return currentPlayer;
	}
	
	public Player getCurrentPlayer() { 
		return this.currentPlayer;
	}
	
	public String getCurrentPlayerName() {
		return getCurrentPlayer().getName();
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public Turn getCurrentTurn() {
		return turns.get(turns.size() - 1);
	}
	
	public Turn getTurn(int index) {
		return turns.get(index);
	}
	
	public Roll getCurrentRoll() {
		return rolls.get(rolls.size() - 1);
	}
	
	public boolean isCompleted() {
		return isCompleted;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public String getGameRules() {
		return GAME_RULES;
	}
	
	/*
	 * Prints total score and chip count for each player in the game. Returns a
	 * string formatted as an end-of-game summary.
	 */
	public String toString() {
		Player winner = getWinnerSoFar();
		String gameSummary = "| Player\t|\tScore\t|\tChips\t|\n";
		for (Player player : players) {
			String playerName = player.getName();
			if (player == winner) {
				playerName = player.getName() + "*";
			}
			gameSummary += "\n| " + playerName + " \t|\t" + player.getPoints() + "\t|\t" + player.getChips() + "\t|\n";
		}
		gameSummary += "\n* = Winner\n";
		return gameSummary;
	}
}
