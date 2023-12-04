package csc223mod11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

class Graph {

	private List<String> airports;
	private Map<String, List<Route>> routesBySourceAirportCode;

	public Graph() {
		airports = new ArrayList<>();
		routesBySourceAirportCode = new HashMap<>();
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

	public static void summarizeAirportDataset(Graph graph) {

		int numberOfAirports = graph.getAirports().size();
		int numberOfRoutes = graph.getAllRoutes().size();
		System.out.println("Number of airports: " + numberOfAirports);
		System.out.println("Number of direct flights: " + numberOfRoutes);
	}

	public void addAirport(String airportCode) {
		airports.add(airportCode);
		routesBySourceAirportCode.putIfAbsent(airportCode, new ArrayList<>());
	}

	public void addRoute(String sourceAirportCode, String destinationAirportCode, int distance) {
		Route route = new Route(sourceAirportCode, destinationAirportCode, distance);

		// Add source and destination airports if not already present
		if (!airports.contains(sourceAirportCode)) {
			airports.add(sourceAirportCode);
			routesBySourceAirportCode.putIfAbsent(sourceAirportCode, new ArrayList<>());
		}

		if (!airports.contains(destinationAirportCode)) {
			airports.add(destinationAirportCode);
			routesBySourceAirportCode.putIfAbsent(destinationAirportCode, new ArrayList<>());
		}

		routesBySourceAirportCode.get(sourceAirportCode).add(route);
		// For undirected graph, add reverse route as well
		routesBySourceAirportCode.get(destinationAirportCode)
				.add(new Route(destinationAirportCode, sourceAirportCode, distance));
	}

	public static void calculateFlightPath(Graph graph) {

		Scanner scan = new Scanner(System.in);
		System.out.print("Enter First Airport code: ");
		String start = scan.next();
		start = start.toUpperCase();
		System.out.print("Enter Second Airport code: ");
		String end = scan.next();
		end = end.toUpperCase();

		List<Route> path = graph.findShortestPath(start, end);
		if (path.isEmpty()) {
			System.out.println("No path available between " + start + " and " + end);
			return;
		}

		System.out.print("The flight path is: ");
		int totalDistance = 0;
		boolean firstRoute = true;
		
		for (Route route : path) {			
			if (firstRoute) {
				System.out.print(route.getSource());
				firstRoute = false;
			}
			System.out.print(" -> " + route.getDestination());
			totalDistance += route.getDistance();
		}
		System.out.println("\nThe total flight distance is " + totalDistance + " miles.");
	}

	public List<Route> findShortestPath(String startCode, String endCode) {

		Map<String, Integer> distances = new HashMap<>();
		Map<String, Route> previous = new HashMap<>();
		PriorityQueue<Route> queue = new PriorityQueue<>(Comparator.comparing(Route::getDistance));

		for (String airport : airports) {
			distances.put(airport, Integer.MAX_VALUE);	// infinite distance between airport and end airport
		}
		distances.put(startCode, 0);
		queue.add(new Route(startCode, startCode, 0));

		while (!queue.isEmpty()) {
			Route current = queue.poll();
			String currentAirport = current.getDestination();

			if (currentAirport.equals(endCode)) {
				break;
			}

			for (Route neighbor : routesBySourceAirportCode.getOrDefault(currentAirport, new ArrayList<>())) {
				int altDistance = distances.get(currentAirport) + neighbor.getDistance();
				if (altDistance < distances.get(neighbor.getDestination())) {
					distances.put(neighbor.getDestination(), altDistance);
					previous.put(neighbor.getDestination(),
							new Route(currentAirport, neighbor.getDestination(), altDistance));
					queue.add(new Route(currentAirport, neighbor.getDestination(), altDistance));
				}
			}
		}

		return buildPath(previous, endCode);
	}

	private List<Route> buildPath(Map<String, Route> previous, String endCode) {

		LinkedList<Route> path = new LinkedList<>();
		Route step = previous.get(endCode);

		// Check if a path exists
		if (step == null) {
			return path; // No path
		}
		path.add(step);
		while (previous.containsKey(step.getSource())) {
			step = previous.get(step.getSource());
			path.add(step);
		}
		Collections.reverse(path);
		return path;
	}

	public Map<String, List<Route>> getRoutesBySourceAirportCode() {
		return routesBySourceAirportCode;
	}

	public List<String> getAirports() {
		return airports;
	}

	public List<Route> getAllRoutes() {

		List<Route> allRoutes = new ArrayList<>();

		for (String airportCode : routesBySourceAirportCode.keySet()) {
			List<Route> routes = routesBySourceAirportCode.get(airportCode);
			allRoutes.addAll(routes);
		}

		return allRoutes;
	}
}