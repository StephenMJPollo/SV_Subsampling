/**
 * Class to define the structure of a structural variant
 * @author Stephen Pollo
 */

public class SVSize {

	private String id;
	private int size;
	
	/**
	 * Constructor
	 * @param svID the id of the new structural variant
	 * @param svSize the size of the new structural variant
	 */
	public SVSize(String svID, int svSize) {
		id = svID;
		size = svSize;
	}
	
	/**
	 * Getter for the ID of the structural variant
	 * @return variant id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Getter for the size of the structural variant
	 * @return variant size
	 */
	public int getSize() {
		return size;
	}
	
} // end class SVSize
