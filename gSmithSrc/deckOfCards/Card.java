// Class: CS 145
// Date: 04/26/2022
// Assignment: Lab 4 - Deck of Cards
// Reference: 
// https://en.wikipedia.org/wiki/Standard_52-card_deck
//
// Purpose: Model a standard 52-card deck playing card,
// including rank points needed for cribbage play.
//
// I used Eclipse for the creation of this class

package deckOfCards;

public class Card {
	private final int rank;
	private final int suit;
	
	public Card(int rank, int suit) {
		// rank initialization attempt
		if (rank < 1 || rank > 13) {
			throw new IllegalArgumentException("Rank must be between 1 and 13");
			
		} else {
			this.rank = rank;
			
		} // end of if/else
		
		// suit initialization attempt
		if (suit < 1 || suit > 4) {
			throw new IllegalArgumentException("Suit must be between 1 and 4");
			
		} else {
			this.suit = suit;
			
		} // end of if/else
		
	} // end of constructor
	
	public String toString() {
		return getRankString() + " of " + getSuitString();
		
	} // end of toString accessor
	
	public String getRankString() {
		switch(this.rank) {
			case 1:
				return "Ace";
				
			case 11:
				return "Jack";
				
			case 12:
				return "Queen";
				
			case 13:
				return "King";
				
			default:
				// all other cards are known by their number
				return Integer.toString(this.rank);
				
		} // end of switch-case
		
	} // end of getRankString accessor
	
	public String getSuitString() {
		switch(this.suit) {
			case 1:
				return "Clubs";
				
			case 2:
				return "Diamonds";
				
			case 3:
				return "Hearts";
				
			case 4:
				return "Spades";
				
			default:
				return null;
				
		} // of switch-case
		
	} // end of getSuitString accessor
	
	public int getRank() {
		return this.rank;
		
	} // end of getRank
	
	public int getSuit() {
		return this.suit;
		
	} // end of getSuit
	
	public int getRankPoints() {
		switch(this.rank) {
			case 11:
			case 12:
			case 13:
				return 10;
				
			default:
				return this.rank;
		
		} // end of switch-case
		
	} // end of getRankPoints
	
} // end of Card class
