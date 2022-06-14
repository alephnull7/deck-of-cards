// Programmer: Gregory Smith
// Date: 04/26/2022
// Program: Deck of Cards
// Reference: https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html,
// https://docs.oracle.com/javase/8/docs/api/java/io/File.html,
// https://www.tutorialspoint.com/java-program-to-list-all-files-in-a-directory-recursively,
// https://www.cs.montana.edu/users/paxton/cribbage.html,
// https://en.wikipedia.org/wiki/Rules_of_cribbage,
// https://www.wikihow.com/Play-Cribbage
//
// Purpose: Use the user defined Card and DeckStack classes to simulate the behavior of
// a cribbage game.
//
// IDE: Eclipse

package deckOfCards;

// array printing, sorting, and copying
import java.util.Arrays;

// System.in and File use
import java.util.Scanner;

// java collections imports
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;

// imports for instructions file
import java.io.File;
import java.io.FileNotFoundException;

public class Cribbage {
	private int numPlayers;
	
	// ending conditions
	private int winPoints;
	private int maxPlayPoints;

	// play rules determined by numPlayers
	private int cardsPerPlayer;
	private int cardsPerPlayerAfterDiscard;
	private int cardsDealtToCrib;
	private int cardsDiscardToCribPerPlayer;
	private int cribSize;
	private int cardsInCrib;

	// game state fields
	private DeckStack deckStack;
	private int[] playersScoreArray;
	private int gameRound;
	
	// game round fields
	private Card[][] currentCardHands;
	private Card[] currentCrib;
	private Stack<Card> showStack;
	private int dealerIndex;
	
	public Cribbage() {
		// set minimum about of points needed to win
		this.winPoints = 121;
		this.maxPlayPoints = 31;
	
		// welcome cribbage
		welcome();
		//printInstructionsFromFile();
		
		// get numPlayers from user input
		setNumPlayers();
		
		// determine first dealer from user input
		chooseInitialDealer();

		// populate fields for numPlayers value
		setCardsPerPlayer();
		setCardsDealtToCrib();
		setDiscardToCribPerPlayer();

		// derivative field
		this.cribSize = this.numPlayers * this.cardsDiscardToCribPerPlayer + this.cardsDealtToCrib;
		this.cardsPerPlayerAfterDiscard = this.cardsPerPlayer - this.cardsDiscardToCribPerPlayer;

		// initialized to 0
		this.playersScoreArray = new int[this.numPlayers];
		
		// get ready for first round of play
		setNewRound();
		this.gameRound = 1;

	} // end of constructor
	
