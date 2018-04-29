package za.ac.sun.cs.ciphering;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

public class Corpus {

	private static final Logger LOGGER = LogManager.getLogger("CIPHERING");

	private static final int MAX_NGRAM = 38;

	private static final int NGRAM_SCALE = 1000000;

	private static final String DEFAULT_CORPUS = "wiki.txt";

	private static String corpusFilename = DEFAULT_CORPUS;

	private static final List<String> corpus = new ArrayList<>();

	private static final Options corpusOptions = new Options();
	
	private static final long[] ngram1 = new long[MAX_NGRAM];

	private static final long[][] ngram2 = new long[MAX_NGRAM][MAX_NGRAM];

	private static final long[][][] ngram3 = new long[MAX_NGRAM][MAX_NGRAM][MAX_NGRAM];

	private static final long[] normalizedNgram1 = new long[MAX_NGRAM];
	
	private static final long[][] normalizedNgram2 = new long[MAX_NGRAM][MAX_NGRAM];
	
	private static final long[][][] normalizedNgram3 = new long[MAX_NGRAM][MAX_NGRAM][MAX_NGRAM];
	
	static {
		Ciphering.addOption(corpusOptions, "c/corpus", "name", "use an alternative corpus");
	}

	public static String[] load(final String[] args) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Corpus.load(args={})", () -> Ciphering.argStr(args));
		String[] rest = null;
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(corpusOptions, args, true);
			rest = line.getArgs();
			
