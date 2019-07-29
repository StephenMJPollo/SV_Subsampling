import java.io.File;

public class DistributionDriver {

	public static void main(String[] args) {

		final int size = 61000;
		
		/*
		 * Must provide genome file in fasta, path to exonerate file, genome gff file
		 */
		
		File inputFile = new File(args[0]);
		GenomeSample gs = new GenomeSample(inputFile);
		BedtoolsWrapper bw = new BedtoolsWrapper(args[1]);
		AnalyzeGOIInSample ags = new AnalyzeGOIInSample(args[2]);
		GetDistOfGOI dist = new GetDistOfGOI(gs, bw, ags, "Driver_test");
		
		int [] distribution = dist.getDist(size, "VSP", "test_61000_VSP");
		
		dist.writeSampleList("samples.txt");
		
		for (int i = 0; i < distribution.length; i++) {
			//System.out.println(distribution[i]);
		}

	} // end main

} // end class DistributionDriver
