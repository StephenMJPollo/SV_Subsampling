/**
 * Class to use bedtools to find overlapping genes
 * from a file containing the results of mapping proteins to the genome
 * using exonerate
 * @author Stephen Pollo
 */

import java.io.*;
import java.util.ArrayList;

public class BedtoolsWrapper {

	private String exoneratePath; // path to the gff file created from mapping proteins to the genome with exonerate
	private ArrayList<String> result = new ArrayList<String>();
	
	/**
	 * Constructor
	 * @param exonerateFile path to the gff file created from 
	 * mapping proteins to the genome with exonerate
	 */
	public BedtoolsWrapper(String exonerateFile) {
		this.exoneratePath = exonerateFile;
	}
	
	/**
	 * Method to run bedtools (in bash process) to find the intersect between a genomic region and
	 * a gff file containing the results of mapping proteins to the genome with exonerate
	 * @param s a Sample object of the genomic region to check
	 */
	public ArrayList<String> findOverlappingGenes(Sample s) {
		result.clear();
		String query = s.getChr() + "\t" + s.getStartCoord() + "\t" + s.getEndCoord();
		
		// Write query to tmp file so bedtools can act on it in external process
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp.txt")));
			out.write(query);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Run bedtools on query and capture result
		try {
			Process p = Runtime.getRuntime().exec("bedtools intersect -a tmp.txt -b " + exoneratePath + " -wb");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = in.readLine();
			
			while(line != null) {
				result.add(line);
				line = in.readLine();
			}
			
			int exitCode = p.waitFor();
			
			if (exitCode != 0) {
				System.err.println("bedtools process finished with exit code " + exitCode);
				System.exit(1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Remove the tmp file
		File tmp = new File("tmp.txt");
		tmp.delete();
		
		return result;
	}
	
	/**
	 * Debugging method. Prints the contents of the results of finding
	 * overlapping genes
	 */
	public void printOverlappingGenes() {
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}
	
	/**
	 * Debugging method to write the current bedtools results to a file
	 * NOTE this list gets overwritten every time the findOverlappingGenes() method is called
	 * @param logName the name of the log file to write
	 */
	public void writeBedtoolsResults(String logName) {
		// Write the result arraylist to a file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < result.size(); i++) {
				out.write(result.get(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter for the current bedtools output
	 * NOTE this list gets overwritten every time the findOverlappingGenes() method is called
	 * @return an ArrayList<String> of the current bedtools output
	 */
	public ArrayList<String> getBedtoolsOutput() {
		return result;
	}
	
} // end class BedtoolsWrapper
