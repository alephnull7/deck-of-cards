// Programmer: Gregory Smith
// Date: 04/26/2022
// Program: Deck of Cards
// Reference: https://en.wikipedia.org/wiki/Standard_52-card_deck
// Purpose: Use the user defined Card class to create
// an array implementation of a standard 52-card deck
//
// IDE: Eclipse

package deckOfCards;

import java.security.SecureRandom;

public class DeckArray {
	// class constants
	private static final int DECK_SIZE;
	private static final int SUIT_NUMBER;
	private static final int RANK_NUMBER;
	private static final SecureRandom randomObj;
	
	/// instance fields
	private Card[] cardDeck;
	private int dealCount;
	
	static {
		DECK_SIZE = 52;
		SUIT_NUMBER = 4;
		RANK_NUMBER = DECK_SIZE / SUIT_NUMBER;
		randomObj = new SecureRandom();
		
	}
	
	public DeckArray() {
		// initialize null cardDeck
		this.cardDeck = new Card[DeckArray.DECK_SIZE];
		
		// add expected 52 Card objects to cardDeck
		populateCardDeck();
		
		// fresh deck, no cards dealt
		this.dealCount = 0;
		
	} // end of constructor
	
	public void populateCardDeck() {
		int rankTemp;
		int suitTemp = 0; // must be initialized to be enumerated
		
		for (int cardCount = 0; cardCount < this.cardDeck.length; cardCount++) {
			rankTemp = (cardCount % DeckArray.RANK_NUMBER) + 1;
			
			// if starting new suit, enumerate suit
			if (rankTemp == 1) {
				suitTemp += 1;
				
			} // end of if
			
			this.cardDeck[cardCount] = new Card(rankTemp, suitTemp);
			
		} // end of for loop
		
	} // end of populateCardDeck mutator
	
	public void shuffle() {
		int otherIndex;
		Card cardTemp;
		
		for (int currentIndex = 0; currentIndex < cardDeck.length; currentIndex++) {
			otherIndex = DeckArray.randomObj.nextInt(DeckArray.DECK_SIZE);
			
			// we only want to swap cards if the cards are different
			if (currentIndex != otherIndex) {
				cardTemp = cardDeck[currentIndex];
				
				cardDeck[currentIndex] = cardDeck[otherIndex];
				cardDeck[otherIndex] = cardTemp;
			} // end of if
			
		} // end of for loop
		
	} // end of shuffle mutator
	
	public Card dealCard() {
		if (dealCount < cardDeck.length) {
			return cardDeck[this.dealCount++];
			
		} else {
			return null; // no more cards
			
		} // end of if/else
		
	} // end of dealCard accessor
	
	public int getCardDeckLength() {
		return this.cardDeck.length;
		
	} // end of getCardDeckLength accessor
	
} // end of DeckArray class
