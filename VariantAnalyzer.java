/**
 * Class to run the sampling experiment on all the structural variants read from the vcf file
 * and analyze the probabilities of finding the observed number of genes of interest
 * @author Stephen Pollo
 */

import java.util.ArrayList;
import java.util.Arrays;

public class VariantAnalyzer {

	private VariantReader vr;
	private GetDistOfGOI dist;
	private ArrayList<SVSize> variants;
	private ArrayList<Integer> cutoffs;
	private String p;
	
	/**
	 * Main constructor
	 * @param reader the variant reader object that is being used to read the vcf file of structural variants
	 * @param getDist the GetDistOfGOI object that will be used to find the distribution of the gene of interest
	 */
	public VariantAnalyzer(VariantReader reader, GetDistOfGOI getDist, String prefix) {
		vr = reader;
		dist = getDist;
		variants = vr.getVariants();
		cutoffs = new ArrayList<Integer>();
		p = prefix;
	}
	
	/**
	 * Method to find the distributions of a gene of interest for all structural variant sizes in the vcf file
	 * @param goi the gene of interest
	 */
	public void runExperiment(String goi) {
		cutoffs.clear();
		
		for (int i = 0; i < variants.size(); i++) {
			int [] goiDist = null;
			try {
			goiDist = dist.getDist(variants.get(i).getSize(), goi, "var_" + variants.get(i).getID() + "_" + variants.get(i).getSize() + "_" + goi);
			} catch (IllegalArgumentException e) {
				System.err.println(e);
				System.err.println("Variant " + variants.get(i).getID() + " has a size that cannot be sampled");
				System.err.println("Skipping variant " + variants.get(i).getID() + ", sorry 'bout it");
				cutoffs.add(0);
				continue;
			}
			Arrays.sort(goiDist);
			int index95 = calculatePercentile(95.0, goiDist);
			int cutoffVal95 = goiDist[index95];
			cutoffs.add(cutoffVal95);
			dist.writeSampleList(p + "_var_" + variants.get(i).getID() + "_" + variants.get(i).getSize() + "_" + goi + "_samples.txt");
		}
	}
	
	/**
	 * Calculates the percentile given using the nearest-rank method
	 * @param percentile the percentile value to be used. Must be >0 and <=100
	 * @param counts the int[] of counts of the goi
	 * @return the index in the int[] of the percentile cutoff value
	 */
	private int calculatePercentile(double percentile, int[] counts) {
		if (percentile <= 0 || percentile > 100.0) {
			throw new IllegalArgumentException("percentile must be >0 and <= 100");
		}
		double index = percentile / 100.0 * counts.length;
		return (int)Math.ceil(index);
	}
	
	/**
	 * Getter for the current cutoffs output
	 * NOTE this list gets overwritten every time the runExperiment() method is called
	 * @return an ArrayList<Integer> of the current cutoffs output
	 */
	public ArrayList<Integer> getCutOffs() {
		return cutoffs;
	}
	
} // end class VariantAnalyzer
