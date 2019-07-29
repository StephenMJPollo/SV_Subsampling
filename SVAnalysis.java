/**
 * Driver class for genome subsampling for structural variants experiment
 * @author Stephen Pollo
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SVAnalysis {
	
	public static void main(String[] args) {
		
		String goi = "VSP";
		
		/*
		 * Need: 1) genome file in fasta, 2) exonerate mapping file in gff, 3) gff file of reference genome, 4) vcf file from sniffles of variants,
		 * 5) prefix of output files
		 */
		
		// Check command line args
		if (args.length != 5) {
			usage();
			return;
		}
		
		String prefix;
		if (args[4].isEmpty()) {
			prefix = "out";
		} else {
			prefix = args[4];
		}
		
		new File(prefix).mkdir();
		
		File inputFile = new File(args[0]);
		GenomeSample gs = new GenomeSample(inputFile);
		BedtoolsWrapper bw = new BedtoolsWrapper(args[1]);
		AnalyzeGOIInSample ags = new AnalyzeGOIInSample(args[2]);
		VariantReader vr = new VariantReader(args[3]);
		GetDistOfGOI dist = new GetDistOfGOI(gs, bw, ags, prefix + "/" + prefix);
		VariantAnalyzer va = new VariantAnalyzer(vr, dist, prefix + "/" + prefix);
		
		va.runExperiment(goi);
		ArrayList<Integer> results = va.getCutOffs();
		ArrayList<SVSize> vars = vr.getVariants();
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results_" + goi + ".txt")));		
			for (int i = 0; i < results.size(); i++) {
				out.write(vars.get(i).getID() + "\t" + results.get(i).toString() + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} // end main
	
	/**
	 * Usage message detailing required arguments
	 */
	public static void usage() {
		System.out.println("You MUST provide four arguments in the following order:\n"
				+ "Genome file in fasta format, exonerate mapping file in gff format, "
				+ "gff file of reference genome, vcf file of called structural variants, "
				+ "prefix of output files");
	}
	
} // end class SVAnalysis
