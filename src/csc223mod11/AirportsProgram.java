package csc223mod11;

import java.util.Scanner;

public class AirportsProgram {

	public static void main(String[] args) {

		Graph graph = Graph.loadDataFromFile("src/Airport_AdjacencyList.txt");
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			Menu.displayMenu();
			int choice = scan.nextInt();
			switch (choice) {
			case 1:
				Graph.summarizeAirportDataset(graph);
				break;
			case 2:
				Graph.calculateFlightPath(graph);
				break;
			case 3:
				System.out.println("Exiting application.");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}
}