	public void printInstructionsFromFile() {
		// IDE configuration can change relative location of instructions file,
		// so we need to find where it is
		File workingDirectory = new File(System.getProperty("user.dir"));
		String instructionsFile = "instructions.txt"; 
		String instructionsPath = findFileRecursive(workingDirectory, instructionsFile);
		
		try {
			Scanner instructionsScanner = new Scanner(new File(instructionsPath));
			
			while (instructionsScanner.hasNextLine()) {
				System.out.println(instructionsScanner.nextLine());
				
			} // end of while
			
			instructionsScanner.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Instructions not found :(\n");
			
		} // end of try-catch
		
	} // end of printInstructionsFromFile
	
	public void welcome() {
		System.out.println("Welcome to Cribbage!");
		System.out.println();
		
		Scanner inputUser = new Scanner(System.in);
		System.out.print("Would you like to see program instructions? (\"yes\" for yes): ");
		String inputString = inputUser.nextLine();
		
		System.out.println();
		if (inputString.trim().equalsIgnoreCase("yes")) {
			printInstructionsFromFile();
			System.out.println();
			
		} // end of if
		
	} // end of welcome
	
	// adapted recursive file finding method, that looks for a 
	// file in a designated directory
	public String findFileRecursive(File directory, String fileName) {
		File filesList[] = directory.listFiles();
		
		for(File file : filesList) {
			
			if (file.isFile() && file.getName().equals(fileName)) {
				return file.getPath();
	            
			} else if (!file.isFile() && file.getName().startsWith("gSmith")) {
        	 	return findFileRecursive(file, fileName);
    	 	
			} // end of if/else
			
		} // end of for
		return null;
		
	} // end of findFileRecursively
	
	public void setNewRound() {
		// new shuffled deck
		setShuffledDeckStack();

		// initialized hands to null
		setNullCurrentCardHands();
		setNullCurrentCrib();
		
		// empty show stack
		setEmptyShowStack();
		
		// set fields for next round
		this.cardsInCrib = 0;
		this.dealerIndex = (this.dealerIndex + 1) % this.numPlayers;
		this.gameRound += 1;
		
	} // end of setNewRound
	
	public void setNullCurrentCardHands() {
		this.currentCardHands = new Card[this.numPlayers][this.cardsPerPlayer];
		
	} // end of setNullCurrentCardHands
	
	public void setNullCurrentCrib() {
		this.currentCrib = new Card[this.cribSize];
		
	} // end of setNullCurrentCrib
	
	public void setEmptyShowStack() {
		this.showStack = new Stack<Card>();
		
	} // end of setEmptyShowStack
	
	public void populateShowStack() {
		Card[] currentHand;
		
		// since we are pushing to a stack, we want to go in reverse show order,
		// starting with the dealer
		int playerIndex = this.dealerIndex;
		for (int playerCount = 0; playerCount < this.numPlayers; playerCount++) {
			currentHand = this.getPlayerHand(playerIndex);
			
			// push currentHand into stack
			// since we will later sort the hand as an array, hand order does not matter
			for (int handIndex = 0; handIndex < currentHand.length; handIndex++) {
				this.showStack.push(currentHand[handIndex]);
				
			} // end of inner for loop
			
			// next player is "right"
			// use proper mod function for possible negative values
			playerIndex = Math.floorMod(playerIndex - 1, this.numPlayers);
			
		} // end of outer for loop
		
	} // end of populateShowStack

	public void setCardsPerPlayer() {
		switch (this.numPlayers) {
		case 2:
			this.cardsPerPlayer = 6;
			break;

		case 3:
		case 4:
			this.cardsPerPlayer = 5;
			break;

		default:
			this.cardsPerPlayer = -1;
			break;

		} // end of switch-case

	} // end of setCardsPerPlayer

	public int getCardsPerPlayer() {
		return this.cardsPerPlayer;

	} // end of getCardsPerPlayer

	public void setCardsDealtToCrib() {
		switch (this.numPlayers) {
		case 2:
		case 4:
			this.cardsDealtToCrib = 0;
			break;

		case 3:
			this.cardsDealtToCrib = 1;
			break;

		default:
			this.cardsDealtToCrib = -1;
			break;

		} // end of switch-case

	} // end of setCardsDealtToCrib

	public int getCardsDealtToCrib() {
		return this.cardsDealtToCrib;

	} // end of getCardsDealtToCrib

	public void setDiscardToCribPerPlayer() {
		switch (this.numPlayers) {
		case 2:
			this.cardsDiscardToCribPerPlayer = 2;
			break;

		case 3:
		case 4:
			this.cardsDiscardToCribPerPlayer = 1;
			break;

		default:
			this.cardsDiscardToCribPerPlayer = -1;
			break;

		} // end of switch-case

	} // end of setDiscardToCribPerPlayer

	public int getDiscardToCribPerPlayer() {
		return this.cardsDiscardToCribPerPlayer;

	} // end of getDiscardToCribPerPlayer

	public void setPlayerScore(int playerIndex, int pointsScored) {
		this.playersScoreArray[playerIndex] += pointsScored;

	} // end of setPlayersScore

	public void setShuffledDeckStack() {
		this.deckStack = new DeckStack();

	} // end of setShuffledDeckStack
	
	public void playRound() {
		deal();
		
		if(!this.findIsGameOver()) {
			doPlay();
			
		} // end of if
		
		if(!this.findIsGameOver()) {
			doShow();
			
		} // end of if
		
		if(!this.findIsGameOver()) {
			setNewRound();
			
		} // end of if
		
		// if game is over print final stats
		if(this.findIsGameOver()) {
			printPlayersScore("the game");
			
		} // end of if
		
	} // end of playRound
	
	public void printPlayersScore(String roundPhase) {
		System.out.printf("At the end of %s, we have:%n", roundPhase);
		
		for (int arrayIndex = 0; arrayIndex < this.numPlayers; arrayIndex++) {
			System.out.printf("Player %d: %d points%n", arrayIndex + 1, this.playersScoreArray[arrayIndex]);
			
		} // end of for
		System.out.println();
		
	} // end of printPlayersScore
	
	public void deal() {
		dealToPlayers();
		dealToCrib();
		dealMessage();
		discardToCrib();
		getStarter();
		populateShowStack();
		
	} // end of deal
	
	public void doPlay() {
		Card[] playCardArray = new Card[this.cardsPerPlayerAfterDiscard * this.numPlayers];
		
		int playPointSum;
		int cardsPlayed = 0;
		int noPlayCount = 0;
		int lastPlayIndex = -1;
		int playerIndex = (this.dealerIndex + 1) % this.numPlayers;
		int currentHandSize, cardIndex, cardPoints;
		
		Card cardTemp;
		
		Scanner inputUser = new Scanner(System.in);
		
		System.out.println("This is the start of play for the round.");
		System.out.println();
		
		// we play until all cards have been played
		while (cardsPlayed < playCardArray.length) {
			
			playPointSum = 0;
			// we try to play to 31 each time
			while (playPointSum < this.maxPlayPoints) {
				
				// if player has playable cards, they get a turn
				if (playerCanPlayCard(playerIndex, playPointSum)) {
					printPlayerTurn(playerIndex);
					System.out.printf("The total number of points played is %d.%n", playPointSum);
					System.out.println("These are the cards played since last reset:");
					System.out.println(Arrays.toString(removeNullFromCardArray(playCardArray)));
					pressEnterToContinue();
					
					System.out.println("This is your hand:");
					System.out.println(Arrays.toString(this.getPlayerHand(playerIndex)));
					
					currentHandSize = this.getPlayerHand(playerIndex).length;
					
					do {
						System.out.printf("Which card would you like to play? (1-%d): ", currentHandSize);
						
						cardIndex = getIntFromUser(inputUser, 1, currentHandSize) - 1; // we want internal index
						cardPoints = this.getPlayerHand(playerIndex)[cardIndex].getRankPoints();
						
						if (cardPoints > (this.maxPlayPoints - playPointSum)) {
							System.out.printf("Playing that card would exceed %d. Pick Again.%n", this.maxPlayPoints);
							
						} // end of if
						
					} while (cardPoints > (this.maxPlayPoints - playPointSum)); // end of do-while
					
					cardTemp = playCard(playerIndex, cardIndex);
					System.out.println("\nThis is your new hand:");
					System.out.println(Arrays.toString(this.getPlayerHand(playerIndex)));
					System.out.println();
					
					playCardArray[cardsPlayed++] = cardTemp;
					
					playPointSum += cardTemp.getRankPoints();
					lastPlayIndex = playerIndex;
					noPlayCount = 0;
					
					// check for fifteen points
					if (playPointSum == 15) {
						int pointsScored = 2;
						System.out.printf("Player %d got a fifteen for %d points!%n%n", 
								lastPlayIndex + 1, pointsScored);
						setPlayerScore(lastPlayIndex, pointsScored);
						
					} // end of if
					
					// get non-null array of played card since reset
					Card[] playCardArrayNonNull = removeNullFromCardArray(playCardArray);
					
					// check for pair points
					int pointsScored = getPairPointsPlay(playCardArrayNonNull);
					if (pointsScored > 0) {
						System.out.printf("Player %d got pairs for %d points!%n%n", lastPlayIndex + 1, pointsScored);
						setPlayerScore(lastPlayIndex, pointsScored);
						
					} // end of if
					
					// check for run points
					if (playCardArrayNonNull.length >= 3) {
						pointsScored = getRunPointsPlay(playCardArrayNonNull);
						
						if (pointsScored > 0) {
							System.out.printf("Player %d got a run for %d points!%n%n", 
									lastPlayIndex + 1, pointsScored);
							setPlayerScore(lastPlayIndex, pointsScored);
							
						} // end of inner if
						
					} // end of outer if
					
				} else if (noPlayCount == this.numPlayers) {
					int pointsScored = 1;
					System.out.printf("Player %d got a \"go\" for %d point!%n%n", lastPlayIndex + 1, pointsScored);
					setPlayerScore(lastPlayIndex, pointsScored);
					break;
				
				} else {
					printPlayerTurn(playerIndex);
					System.out.printf("The total number of points played is %d.%n", playPointSum);
					System.out.printf("Player %d can not play.%n%n", playerIndex + 1);
					noPlayCount++;
					
				} // end of if/else
				
				// if game is over break inner while
				if(this.findIsGameOver()) {
					break;
					
				} // end of if
				
				playerIndex = (playerIndex + 1) % this.numPlayers;
			} // end of inner while loop
			
			if (playPointSum == this.maxPlayPoints) {
				int pointsScored = 2;
				System.out.printf("Player %d got a \"go\" for %d points!%n%n", lastPlayIndex + 1, pointsScored);
				setPlayerScore(lastPlayIndex, pointsScored);
				
			} // end of if
			
			// if game is over break outer while
			if(this.findIsGameOver()) {
				break;
				
			} // end of if
			
			// next play starts to the left of the person who played a card last (got the go)
			playerIndex = (lastPlayIndex + 1) % this.numPlayers;
			
			// discard played cards on reset
			playCardArray = Arrays.copyOfRange(playCardArray, cardsPlayed, playCardArray.length);
			
			// reset number of card for next play
			cardsPlayed = 0;
			
		} // end of outer while loop
		
		// if game is over return to calling method
		if(this.findIsGameOver()) {
			return;
			
		} // end of if
		
		printPlayersScore("play");
		
	} // end of doPlay
	
	public void doShow() {
		int showHandSize = this.cardsPerPlayerAfterDiscard + 1;
		Card[] currentHand = new Card[showHandSize];
		
		System.out.println("This is the start of show for the round.");
		System.out.println();
		
		// show order starts to left of dealer
		int playerIndex = (this.dealerIndex + 1) % this.numPlayers;
		for (int playerCount = 0; playerCount < this.numPlayers; playerCount++) {
			
			// populate currentHand from stack
			for (int handIndex = 0; handIndex < showHandSize - 1; handIndex++) {
				currentHand[handIndex] = this.showStack.pop();
				
			} // end of inner for loop
			
			// add starter to hand
			currentHand[showHandSize -1] = this.deckStack.peekCard();
			
			printPlayerTurn(playerIndex);
			pressEnterToContinue();
			System.out.println("This is your show hand:");
			System.out.println(Arrays.toString(currentHand));
			System.out.println();
			
			// score hand
			scoreShowHand(currentHand, playerIndex, false);
			
			// if game is over break for loop
			if(this.findIsGameOver()) {
				break;
				
			} // end of if
			
			// next player is "left"
			playerIndex = (playerIndex + 1) % this.numPlayers;
			
		} // end of for loop
		
		// if game is over return to calling method
		if(this.findIsGameOver()) {
			return;
			
		} // end of if
		
		// dealer score crib
		playerIndex = this.dealerIndex;
		currentHand = Arrays.copyOf(this.getCrib(), this.getCrib().length + 1);
		currentHand[currentHand.length - 1] = this.deckStack.peekCard();
		
		System.out.println("It now time to score the crib.");
		printPlayerTurn(playerIndex);
		pressEnterToContinue();
		System.out.println("This is your crib:");
		System.out.println(Arrays.toString(currentHand));
		System.out.println();
		scoreShowHand(currentHand, playerIndex, true);
		
		printPlayersScore("show");
		
	} // end of doShow
	
	public void scoreShowHand(Card[] currentHand, int playerIndex, boolean isCrib) {
		int pointsScored;
		
		// pair points
		pointsScored = getPairPointsShow(currentHand);
		if (pointsScored > 0) {
			System.out.printf("Player %d got pairs for %d points!%n", playerIndex + 1, pointsScored);
			setPlayerScore(playerIndex, pointsScored);
		} // end of if
		
		// flush points
		pointsScored = getFlushPointsShow(currentHand, isCrib);
		if (pointsScored > 0) {
			System.out.printf("Player %d got flush for %d points!%n", playerIndex + 1, pointsScored);
			setPlayerScore(playerIndex, pointsScored);
		} // end of if
		
		// nob point
		pointsScored = getNobPointsShow(currentHand);
		if (pointsScored > 0) {
			System.out.printf("Player %d has a \"nob\" for %d point!%n", playerIndex + 1, pointsScored);
			setPlayerScore(playerIndex, pointsScored);
		} // end of if
		
		// run points
		pointsScored = getRunPointsShow(currentHand);
		if (pointsScored > 0) {
			System.out.printf("Player %d has got a run for %d points!%n", playerIndex + 1, pointsScored);
			setPlayerScore(playerIndex, pointsScored);
		} // end of if
		
		// fifteen point
		pointsScored = get15PointsShow(currentHand); 
		if (pointsScored > 0) {
			System.out.printf("Player %d has fifteen for %d points!%n", playerIndex + 1, pointsScored);
			setPlayerScore(playerIndex, pointsScored);
		} // end of if
		
		// end of scoring
		System.out.println();
		
	} // end of scoreShowHand
	
	public void dealMessage() {
		System.out.printf("%nRound %d of play has begun.%n", this.gameRound);
		
		String cribDealtString = "";
		if (this.cardsDealtToCrib > 0) {
			cribDealtString = String.format(" and %d card to the crib", this.cardsDealtToCrib);
			
		} // end of if
		
		System.out.printf("%d cards have been dealt to each player%s.%n", 
				this.cardsPerPlayer, cribDealtString);
		
	} // end of dealMessage
	
	public void dealToPlayers() {
		int playerIndex;
		
		for (int dealingStep = 0; dealingStep < this.cardsPerPlayer; dealingStep++) {
			
			// set first dealt player as next/"left" of dealer
			playerIndex = (this.dealerIndex + 1) % this.numPlayers;
			
			for (int playersDealt = 0; playersDealt < this.numPlayers; playersDealt++) {
				this.currentCardHands[playerIndex][dealingStep] = this.deckStack.dealCard();
				
				// next dealt player is "left"
				playerIndex = (playerIndex + 1) % this.numPlayers;
				
			} // end of inner for loop
			
		} // end of outer for loop
		
	} // end of dealToPlayers
	
	public void dealToCrib() {
		for (int cardsDealt = 0; cardsDealt < this.cardsDealtToCrib; cardsDealt++) {
			this.currentCrib[cardsDealt] = this.deckStack.dealCard();
			this.cardsInCrib++;
			
		} // end of for loop
		
	}  // end of dealToCrib
	
	public void discardToCribPlayer(int playerIndex, int cardIndex) {
		Card cardTemp = discardPlayer(playerIndex, cardIndex);
		
		this.currentCrib[this.cardsInCrib++] = cardTemp;
		
	} // end of discardToCribPlayer
	
	public Card playCard(int playerIndex, int cardIndex) {
		Card[] playerHand = this.currentCardHands[playerIndex];
		Card cardTemp = playerHand[cardIndex];
		
		// update player's hand
		playerHand[cardIndex] = null;

		this.currentCardHands[playerIndex] = removeNullFromCardArray(playerHand);
		
		return cardTemp;
		
	} // end of playCard
	
	public Card discardPlayer(int playerIndex, int cardIndex) {
		Card[] playerHand = this.currentCardHands[playerIndex];
		Card cardTemp = playerHand[cardIndex];
		
		// update player's hand
		playerHand[cardIndex] = null;
		this.currentCardHands[playerIndex] = removeNullFromCardArray(playerHand);
		
		return cardTemp;
		
	} // end of discardPlayer
	
	public void discardToCrib() {
		Scanner inputUser = new Scanner(System.in);
		
		System.out.println();
		
		int cardIndex;
		int currentHandSize;
		
		for (int playerIndex = 0; playerIndex < this.numPlayers; playerIndex ++) {
			printPlayerTurn(playerIndex);
			
			// remind player if it is their crib
			if (playerIndex == this.dealerIndex) {
				System.out.println("The crib is yours this round.");
				
			} // end of if
			
			pressEnterToContinue();

			System.out.println(Arrays.toString(this.getPlayerHand(playerIndex)));
			
			System.out.printf("You must discard %d card(s) to the crib.%n", this.cardsDiscardToCribPerPlayer);
			
			for (int discardCount = 0; discardCount < this.cardsDiscardToCribPerPlayer; discardCount++) {
				currentHandSize = this.getPlayerHand(playerIndex).length;
				
				System.out.printf("Which card would you like to discard to the crib? (1-%d): ", currentHandSize);
				
				cardIndex = getIntFromUser(inputUser, 1, currentHandSize) - 1; // we want internal index
				
				discardToCribPlayer(playerIndex, cardIndex);
				System.out.println("\nThis is your new hand:");
				System.out.println(Arrays.toString(this.getPlayerHand(playerIndex)));
				
			} // end of inner for
			System.out.println();
			
		} // end of outer for
		
	} // end of discardToCrib
	
	public Card[] getPlayerHand(int playerIndex) {
		return this.currentCardHands[playerIndex];
		
	} // end of getPlayerHand
	
	public Card[] getCrib() {
		return this.currentCrib;
		
	} // end of getCrib
	
	public void getStarter() {
		Scanner inputUser = new Scanner(System.in);
		
		printPlayerTurn((this.dealerIndex + 1) % this.numPlayers);
		
		int deckMaxIndex = this.deckStack.getCardDeckSize() - 1;
		System.out.printf("How many cards would you like to cut before selecting a card? (0-%d): ", deckMaxIndex);
		int cardIndex = getIntFromUser(inputUser, 0, deckMaxIndex);
		this.deckStack.cutCardToTop(cardIndex);
		
		Card starterCard = this.deckStack.peekCard();
		System.out.printf("%nThe starter is %s.%n", starterCard.toString());
		
		if (starterCard.getRank() == 11) {
			int pointsScored = 2;
			System.out.printf("Player %d got \"heels\"; %d points!%n", this.dealerIndex + 1, pointsScored);
			setPlayerScore(this.dealerIndex, pointsScored);
			
		} // end of if
		
		System.out.println();
		
	} // end of getStarter
	
	public void chooseInitialDealer() {
		Scanner inputUser = new Scanner(System.in);
		int[] playerCutRankArray = new int[this.numPlayers];
		int deckMaxIndex;
		Card cutCard;
		int cutIndex;
		
		System.out.println("We will now determine who will deal first.");
		System.out.println("Each player will cut the shuffled deck to a card.");
		System.out.println("The player who has the lowest rank card deals first.");
		System.out.println("Tie goes to whichever player cut first.");
		System.out.println();
		
		// new shuffled deck to cut
		setShuffledDeckStack();
		
		for (int playerIndex = 0; playerIndex < playerCutRankArray.length; playerIndex++) {
			printPlayerTurn(playerIndex); 
			
			// the player can not cut all the cards remaining, and there must be enough enough
			// cards remaining for each remaining player to get a card
			deckMaxIndex = this.deckStack.getCardDeckSize() - (this.numPlayers - 1 - playerIndex) - 1;
			
			// we only ask user input if they can cut cards
			if (deckMaxIndex > 0) {
				System.out.printf("How many cards would you like to cut before selecting a card? (0-%d): ", 
						deckMaxIndex);
				
				do {
					try {
						cutIndex = Integer.parseInt(inputUser.next());
						
					} catch(NumberFormatException e) {
						cutIndex = -1;
						
					} // end of try-catch
					
					if (cutIndex < 0 || cutIndex > deckMaxIndex) {
						System.out.printf("Please pick a number between 0 and %d : ", deckMaxIndex);
						cutCard = null;
						
					} else {
						cutCard = this.deckStack.cutCardDiscard(cutIndex);
						
					} // end of if/else
					
				} while(cutCard == null); // end of do-while
				
			} else {
				System.out.println("You can not cut any cards.");
				cutCard = this.deckStack.dealCard();
				
			} // end of if/else
			
			System.out.printf("You picked the %s!%n%n", cutCard);
			playerCutRankArray[playerIndex] = cutCard.getRank();

		} // end of for loop
		
		// declare and populate Queue of playerCutRank
		Queue<Integer> playerCutRankQueue = new LinkedList<Integer>();
		
		for (int rank : playerCutRankArray) {
			playerCutRankQueue.add(rank);
			
		} // end of for loop
		
		int currentRank;
		int minRank = 14; // start loop with too high of rank
		
		// cycle through all cut cards' rank twice, except for the last in queue, to find all cards of
		// minimum rank
		for (int ranksParsed = 1; ranksParsed <= playerCutRankArray.length*2-1; ranksParsed++) {
			currentRank = playerCutRankQueue.remove();
			
			// queue rank if <= minRank
			if (currentRank <= minRank) {
				playerCutRankQueue.add(currentRank);
				
			} // end of if
			
			// update minRank when needed
			if (currentRank < minRank) {
				minRank = currentRank;
				
			} // end of if
			
		} // end of for loop
		
		// TODO: to properly simulate cribbage, on rank tie, card cutting should be done
		// again by the tied players
		
		// tie goes to first cutter
		int firstDealerIndex = findFirstInstanceOf(playerCutRankArray, minRank);
		
		System.out.printf("Player %d will deal first.%n", firstDealerIndex + 1);
		
		this.dealerIndex = firstDealerIndex;
		
	} // end of chooseInitialDealer
	
	public void setNumPlayers() {
		int maxPlayers = 4;
		int minPlayers = 2;
		
		Scanner inputUser = new Scanner(System.in);
		
		System.out.printf("How many players are playing cribbage? (%d-%d): ", minPlayers, maxPlayers);
		this.numPlayers = getIntFromUser(inputUser, minPlayers, maxPlayers);
		System.out.println();
		
	} // end of setNumPlayers
	
	public int getIntFromUser(Scanner inputUser, int minInt, int maxInt) {
		int outputInt;
		
		do {
			try {
				outputInt = Integer.parseInt(inputUser.next());
				
			} catch(NumberFormatException e) {
				outputInt = -1;
				
			} // end of try-catch
			
			if (outputInt < minInt || outputInt > maxInt) {
				System.out.printf("Please pick a number between %d and %d : ", minInt, maxInt);
				outputInt = -1;
				
			} //end of if
			
		} while (outputInt == -1); // end of do-while
		
		return outputInt;
		
	} // end of getIntFromUser
	
	public int findFirstInstanceOf(int[] intArray, int intValue) {
		// unless value found, -1 will be returned
		int arrayIndex = -1;
		
		for (int index = 0; index < intArray.length; index++) {
			if (intArray[index] == intValue) {
				arrayIndex = index;
				break;
				
			} // end of if
			
		} // end of for loop
		
		return arrayIndex;
		
	} // end of findFirstInstanceOf
	
	public boolean playerCanPlayCard(int playerIndex, int playPointSum) {
		Card[] playerHand = this.currentCardHands[playerIndex];
		
		// declare and populate card points value array
		int[] playerPoints = new int[playerHand.length];
		
		for (int cardIndex = 0; cardIndex < playerHand.length; cardIndex++) {
			playerPoints[cardIndex] = playerHand[cardIndex].getRankPoints();
			
		} // end of for loop
		
		int maxPoints = this.maxPlayPoints - playPointSum;
		
		boolean canPlay = false;
		int instanceIndex;
		
		for (int cardPoints = maxPoints; cardPoints > 0; cardPoints--) {
			instanceIndex = findFirstInstanceOf(playerPoints, cardPoints);
			
			if (instanceIndex != -1) {
				canPlay = true;
				break;
				
			} // end of if
			
		} // end of for loop
		
		return canPlay;
		
	} // end of playerCanPlayCard
	
	public int getRunPointsPlay(Card[] cardArray) {
		int maxIndex = cardArray.length;
		Card[] arrayTemp;
		
		// System.out.println(maxIndex);
		
		boolean isListARun;
		int runLength = 0;
		
		// start with the three most recently played cards and see if it is a run;
		// check for 1 card longer run if current array contains a run
		for (int arrayIndex = maxIndex - 3; arrayIndex >= 0; arrayIndex --) {
			arrayTemp = Arrays.copyOfRange(cardArray, arrayIndex, maxIndex);
			// System.out.println(Arrays.toString(arrayTemp));
			isListARun = getIsListARun(arrayTemp);
			
			if (isListARun) {
				runLength = arrayTemp.length;
				
			} else {
				break;
				
			} // end of if/else
			
		} // end of for loop
		
		return runLength;
		
	} // end of getRunPointsPlay
	
	public int getRunPointsShow(Card[] cardArray) {
		int[] rankArray = getRankArray(cardArray);
		
		int runMultiplier = 1;
		int previousRank = rankArray[0];
		int currentRank, nextIndex, pairCount;
		int runLength = 1;
		int runPoints = 0;
		for(int rankIndex = 0; rankIndex < rankArray.length; rankIndex++) {
			currentRank = rankArray[rankIndex];
			
			if (currentRank == (previousRank + 1)) {
				runLength++;
				
			} else if (currentRank == previousRank) {
				nextIndex = rankIndex + 1;
				pairCount = 1;
				
				// we look to see how many of the same rank
				// are in cardArray
				while(nextIndex < rankArray.length) {
					// if we have another pair, we go to the next element in array
					if (rankArray[nextIndex] == currentRank) {
						pairCount++;
						nextIndex++;
					
					// if these pairs are part of a run, they act as multiplier
					// to the run
					} else if (rankArray[nextIndex] == (currentRank + 1)) {
						runMultiplier *= pairCount;
						break;
						
					// the next element is neither part of a pair or a run
					} else {
						break;
						
					} // end of inner if/else
					
				} // end of while loop
				
			} else {
				// score any points from a run
				if (runLength >=3) {
					runPoints += runLength * runMultiplier;
					
				} // end of if
				
				runLength = 1;
				runMultiplier = 1;
				
			} // end of outer if/else
			
			previousRank = currentRank;
		} // end of for
		
		// add final runPoints
		if (runLength >=3) {
			runPoints += runLength * runMultiplier;
			
		} // end of if
		
		return runPoints;
		
	} // end of getRunPointsShow
	
	public boolean getIsListARun(Card[] cardArray) {
		int[] rankArray = getRankArray(cardArray);
		
		// initialize entering value
		int lastValue = rankArray[0];
		int currentValue;
		
		boolean isListARun = true;
		for (int arrayIndex = 1; arrayIndex < cardArray.length; arrayIndex++) {
			currentValue = rankArray[arrayIndex];
			
			if (currentValue != lastValue + 1) {
				isListARun = false;
				break;
				
			} else {
				lastValue = currentValue;
				
			} // end of if/else
			
		} // end of for
		
		return isListARun;
	} // end of getIsListARun
	
	public int[] getRankArray(Card[] cardArray) {
		int[] rankArray = new int[cardArray.length];
		
		for (int arrayIndex = 0; arrayIndex < cardArray.length; arrayIndex++) {
			rankArray[arrayIndex] = cardArray[arrayIndex].getRank();
	
		} // end of for
		
		Arrays.sort(rankArray);
		
		return rankArray;
		
	} // end of getRankArray
	
	public int[] getSuitArray(Card[] cardArray) {
		int[] suitArray = new int[cardArray.length];
		
		for (int arrayIndex = 0; arrayIndex < cardArray.length; arrayIndex++) {
			suitArray[arrayIndex] = cardArray[arrayIndex].getSuit();
	
		} // end of for
		
		Arrays.sort(suitArray);
		
		return suitArray;
		
	} // end of getSuitArray
	
	public int[] getPointArray(Card[] cardArray) {
		int[] pointArray = new int[cardArray.length];
		
		for (int arrayIndex = 0; arrayIndex < cardArray.length; arrayIndex++) {
			pointArray[arrayIndex] = cardArray[arrayIndex].getRankPoints();
	
		} // end of for
		
		Arrays.sort(pointArray);
		
		return pointArray;
		
	} // end of getPointsArray
	
	public Queue<Integer> getRankQueue(Card[] cardArray) {
		int[] rankArray = getRankArray(cardArray);
		
		// populate queue
		Queue<Integer> rankQueue = new LinkedList<Integer>();
		for (int cardIndex = 0; cardIndex < rankArray.length; cardIndex++) {
			rankQueue.add(rankArray[cardIndex]);
			
		} // end of for
		
		return rankQueue;
	} // end of getRankQueue
	
	public int getPairPointsPlay(Card[] cardArray) {
		int cardArrayLength = cardArray.length;
		int recentCardRank = cardArray[cardArrayLength - 1].getRank();
		
		// go through all cards except for the most recent, backwards, to see if
		// pairs are present
		int pairSize = 1;
		for (int arrayIndex = cardArrayLength - 2; arrayIndex >= 0; arrayIndex--) {
			if (cardArray[arrayIndex].getRank() == recentCardRank) {
				pairSize++;
				
			} else {
				break;
				
			} // end of if/else
			
		} // end of for
		
		return getPairPointsFromPairSize(pairSize); 

	} // end of getPairPointsPlay
	
	public int getPairPointsShow(Card[] cardArray) {
		Queue<Integer> rankQueue = getRankQueue(cardArray);
		
		int pairPoints = 0;
		
		int currentRank;
		
		// we need to initialize these variables for
		// first element in queue
		int pairSize = 0;
		int previousRank = rankQueue.peek();
		
		for (int rankCount = 0; rankCount < cardArray.length; rankCount++) {
			currentRank = rankQueue.remove();
			
			if (currentRank == previousRank) {
				pairSize++;
				
			} else {
				// before we reset pairSize, we find the contributed points value
				// for this pairSize
				pairPoints += getPairPointsFromPairSize(pairSize);
				
				pairSize = 1;
				
			} // end of if/else
			// update previousRank for next iteration
			previousRank = currentRank;
			
		} // end of for
		// add final pairPoints
		pairPoints += getPairPointsFromPairSize(pairSize);
		
		return pairPoints;

	} // end of getPairPointsShow
	
	public int getFlushPointsShow(Card[] cardArray, boolean isCrib) {
		// for initial flush check, we don't want to use the starter card, unless
		// it is the crib
		int[] suitArray;
		
		if (isCrib) {
			suitArray = getSuitArray(cardArray);
			
		} else {
			suitArray = getSuitArray(Arrays.copyOfRange(cardArray, 0, cardArray.length - 1));
			
		} // end of if/else
		
		int currentSuit;
		int suitMatch = 0;
		
		// we need to initialize this variable for
		// first element in array
		int previousSuit = suitArray[0];
		
		for (int suitIndex = 0; suitIndex < suitArray.length; suitIndex++) {
			currentSuit = suitArray[suitIndex];
			
			if (currentSuit == previousSuit) {
				suitMatch++;
				
			} // end of if
			// update previousSuit for next iteration
			previousSuit = currentSuit;
			
		} // end of for loop

		if (suitMatch == suitArray.length) {
			// if the starter suit matches the original 4 card suits
			// score additional point
			if (cardArray[cardArray.length - 1].getSuit() == previousSuit) {
				return 5;
				
			} else {
				return 4;
				
			} // end of inner if/else
			
		} else {
			return 0;
			
		} // end of outer if/else

	} // end of getFlushPointsShow
	
	public int getNobPointsShow(Card[] cardArray) {
		// we are looking to see if players hand minus starter
		// has a jack of the same suit as starter
		Card[] playerHand = Arrays.copyOfRange(cardArray, 0, cardArray.length - 1);
		int starterCardSuit = cardArray[cardArray.length - 1].getSuit();
		
		// get any jacks from playerHand
		Stack<Card> jackStack = new Stack<Card>();
		
		for (Card card: playerHand) {
			if (card.getRank() == 11) {
				jackStack.push(card);
				
			} // end of if
			
		} // end of playerHand for
		
		// see if any of the jacks have starter suit
		// and if so, return 1
		Card currentCard;
		while(!jackStack.isEmpty()) {
			currentCard = jackStack.pop();
			
			if (currentCard.getSuit() == starterCardSuit) {
				return 1;
				
			} // end of if
			
		} // end of while
		
		// if no "nob" is found, return 0
		return 0;

	} // end of getNobPointsShow
	
	public int get15PointsShow(Card[] cardArray) {
		int[] rankArray = getPointArray(cardArray);
		
		int points15 = 0;
		
		// brute-force finding of 15s, by creating arrays of all card combinations
		// TODO: more elegant 15s finding, probably using recursion
		int[] oneArray = {rankArray[0], 0};
		
		for (int onePoint : oneArray) {
			int[] twoArray = {rankArray[1], 0};
			
			for (int twoPoint : twoArray) {
				int[] threeArray = {rankArray[2], 0};
				
				for (int threePoint : threeArray) {
					int[] fourArray = {rankArray[3], 0};
					
					for (int fourPoint : fourArray) {
						int[] fiveArray = {rankArray[4], 0};
						
						for (int fivePoint : fiveArray) {
							int[] currentArray = {onePoint, twoPoint, threePoint, fourPoint, fivePoint};
							
							if (getSumOfArray(currentArray) == 15) {
								points15 += 2;
								
							} // end of if
							
						} // end of fiveArray for
						
					} // end of fourArray for
					
				} // end of threeArray for
				
			} // end of twoArray for
			
		} // end of oneArray for
		
		return points15;
		
	} // end of get15PointsShow
	
	public int getSumOfArray(int[] pointArray) {
		int sum = 0;
		
		for (int points : pointArray) {
			sum += points;
			
		} // end of for
		
		return sum;
	} // end of getSumOfArray
	
	
	public int getPairPointsFromPairSize(int pairSize) {
		switch(pairSize) {
			case 2:
				return 2;
				
			case 3:
				return 6;
				
			case 4:
				return 12;
				
			default:
				return 0;
		
		} // end of switch-case
		
	} // end of getPairPointsFromPairSize
	
	public void printPlayerTurn(int playerIndex) {
		System.out.printf("It is now Player %d's turn.%n", playerIndex + 1);
		
	} // end of printPlayerTurn
	
	public void pressEnterToContinue() {
		System.out.println("Press ENTER key to view your hand.");
		
		Scanner inputScanner = new Scanner(System.in);
		inputScanner.nextLine();
		
	} // end of pressEnterToContinue
	
	public Card[] removeNullFromCardArray(Card[] cardArray) {
		Queue<Card> queueTemp = new LinkedList<Card>();
		
		// add all non-null cards to queue
		for (Card card : cardArray) {
			if (card != null) {
				queueTemp.add(card);
				
			} // end of if
			
		} // end of for loop
		
		// create new array from queue
		Card[] newCardArray = new Card[queueTemp.size()];
		
		for (int arrayIndex = 0; arrayIndex < newCardArray.length; arrayIndex++) {
			newCardArray[arrayIndex] = queueTemp.remove();
			
		} // end of for loop
		
		return newCardArray;
		
	} // end of removeNullFromCardArray
	
	public boolean findIsGameOver() {
		for (int score : playersScoreArray) {
			if (score >= this.winPoints) {
				return true;
				
			} // end of if
			
		} // end of for loop
		
		return false;
	} // end of findIsGameOver

} // end of Cribbage class
