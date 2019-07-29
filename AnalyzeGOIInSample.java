/**
 * Class to take a genome sample and find how many genes of interest it contains
 * @author Stephen Pollo
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class AnalyzeGOIInSample {
	
	private TreeMap<String, String> geneDescriptions = new TreeMap<String, String>();
	private ArrayList<String> descriptions = new ArrayList<String>();
	private ArrayList<String> allIds = new ArrayList<String>();
	private ArrayList<String> uniqueIds = new ArrayList<String>();
	
	/**
	 * Constructor
	 * @param genomeGff the path to the file that contains the genome gff file that has
	 * all the gene information paired to the gene ids
	 */
	public AnalyzeGOIInSample(String genomeGff) {
		
		/*
		 * Attempt to read the file provided.
		 * Catch exceptions thrown if errors occur
		 */
		try {
			File gff = new File(genomeGff);
		    Scanner in = new Scanner(gff);
		    String line;
		    String [] vals;
		    
		    while (in.hasNextLine()) {
		    	line = in.nextLine();
		    	
		    	if (line.charAt(0) == '#') {
		    		// Comment line, skip
		    		continue;
		    	}
		    	else {
		    		// Populate treemap with id, description pairs of gene entries
		    		vals = line.split("\t");
		    		
		    		if (vals[2].equalsIgnoreCase("gene")) {
		    			String key = vals[8].substring(3, vals[8].indexOf(';'));
			    		String value = vals[8].substring(vals[8].indexOf("description=") + 12);
			    		
			    		geneDescriptions.put(key, value);
		    		} else {
		    			// Skip
		    			continue;
		    		}
		    	
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
			usage();
			System.exit(1);
		}
	} // end constructor
	
	/**
	 * Method to count the number of genes of interest in a list of gene descriptions
	 * @param goi the name of the gene of interest
	 * @param geneDescriptions the list of gene descriptions 
	 * @return the count of genes of interest in the list
	 */
	public int countGOI(String goi, ArrayList<String> geneDescriptionList) {
		int count = 0;
		
		for (int i = 0; i < geneDescriptionList.size(); i++) {
			if (geneDescriptionList.get(i).equalsIgnoreCase(goi)) {
				count++;
			} else if (geneDescriptionList.get(i).contains(goi)) {
				count++;
			} else if (geneDescriptionList.get(i).contains(goi.toLowerCase())) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Method to parse the bedtools output from the bedtools wrapper class to
	 * pull out the gene id then use it to find the matching gene description
	 * from the original gff file
	 * @param bedtoolsOutput the ArrayList<String> output from the BedtoolsWrapper class
	 * @return an ArrayList of the gene descriptions of the genes in the bedtools output
	 */
	public ArrayList<String> convertGeneIdsToDescriptions(ArrayList<String> bedtoolsOutput) {
		descriptions.clear();
		allIds.clear();
		uniqueIds.clear();
		
		for (int i = 0; i < bedtoolsOutput.size(); i++) {
			String [] bedLine = bedtoolsOutput.get(i).split("\t");
			String id = bedLine[11].substring(bedLine[11].indexOf("sequence ") + 9, bedLine[11].indexOf("-t26_1-p1"));
			allIds.add(id);
		}
		
		uniqueIds = filterUniqueIds(allIds);
		
		for (int i = 0; i < uniqueIds.size(); i++) {
			String desc = geneDescriptions.get(uniqueIds.get(i));
			descriptions.add(desc);
		}

		return descriptions;
	}
	
	/**
	 * Method to filter duplicate ids out of array list of gene ids
	 * @param ids the arraylist of all ids
	 * @return an arraylist of only the unique ids
	 */
	private ArrayList<String> filterUniqueIds(ArrayList<String> ids) {
		ArrayList<String> toFilter = new ArrayList<String>(ids);
		toFilter.sort(null);
		// Make sure all ids are unique
		for (int i = 1; i < toFilter.size(); i++) {
			if (toFilter.get(i).equalsIgnoreCase(toFilter.get(i - 1))) {
				toFilter.remove(i);
				i--;
			}
		}
		
		return toFilter;
	}
	
	/**
	 * Debugging method. View key value pairs in the tree.
	 */
	public void printTree() {
		System.out.println(geneDescriptions.toString());
	}
	
	/**
	 * Error message.
	 */
	public void usage() {
		System.err.println("Could not find required gff file");
		System.err.println("You must provide a gff file of the proteins that were mapped that links the id and the description");
	}
	
	/**
	 * Method to write the constructed treemap to a file
	 * @param logName the name of the log file to write
	 */
	public void writeGffLog(String logName) {
		// Write the treemap of the genes from the gff file to a log file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			out.write(geneDescriptions.toString() + "\n");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Debugging method to write the current list of all gene ids from the bedtools output to a file
	 * @param logName the name of the log file to write
	 */
	public void writeAllIdsLog(String logName) {
		// Write the arraylist of all ids parsed from bedtools output to a log file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < allIds.size(); i++) {
				out.write(allIds.get(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Debugging method to write the current list of unique gene ids from the bedtools output to a file
	 * @param logName the name of the log file to write
	 */
	public void writeUniqueIdsLog(String logName) {
		// Write the arraylist of the filtered unique ids from the bedtools output to a log file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < uniqueIds.size(); i++) {
				out.write(uniqueIds.get(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Debugging method to write the current gene descriptions to a file
	 * @param logName the name of the log file to write
	 */
	public void writeDescriptionsLog(String logName) {
		// Write the descriptions of the unique ids to a log file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < descriptions.size(); i++) {
				out.write(descriptions.get(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter for the current list of all ids
	 * NOTE this list gets overwritten every time the convertGeneIdsToDescriptions() method is called
	 * @return ArrayList<String> of all ids from the bedtools file
	 */
	public ArrayList<String> getAllIds() {
		return allIds;
	}
	
	/**
	 * Getter for the current list of unique ids
	 * NOTE this list gets overwritten every time the convertGeneIdsToDescriptions() method is called
	 * @return ArrayList<String> of the unique ids from the bedtools file
	 */
	public ArrayList<String> getUniqueIds() {
		return uniqueIds;
	}
	
	/**
	 * Getter for the current list of gene descriptions from the bedtools file
	 * NOTE this list gets overwritten every time the convertGeneIdsToDescriptions() method is called
	 * @return ArrayList<String> of gene descriptions from the bedtools file
	 */
	public ArrayList<String> getDescriptions() {
		return descriptions;
	}
	
} // end class AnalyzeGOIInSample
