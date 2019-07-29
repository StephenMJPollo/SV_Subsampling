/**
 * Class to define the structure of a sample of a genome
 * @author Stephen Pollo
 */

public class Sample {

	// the chromosome and coordinates on that chromosome of the sample
	private String chr;
	private int startCoord;
	private int endCoord;
	
	/**
	 * Constructor
	 * @param c the name of the chromosome of the sample
	 * @param s the start coordinate of the sample
	 * @param e the end coordinate of the sample
	 */
	public Sample(String c, int s, int e) {
		chr = c;
		startCoord = s;
		endCoord = e;
	}
	
	/**
	 * Getter for chromosome name
	 * @return chromosome name
	 */
	public String getChr() {
		return chr;
	}
	
	/**
	 * Getter for sample start coordinate
	 * @return start coordinate
	 */
	public int getStartCoord() {
		return startCoord;
	}
	
	/**
	 * Getter for sample end coordinate
	 * @return end coordinate
	 */
	public int getEndCoord() {
		return endCoord;
	}
	
	/**
	 * @return the values of the Sample object in tab delimited format
	 */
	public String toString() {
		String s = chr + "\t" + startCoord + "\t" + endCoord;
		return s;
	}
} // end class Sample
