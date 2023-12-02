package csc223mod11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AirportsGraph {

	public static void main(String[] args) {

		Graph graph = loadDataFromFile("src/Airport_AdjacencyList.txt");
		Scanner scan = new Scanner(System.in);

		while (true) {
			System.out.println("\nAirport Graph Application");
			System.out.println("1. Summarize Airport Dataset");
			System.out.println("2. Calculate Flight Path");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			int choice = scan.nextInt();
			switch (choice) {
			case 1:
				summarizeDataset(graph);
				break;
			case 2:
				calculateFlightPath(graph);
				break;
			case 3:
				System.out.println("Exiting application.");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void summarizeDataset(Graph graph) {

		int numberOfAirports = graph.getAirports().size();
		int numberOfRoutes = graph.getAllRoutes().size();
		System.out.println("Number of airports: " + numberOfAirports);
		System.out.println("Number of direct flights: " + numberOfRoutes);
	}

	private static void calculateFlightPath(Graph graph) {

		Scanner scan = new Scanner(System.in);
		System.out.print("Enter First Airport code: ");
		String start = scan.next();
		System.out.print("Enter Second Airport code: ");
		String end = scan.next();

		List<Route> path = graph.findShortestPath(start, end);
		if (path.isEmpty()) {
			System.out.println("No path available between " + start + " and " + end);
			return;
		}

		System.out.print("The flight path is: ");
		int totalDistance = 0;
		for (Route route : path) {
			System.out.print(route.getSource() + " - " + route.getDestination() + " ");
			totalDistance += route.getDistance();
		}
		System.out.println("\nThe total flight distance is " + totalDistance + " miles.");
	}

	public static Graph loadDataFromFile(String filename) {

		Graph graph = new Graph();

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(": ");
				String sourceAirport = parts[0].trim();
				String[] destinations = parts[1].split(", ");

				for (String destination : destinations) {
					String[] connectionParts = destination.split("-");
					String targetAirport = connectionParts[0];
					int distance = Integer.parseInt(connectionParts[1]);

					graph.addRoute(sourceAirport, targetAirport, distance);
				}
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		return graph;
	}
}
