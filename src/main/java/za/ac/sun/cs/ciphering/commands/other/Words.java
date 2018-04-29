package za.ac.sun.cs.ciphering.commands.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Corpus;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.Command;

public class Words extends Command {

	private final SortedMap<String, Integer> count = new TreeMap<>();

	private static final Words instance = new Words();

	public static Words getInstance() {
		return instance;
	}

	private Words() {
		Ciphering.addOption(commandOptions, "n/normalized", null, "display n-grams as normalized");
	}

	@Override
	public String getName() {
		return "words";
	}

	@Override
	public String getDescription() {
		return "Display all dictionary words and frequencies";
	}

	@Override
	public void handle0(CommandLine line) throws ErrorException {
		boolean normalized = line.hasOption('n');
		String filename = Corpus.getCorpusFilename();
		try {
			InputStream inputStream = Ciphering.openFile(filename);
			int wordCount = 0;
			StringBuilder b = new StringBuilder();
			while (inputStream.available() > 0) {
				char nextCh = Ciphering.translate((char) inputStream.read());
				if ((nextCh < 'A') || (nextCh > 'Z')) {
					if (b.length() > 0) {
						wordCount++;
						addWord(b.toString());
						b.setLength(0);
					}
				} else {
					b.append(nextCh);
				}
			}
			if (b.length() > 0) {
				wordCount++;
				addWord(b.toString());
			}
			inputStream.close();
			if (normalized) {
				for (String word : count.keySet()) {
					long c = count.get(word) * 1000000L / wordCount;
					System.out.printf("%-12s %8d\n", word, c);
				}
			} else {
				for (String word : count.keySet()) {
					long c = count.get(word);
					System.out.printf("%-12s %8d\n", word, c);
				}
			}
		} catch (IOException x) {
			LOGGER.trace("IO_EXCEPTION", x);
			System.out.println("Error while reading corpus file \"" + filename + "\"");
			throw new ErrorException();
		}
	}

	private void addWord(String word) {
		Integer c = count.get(word);
		if (c == null) {
			count.put(word, 1);
		} else {
			count.put(word, c + 1);
		}
	}

}
