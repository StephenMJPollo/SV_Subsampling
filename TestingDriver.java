import java.io.File;

public class TestingDriver {

	public static void main(String[] args) {
		
		// Check command line arguments for the genome file in fasta format
		if (args.length != 1) {
			return;
		}
		
		File inputFile = new File(args[0]);
		GenomeSample test = new GenomeSample(inputFile);
		
		test.printIndex();
		System.out.println();
		System.out.println(test.getGenomeSize());
		System.out.println();
		
		Sample s;
		
		for (int i = 0; i < 10; i++) {
			s = test.getRandomSample(61000);
			System.out.println(s.getChr() + "\t" + s.getStartCoord() + "\t" + s.getEndCoord());
		}
			
	} // end main

} // end class TestingDriver
