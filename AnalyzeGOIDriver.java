import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AnalyzeGOIDriver {

	public static void main(String[] args) {
		
		AnalyzeGOIInSample test = new AnalyzeGOIInSample(args[0]);
		ArrayList<String> bedOut = new ArrayList<String>();
		
		/*
		 * Attempt to read the file provided.
		 * Catch exceptions thrown if errors occur
		 */
		try {
			File bedtools = new File(args[1]);
		    Scanner in = new Scanner(bedtools);
		    String line;
		    
		    while (in.hasNextLine()) {
		    	line = in.nextLine();
		    	bedOut.add(line);
		    	
		    } // end while reading file
		    
		    /*for (int i = 0; i < bedOut.size(); i++) {
		    	System.out.println(bedOut.get(i));
		    }*/
		    
		    in.close();
		} // end file reading try block

		/*
		 * Print the error if one was found and terminate
		 * the program
		 */
		catch(FileNotFoundException e) {
			e.toString();
			System.exit(1);
		}
		
		ArrayList<String> descs = test.convertGeneIdsToDescriptions(bedOut);
		
		for (int i = 0; i < descs.size(); i++) {
			System.out.println(descs.get(i));
		}

	} // end main

} // end class AnalyzeGOIDriver