			if (line.hasOption('c')) {
				corpusFilename = line.getOptionValue('c');
			}
			LOGGER.trace("corpusFilename={}", corpusFilename);
			InputStream inputStream = Ciphering.openFile(corpusFilename);
			int charCount = 0;
			char prevCh = ' ';
			int ch2 = -1, ch1 = -1, ch0 = -1;
			StringBuilder b = new StringBuilder();
			while (inputStream.available() > 0) {
				char nextCh = (char) inputStream.read();
				if (nextCh == 10) {
					if (b.length() > 0) {
						corpus.add(b.toString());
						b.setLength(0);
					}
					continue;
				}
				char currCh = Ciphering.translate(nextCh);
				if ((currCh != ' ') || (prevCh != ' ')) {
					b.append(currCh);
					prevCh = currCh;
					charCount++;
				}
				ch0 = Ciphering.filter(nextCh);
				if ((ch1 != -1) || (ch0 != -1)) {
					increaseNgrams(ch2, ch1, ch0);
					ch2 = ch1;
					ch1 = ch0;
				}
			}
			inputStream.close();
			normalizeNgram(ngram1, normalizedNgram1);
			normalizeNgram(ngram2, normalizedNgram2);
			normalizeNgram(ngram3, normalizedNgram3);
			LOGGER.trace("corpus: chars={} lines={}", charCount, corpus.size());
		} catch (ParseException x) {
			LOGGER.trace("PARSE_EXCEPTION", x);
			System.out.println(x.getMessage());
			System.out.println("Try \"--help\" option for details.");
			throw new ErrorException();
		} catch (IOException x) {
			LOGGER.trace("IO_EXCEPTION", x);
			System.out.println("Error while reading corpus file \"" + corpusFilename + "\"");
			throw new ErrorException();
		}
		return LOGGER.traceExit(e, rest);
	}

	private static void increaseNgrams(int ch2, int ch1, int ch0) {
		ngram1[ch0 + 1]++;
		ngram2[ch1 + 1][ch0 + 1]++;
		ngram3[ch2 + 1][ch1 + 1][ch0 + 1]++;
	}

	public static void displayOptions(PrintWriter printWriter) {
		Ciphering.displayOptions(printWriter, corpusOptions);
	}

	public static String getCorpusFilename() {
		return corpusFilename;
	}

	public static long getNgram(int i) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		return ngram1[i + 1];
	}

	public static long getNgram(int i, int j) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		assert (j >= -1) && (j < Ciphering.filterSize());
		return ngram2[i + 1][j + 1];
	}
	
	public static long getNgram(int i, int j, int k) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		assert (j >= -1) && (j < Ciphering.filterSize());
		assert (k >= -1) && (k < Ciphering.filterSize());
		return ngram3[i + 1][j + 1][k + 1];
	}

	public static long getNormalizedNgram(int i) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		return normalizedNgram1[i + 1];
	}
	
	public static long getNormalizedNgram(int i, int j) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		assert (j >= -1) && (j < Ciphering.filterSize());
		return normalizedNgram2[i + 1][j + 1];
	}
	
	public static long getNormalizedNgram(int i, int j, int k) {
		assert (i >= -1) && (i < Ciphering.filterSize());
		assert (j >= -1) && (j < Ciphering.filterSize());
		assert (k >= -1) && (k < Ciphering.filterSize());
		return normalizedNgram3[i + 1][j + 1][k + 1];
	}
	
	public static int getSize() {
		return corpus.size();
	}

	public static String get(int index) {
		return corpus.get(index);
	}

	public static long[] calculateNgram1(String text) {
		EntryMessage e = LOGGER.traceEntry("Corpus.calculateNgram1(text={})", text);
		long[] ngrams = new long[MAX_NGRAM];
		int n = text.length();
		for (int i = 0; i < n; i++) {
			int ch = Ciphering.filter(text.charAt(i));
			ngrams[ch + 1]++;
		}
		return LOGGER.traceExit(e, ngrams);
	}

	public static long[][] calculateNgram2(String text) {
		EntryMessage e = LOGGER.traceEntry("Corpus.calculateNgram2(text={})", text);
		long[][] ngrams = new long[MAX_NGRAM][MAX_NGRAM];
		int n = text.length();
		if (n > 0) {
			int ch0 = Ciphering.filter(text.charAt(0));
			for (int i = 1; i < n; i++) {
				int ch1 = Ciphering.filter(text.charAt(i));
				ngrams[ch0 + 1][ch1 + 1]++;
				ch0 = ch1;
			}
		}
		return LOGGER.traceExit(e, ngrams);
	}
	
	public static long[][][] calculateNgram3(String text) {
		EntryMessage e = LOGGER.traceEntry("Corpus.calculateNgram3(text={})", text);
		long[][][] ngrams = new long[MAX_NGRAM][MAX_NGRAM][MAX_NGRAM];
		int n = text.length();
		if (n > 1) {
			int ch0 = Ciphering.filter(text.charAt(0));
			int ch1 = Ciphering.filter(text.charAt(1));
			for (int i = 2; i < n; i++) {
				int ch2 = Ciphering.filter(text.charAt(i));
				ngrams[ch0 + 1][ch1 + 1][ch2 + 1]++;
				ch0 = ch1;
				ch1 = ch2;
			}
		}
		return LOGGER.traceExit(e, ngrams);
	}
	
	public static void normalizeNgram(long[] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[]=...)");
		int n = ngrams.length;
		long total = 0;
		for (int i = 0; i < n; i++) {
			total += ngrams[i];
		}
		for (int i = 0; i < n; i++) {
			ngrams[i] = ngrams[i] * NGRAM_SCALE / total;
		}
		LOGGER.traceExit(e);
	}
	
	public static void normalizeNgram(long[] ngrams, long[] nngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[]=..., nngrams[]=...)");
		int n = ngrams.length;
		for (int i = 0; i < n; i++) {
			nngrams[i] = ngrams[i];
		}
		normalizeNgram(nngrams);
		LOGGER.traceExit(e);
	}
	
	public static long[] getNormalizeNgram(long[] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.getNormalizeNgram(ngrams[]=...)");
		int n = ngrams.length;
		long[] nngrams = new long[n];
		for (int i = 0; i < n; i++) {
			nngrams[i] = ngrams[i];
		}
		normalizeNgram(nngrams);
		return LOGGER.traceExit(e, nngrams);
	}

	public static void normalizeNgram(long[][] ngrams, long[][] nngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[][]=..., nngrams[][]=...)");
		int n = ngrams.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				nngrams[i][j] = ngrams[i][j];
			}
		}
		normalizeNgram(nngrams);
		LOGGER.traceExit(e);
	}
	
	public static void normalizeNgram(long[][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[][]=...)");
		int n = ngrams.length;
		long total = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				total += ngrams[i][j];
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				ngrams[i][j] = ngrams[i][j] * NGRAM_SCALE / total;
			}
		}
		LOGGER.traceExit(e);
	}
	
	public static long[][] getNormalizeNgram(long[][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.getNormalizeNgram(ngrams[][]=...)");
		int n = ngrams.length;
		long[][] nngrams = new long[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				nngrams[i][j] = ngrams[i][j];
			}
		}
		normalizeNgram(nngrams);
		return LOGGER.traceExit(e, nngrams);
	}

	public static void normalizeNgram(long[][][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[][][]=...)");
		int n = ngrams.length;
		long total = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					total += ngrams[i][j][k];
				}
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					ngrams[i][j][k] = ngrams[i][j][k] * NGRAM_SCALE / total;
				}
			}
		}
		LOGGER.traceExit(e);
	}
	
	public static void normalizeNgram(long[][][] ngrams, long[][][] nngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.normalizeNgram(ngrams[][][]=..., nngrams[][][]=...)");
		int n = ngrams.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					nngrams[i][j][k] = ngrams[i][j][k];
				}
			}
		}
		normalizeNgram(nngrams);
		LOGGER.traceExit(e);
	}
	
	public static long[][][] getNormalizeNgram(long[][][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.getNormalizeNgram(ngrams[][][]=...)");
		int n = ngrams.length;
		long[][][] nngrams = new long[n][n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					nngrams[i][j][k] = ngrams[i][j][k];
				}
			}
		}
		normalizeNgram(nngrams);
		return LOGGER.traceExit(e, nngrams);
	}

	public static char[] sortNgram(long[] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.sortNgrams(ngram[]=...)");
		int n = ngrams.length;		
		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++) {
			order[i] = i;
		}
		Arrays.sort(order, (a, b) -> (int) (ngrams[a] - ngrams[b]));
		char[] result = new char[n];
		for (int i = 0; i < n; i++) {
			result[i] = Ciphering.unfilter(order[i]);
		}
		return LOGGER.traceExit(e, result);
	}
	
	public static long dotProduct(long[] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.dotProduct(ngrams[]=...)");
		int n = Math.min(ngrams.length, normalizedNgram1.length);
		long score = 0;
		for (int i = 0; i < n; i++) {
			score += ngrams[i] * normalizedNgram1[i];
		}
		return LOGGER.traceExit(e, Long.MAX_VALUE - score);
	}

	public static long dotProduct(long[][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.dotProduct(ngrams[][]=...)");
		int n = Math.min(ngrams.length, normalizedNgram2.length);
		long score = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				score += ngrams[i][j] * normalizedNgram2[i][j];
			}
		}
		return LOGGER.traceExit(e, Long.MAX_VALUE - score);
	}
	
	public static long dotProduct(long[][][] ngrams) {
		EntryMessage e = LOGGER.traceEntry("Corpus.dotProduct(ngrams[][][]=...)");
		int n = Math.min(ngrams.length, normalizedNgram3.length);
		long score = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					score += ngrams[i][j][k] * normalizedNgram3[i][j][k];
				}
			}
		}
		return LOGGER.traceExit(e, Long.MAX_VALUE - score);
	}
	
}
