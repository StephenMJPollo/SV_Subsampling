/**
 * Class to run a genome sampling experiment to get the distribution of a gene of interest
 * in genome samples of a given size
 * @author Stephen Pollo
 */

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GetDistOfGOI {

	public final int NUMBER_OF_SAMPLES = 10000;
	private String goi;
	private int sampleSize;
	private int [] samples;
	private GenomeSample gs;
	private BedtoolsWrapper bw;
	private AnalyzeGOIInSample ags;
	private ArrayList<String> bedOut;
	private ArrayList<String> descsOut;
	private ArrayList<Sample> genomeSamples;
	private String logPrefix;
	
	/**
	 * Constructor.
	 * @param genomeSample the GenomeSample object that will be used to sample the genome
	 * @param bedtools the BedtoolsWrapper object that will be used to call bedtools
	 * @param analyze the AnalyzeGOIInSample object that will be used to get the number of genes of interest
	 */
	public GetDistOfGOI(GenomeSample genomeSample, BedtoolsWrapper bedtools, AnalyzeGOIInSample analyze, String prefix) {
		gs = genomeSample;
		bw = bedtools;
		ags = analyze;
		logPrefix = prefix;
		
		samples = new int[NUMBER_OF_SAMPLES];
		bedOut = new ArrayList<String>();
		descsOut = new ArrayList<String>();
		genomeSamples = new ArrayList<Sample>();
		
		gs.writeIndexLog(prefix + "_genome_index.txt");
		ags.writeGffLog(prefix + "_gff_treemap.txt");
	}
	
	/**
	 * Public wrapper for the private method to get the distribution of the 
	 * gene of interest
	 * @param size the size of the samples to take
	 * @param geneOfInterest the name of the gene of interest
	 * @param id a unique id for the distribution. Will be used as a prefix for the output files
	 */
	public int[] getDist(int size, String geneOfInterest, String id) {
		//Initialize samples array to an error value of -1
		for (int i = 0; i < samples.length; i++) {
			samples[i] = -1;
		}
		sampleSize = size;
		goi = geneOfInterest;
		getDist(id);
		return samples;
	}
	
	/**
	 * Method to get the distribution of the gene of interest from samples of a specified size
	 * from the genome. Also logs all results
	 * @param sampleId a unique identifier for the distribution. Will be used as a prefix for output files
	 */
	private void getDist(String sampleId) {
		// Open log files
		try {
			BufferedWriter bedResOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPrefix + "_" + sampleId + "_bedtools_results.txt")));
			BufferedWriter allIdsOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPrefix + "_" + sampleId + "_allIds.txt")));
			BufferedWriter uniIdsOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPrefix + "_" + sampleId + "_uniqueIds.txt")));
			BufferedWriter geneDesOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPrefix + "_" + sampleId + "_geneDescriptions.txt")));

			for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
				if (i%100 == 0) {
					System.err.println("Finished " + i + " samples");
				}
				Sample s = gs.getRandomSample(sampleSize);
				genomeSamples.add(s);
				
				bedOut.clear();;
				descsOut.clear();
				bedOut = bw.findOverlappingGenes(s);
				descsOut = ags.convertGeneIdsToDescriptions(bedOut);
				
				ArrayList<String> all = ags.getAllIds();
				ArrayList<String> uni = ags.getUniqueIds();
				ArrayList<String> descs = ags.getDescriptions();
				
				//Store count of GOI in samples array
				samples[i] = ags.countGOI(goi, descsOut);
				
				//Write to logs
				bedResOut.write("Sample " + (i + 1) + "\n");
				allIdsOut.write("Sample " + (i + 1) + "\n");
				uniIdsOut.write("Sample " + (i + 1) + "\n");
				geneDesOut.write("Sample " + (i + 1) + "\n");
				for (int j = 0; j < bedOut.size(); j++) {
					bedResOut.write(bedOut.get(j) + "\n");
				}
				for (int j = 0; j < all.size(); j++) {
					allIdsOut.write(all.get(j) + "\n");
				}
				for (int j = 0; j < uni.size(); j++) {
					uniIdsOut.write(uni.get(j) + "\n");
				}
				for (int j = 0; j < descs.size(); j++) {
					geneDesOut.write(descs.get(j) + "\n");
				}
				
				bedResOut.write("\n");
				allIdsOut.write("\n");
				uniIdsOut.write("\n");
				geneDesOut.write("\n");
			}
			
			bedResOut.close();
			allIdsOut.close();
			uniIdsOut.close();
			geneDesOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Getter method for the list of samples in the distribution
	 * @return an ArrayList of Sample
	 */
	public ArrayList<Sample> getGenomeSamples() {
		return genomeSamples;
	}
	
	/**
	 * Debugging method to write all samples to a file
	 * @param logName the name of the log file to write
	 */
	public void writeSampleList(String logName) {
		// Writes the list of all samples taken to file
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logName)));
			for (int i = 0; i < genomeSamples.size(); i++) {
				out.write(genomeSamples.get(i).toString() + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
} // end class GetDistOfGOI
