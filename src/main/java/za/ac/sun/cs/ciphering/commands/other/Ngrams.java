package za.ac.sun.cs.ciphering.commands.other;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Corpus;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.Command;

public class Ngrams extends Command {

	private static final Ngrams instance = new Ngrams();

	public static Ngrams getInstance() {
		return instance;
	}

	private Ngrams() {
		Ciphering.addOption(commandOptions, "1", null, "display 1-grams (default)");
		Ciphering.addOption(commandOptions, "2", null, "display 2-grams");
		Ciphering.addOption(commandOptions, "3", null, "display 3-grams");
		Ciphering.addOption(commandOptions, "no1", null, "do not display 1-grams");
		Ciphering.addOption(commandOptions, "no2", null, "do not display 2-grams (default)");
		Ciphering.addOption(commandOptions, "no3", null, "do not display 3-grams (default)");
		Ciphering.addOption(commandOptions, "n/normalized", null, "display n-grams as normalized");
	}

	@Override
	public String getName() {
		return "ngrams";
	}

	@Override
	public String getDescription() {
		return "Display all n-grams";
	}

	@Override
	public void handle0(CommandLine line) throws ErrorException {
		boolean one = true, two = false, tre = false, normalized = false;
		if (line.hasOption("no1")) { one = false; }
		if (line.hasOption('1')) { one = true; }
		if (line.hasOption("no2")) { two = false; }
		if (line.hasOption('2')) { two = true; }
		if (line.hasOption("no3")) { tre = false; }
		if (line.hasOption('3')) { tre = true; }
		if (line.hasOption('n')) { normalized = true; }
		
		if (one) { dump1(normalized); }
		if (two) { dump2(normalized); }
		if (tre) { dump3(normalized); }
	}

	private void dump1(boolean normalized) {
		EntryMessage e = LOGGER.traceEntry("Ngrams.dump1(normalized={})", normalized);
		int n = Ciphering.filterSize();
		int printed = 0;
		for (int i = -1; i < n; i++) {
			long x = normalized ? Corpus.getNormalizedNgram(i) : Corpus.getNgram(i);
			if (x > 0) {
				if ((printed > 0) && ((printed % 5) == 0)) { System.out.print("\n"); }
				System.out.printf("%c %9d | ", Ciphering.unfilter(i), x);
				printed++;
			}
		}
		if ((printed > 0) && ((printed % 5) > 0)) { System.out.print("\n"); }
		LOGGER.traceExit(e);
	}

	private void dump2(boolean normalized) {
		EntryMessage e = LOGGER.traceEntry("Ngrams.dump2(normalized={})", normalized);
		int n = Ciphering.filterSize();
		int printed = 0;
		for (int i = -1; i < n; i++) {
			for (int j = -1; j < n; j++) {
				long x = normalized ? Corpus.getNormalizedNgram(i, j) : Corpus.getNgram(i, j);
				if (x > 0) {
					if ((printed > 0) && ((printed % 5) == 0)) { System.out.print("\n"); }
					System.out.printf("%c%c %8d | ", Ciphering.unfilter(i), Ciphering.unfilter(j), x);
					printed++;
				}
			}
		}
		if ((printed > 0) && ((printed % 5) > 0)) { System.out.print("\n"); }
		LOGGER.traceExit(e);
	}
	
	private void dump3(boolean normalized) {
		EntryMessage e = LOGGER.traceEntry("Ngrams.dump3(normalized={})", normalized);
		int n = Ciphering.filterSize();
		int printed = 0;
		for (int i = -1; i < n; i++) {
			for (int j = -1; j < n; j++) {
				for (int k = -1; k < n; k++) {
					long x = normalized ? Corpus.getNormalizedNgram(i, j, k) : Corpus.getNgram(i, j, k);
					if (x > 0) {
						if ((printed > 0) && ((printed % 5) == 0)) {System.out.print("\n"); }
						System.out.printf("%c%c%c %7d | ", Ciphering.unfilter(i), Ciphering.unfilter(j), Ciphering.unfilter(k), x);
						printed++;
					}
				}
			}
		}
		if ((printed > 0) && ((printed % 5) > 0)) { System.out.print("\n"); }
		LOGGER.traceExit(e);
	}
	
}
