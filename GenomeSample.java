/**
 * Class to take a genome as a fasta file and a non-zero positive integer
 * and return a random region from the genome of that size
 * @author Stephen Pollo
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GenomeSample {

	/**
	 * Defines an object for tracking the length of each chromosome for faster indexing
	 * @author Stephen Pollo
	 */
	private class Chrs {
		String chr;
		int start0; // 0-index of base 1 of the chromosome
		int endLength; // index of last base of the chromosome
		
		/**
		 * Constructor
		 * @param chr1 the chromosome
		 * @param len the final length of the chromosome
		 */
		public Chrs(String chr1, int start, int len) {
			chr = chr1;
			start0 = start;
			endLength = len;
		}
		
		public String toString() {
			String s = chr + "\t" + start0 + "\t" + endLength;
			return s;
		}
	}
	
	private ArrayList<Chrs> genomeIndex = new ArrayList<Chrs>();
	private int genomeSize;
	private int sampleSize;
	private int largestChrSize;
	
	/**
	 * Alternate constructor, to specify a size right away
	 * @param genome the genome in fasta format
	 * @param size a positive non-zero integer representing the size of the region to sample from the genome
	 */
	public GenomeSample(File genome, int size) {
		this(genome);
		sampleSize = size;
	}

	/**
	 * Main constructor
	 * @param genome the genome in fasta format
	 */
	public GenomeSample(File genome) {
		largestChrSize = 0;
		
		/*
		 * Attempt to read the file provided.
		 * Catch exceptions thrown if errors occur
		 */
		try {
		    Scanner in = new Scanner(genome);

		    String line;
		    String currentChr = "";
		    int totalBases = 0;
		    
		    while (in.hasNextLine()) {
		    	line = in.nextLine();
		    	
		    	// Populate the genome index
		    	
		    	if (line.charAt(0) == '>') {
		    		if (currentChr.equalsIgnoreCase("")) {
		    			// Do nothing
		    		}
		    		else {
		    			// Add previous chromosome to index
		    			if (genomeIndex.size() == 0) {
		    				Chrs c = new Chrs(currentChr, 0, totalBases - 1);
		    				genomeIndex.add(c);
		    				largestChrSize = totalBases;
		    			}
		    			else {
		    				Chrs c = new Chrs(currentChr, genomeIndex.get(genomeIndex.size() - 1).endLength + 1, totalBases - 1);
			    			genomeIndex.add(c);
			    			if ((c.endLength - c.start0 + 1) > largestChrSize) {
			    				largestChrSize = c.endLength - c.start0 + 1;
			    			}
		    			}
		    			
		    		}
		    		
		    		currentChr = line.substring(1);
		    	}
		    	else {
		    		totalBases += line.length();
		    	}
		    	
		    } // end while reading file
		    
		    Chrs c = new Chrs(currentChr, genomeIndex.get(genomeIndex.size() - 1).endLength + 1, totalBases - 1);
		    genomeIndex.add(c);
		    if ((c.endLength - c.start0 + 1) > largestChrSize) {
				largestChrSize = c.endLength - c.start0 + 1;
			}
		    
		    genomeSize = totalBases;
		    
		    in.close();
		} // end file reading try block

		/*
		 * Print the error if one was found and terminate
		 * the program
		 */
		catch(FileNotFoundException e) {
			e.toString();
			System.out.println("Problem finding genome file");
			usage();
			System.exit(1);
		}
	} // end constructor
	
	/**
	 * Method to randomly find a sample region from the provided genome of the specified size
	 * @param size the size of region to sample
	 * @return an object of type Sample that contains the chromosome and coordinates of the sample
	 */
	public Sample getRandomSample(int size) {
		if (size > largestChrSize) {
			// Sample of this size cannot be taken
			throw new IllegalArgumentException("The size " + size + " is too big to sample from this genome");
		}
		sampleSize = size;
		int sampleIndexStart = -1;
		int sampleIndexStop = -1;
		boolean noFit = true;
		Sample s = null;
		
		while(noFit) {
			int[] sample = getSampleSpot();
			int randSpot = sample[0];
			int chosenChrIndex = sample[1];
			
			// Use randSpot as start of sampled region, extend to get actual sample
			if ((randSpot + size) <= genomeIndex.get(chosenChrIndex).endLength) {
				// Complete sample fits with extension downstream, return sample
				sampleIndexStart = randSpot;
				sampleIndexStop = randSpot + size;
				s = new Sample(genomeIndex.get(chosenChrIndex).chr, sampleIndexStart - genomeIndex.get(chosenChrIndex).start0 + 1, 
						sampleIndexStop - genomeIndex.get(chosenChrIndex).start0 + 1);
				noFit = false;
			}
			else {
				// Sample would extend beyond edge of contig
				// Resample
				continue;
			}
		}
		
		return s;
	}
	
	/**
	 * Method to randomly select from the genome a chromosome (and position) 
	 * @return an integer array of size 2: the random coordinate in the genome to use for the sample, 
	 * and the index in genomeIndex of the corresponding chromosome
	 */
	private int[] getSampleSpot() {
		int randomSpot = -1;
		int chosenChr = -1;
		
		randomSpot = (int)(Math.random()*(genomeSize - 1));

		for (int i = 0; i < genomeIndex.size(); i++) {
			if (randomSpot <= genomeIndex.get(i).endLength) {
				if (randomSpot >= genomeIndex.get(i).start0) {
					chosenChr = i;
					break;
				}
			}
		}
		
		int[] vals = {randomSpot, chosenChr};
		return vals;
	}
	
	/**
	 * Getter for genomeSize
	 * @return size of genome in file provided
	 */
	public int getGenomeSize() {
		return genomeSize;
	}
	
	/**
	 * Getter for sampleSize
	 * @return the size of the samples randomly selected from the genome
	 */
	public int getSampleSize() {
		return sampleSize;
	}
	
	/**
	 * Debugging method.
	 * Prints the index to the console
	 */
	public void printIndex() {
		for (int i = 0; i < genomeIndex.size(); i++) {
			System.out.println(genomeIndex.get(i).chr + "\t" + genomeIndex.get(i).start0 + "\t" + genomeIndex.get(i).endLength);
		}
	}
	
	/**
	 * Prints usage instructions to the console.
	 */
	public void usage() {
		System.out.println("Must provide a genome file in fasta format and a positive, non-zero integer");
	}
	
	/**
	 * Method to write the constructed genome index to a file
	 * @param logName the name of the log file to write
	 */
	public void writeIndexLog(String logName) {
		// Write the genome index and genome size to a file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < genomeIndex.size(); i++) {
				out.write(genomeIndex.get(i).toString() + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
} //end class GenomeSample
