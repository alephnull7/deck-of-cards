// Class: CS 145
// Date: 04/26/2022
// Assignment: Lab 4 - Deck of Cards
//
// Purpose: The client program, with the use of the user defined Cribbage class,
// will simulate playing of cribbage games as indicated by user. 
//
// I used Eclipse for the creation of this class

package deckOfCards;

// System.in use
import java.util.Scanner;

public class PlayCribbage {

	public static void main(String[] args) {
		Scanner inputUser = new Scanner(System.in);
		System.out.print("Would you like to play a game of cribbage? (\"yes\" for yes): ");
		String inputString = inputUser.nextLine();
		
		System.out.println();
		while (inputString.trim().equalsIgnoreCase("yes")) {
			Cribbage cribbageGame = new Cribbage();
			
			while(!cribbageGame.findIsGameOver()) {
				cribbageGame.playRound();
				
			} // end of inner while loop
			
			System.out.println();
			
			System.out.print("Would you like to play another game of cribbage? (\"yes\" for yes): ");
			inputString = inputUser.nextLine();
		
		} // end of outer while loop
		
	} // end of main
	
} // end of PlayCribbage client class
