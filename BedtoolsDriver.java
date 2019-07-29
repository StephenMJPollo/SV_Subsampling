/**
 * Driver to test the BedtoolsWrapper class
 * @author Stephen Pollo
 */

import java.util.ArrayList;

public class BedtoolsDriver {

	public static void main(String[] args) {
		ArrayList<String> result;

		BedtoolsWrapper wrap = new BedtoolsWrapper(args[0]);
		Sample test = new Sample("utg484_pilon", 88819, 149819);
		
		result = wrap.findOverlappingGenes(test);
		//wrap.printOverlappingGenes();
		
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
		
	} // end main

} // end class BedtoolsDriver
