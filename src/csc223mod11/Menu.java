package csc223mod11;

public class Menu {
	
	private static boolean firstDisplay = true;

    public static void displayMenu() {
    	if (firstDisplay) {
    		firstDisplay = false;
    	} else {
    		System.out.println();
    	}
        System.out.println("Airport Graph Application");
        System.out.println("1. Summarize Airport Dataset");
        System.out.println("2. Calculate Flight Path");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }
}