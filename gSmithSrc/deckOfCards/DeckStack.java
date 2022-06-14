// Programmer: Gregory Smith
// Date: 04/26/2022
// Program: Deck of Cards
// Reference: https://docs.oracle.com/javase/8/docs/api/java/util/Stack.html
// Purpose: Use the user defined DeckArray class to create
// a stack implementation of a standard 52-card deck
//
// IDE: Eclipse

package deckOfCards;

import java.util.Stack;

public class DeckStack {
	private Stack<Card> cardDeck;

	public DeckStack() {
		// get a shuffled DeckArray
		DeckArray deckArray = new DeckArray();
		deckArray.shuffle();
		
		// since no additional manipulation of elements done,
		// order of elements in Stack is reversed
		this.cardDeck = dealDeckArrayIntoStack(deckArray);
		
	} // end of constructor
	
	public Stack<Card> dealDeckArrayIntoStack(DeckArray deckArray) {
		Stack<Card> cardDeckStack = new Stack<>();
		
		Card currentCard;
		
		for (int dealCount = 0; dealCount < deckArray.getCardDeckLength(); dealCount++) {
			currentCard = deckArray.dealCard();
			cardDeckStack.push(currentCard);
			
		} // end of for
		
		return cardDeckStack;
		
	} // end of dealDeckArrayIntoStack
	
	public Card dealCard() {
		if (!this.cardDeck.isEmpty()) {
			return this.cardDeck.pop();
			
		} else {
			return null;
			
		} // end of if/else
		
	} // end of dealCard mutator
	
	public Card peekCard() {
		if (!this.cardDeck.isEmpty()) {
			return this.cardDeck.peek();
			
		} else {
			return null;
			
		} // end of if/else
		
	} // end of peekCard mutator
	
	public void cutCardDeck(int cutIndex) {		
		Stack<Card> cutToBottom = new Stack<>();
		Stack<Card> cutToTop = new Stack<>();
		
		// cards moved to bottom of deck
		// order will be correct when pushing back into cardDeck
		for (int popCount = 0; popCount < cutIndex; popCount++) {
			cutToBottom.push(this.cardDeck.pop());
			
		} // end of for
		
		// rest of cards go to the top
		// order will be correct when pushing back into cardDeck
		while (!this.cardDeck.isEmpty()) {
			cutToBottom.push(this.cardDeck.pop());
			
		} // end of while
		
		// push bottom stack onto cardDeck
		while (!cutToBottom.isEmpty()) {
			this.cardDeck.push(cutToBottom.pop());
			
		} // end of while
		
		// push top stack onto cardDeck
		while (!cutToTop.isEmpty()) {
			this.cardDeck.push(cutToTop.pop());
			
		} // end of while
		
	} // end of cutCardDeck mutator
	
	public void cutCardToTop(int cardIndex) {
		Stack<Card> stackTemp = new Stack<Card>();
		
		// cards put below cut card
		// order will be correct when pushing back into cardDeck
		for (int popCount = 0; popCount < cardIndex; popCount++) {
			stackTemp.push(this.cardDeck.pop());
			
		} // end of for
		
		// save card to put on top
		Card topCard = this.cardDeck.pop();
		
		// put rest of cards back in place
		while (!stackTemp.isEmpty()) {
			this.cardDeck.push(stackTemp.pop());
			
		} // end of while
		
		// card goes on top
		this.cardDeck.push(topCard);
		
	} // end of cutCardToTop
	
	public Card cutCardDiscard(int cardIndex) {
		// remove cards above chosen card
		for (int popCount = 0; popCount < cardIndex; popCount++) {
			this.cardDeck.pop();
			
		} // end of for
		
		// return chosen card
		return this.cardDeck.pop();
		
	} // end of cutCardDiscard
	
	public int getCardDeckSize() {
		return this.cardDeck.size();
		
	} // end of getCardDeckSize
	
} // end of DeckStack class
