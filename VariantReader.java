/**
 * Class to read a vcf file of structural variants produced by sniffles
 * @author Stephen Pollo
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class VariantReader {

	private ArrayList<SVSize> variants = new ArrayList<SVSize>();
	
	/**
	 * Main constructor. Reads the structural variants and parses the id and size of each
	 * @param vcfFile the vcf file containing the structural variants
	 */
	public VariantReader(String vcfFile) {
		
		/*
		 * Attempt to read the file provided.
		 * Catch exceptions thrown if errors occur
		 */
		try {
			File vcf = new File(vcfFile);
		    Scanner in = new Scanner(vcf);
		    String line;
		    String [] vals;
		    
		    while (in.hasNextLine()) {
		    	line = in.nextLine();
		    	
		    	if (line.charAt(0) == '#') {
		    		// Comment line, skip
		    		continue;
		    	} else {
		    		vals = line.split("\t");
		    		
		    		String s = vals[7].substring(vals[7].indexOf("SVLEN=") + 6);
		    		s = s.substring(0, s.indexOf(';'));
		    		int size = Integer.parseInt(s);
		    		size = Math.abs(size);
		    		String svID = vals[2];
		    		SVSize v = new SVSize(svID, size);
		    		variants.add(v);
		    	}
		    	
		    } // end while reading file
		    
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
	} // end constructor
	
	/**
	 * Debugging method. Prints the contents of the variants ArrayList
	 */
	public void printVariants() {
		for (int i = 0; i < variants.size(); i++) {
			System.out.println(variants.get(i).getID() + "\t" + variants.get(i).getSize());
		}
	}
	
	/**
	 * Getter for the variants read from the file
	 * @return an ArrayList<SVSize> of the variants read from the vcf file
	 */
	public ArrayList<SVSize> getVariants() {
		return variants;
	}
	
	/**
	 * Debugging method to write the variants results to a file
	 * @param logName the name of the log file to write
	 */
	public void writeVariantsResults(String logName) {
		// Write the variants arraylist to a file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < variants.size(); i++) {
				out.write(variants.get(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
} // end class VariantReader
