package za.ac.sun.cs.ciphering;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

public class Dictionary {

	public static final int SCORE_WORDS = 0;

	public static final int SCORE_MONOGRAMS = 1;
	
	public static final int SCORE_DIGRAMS = 2;
	
	public static final int SCORE_TRIGRAMS = 3;
	
	private static final Logger LOGGER = LogManager.getLogger("CIPHERING");

	private static String dictionaryFilename = null;

	private static final SortedSet<String> dictionary = new TreeSet<>();

	private static final Map<Integer, Set<String>> lenDictionary = new HashMap<>();
	
	private static final Options dictionaryOptions = new Options();

	private static final Map<String, String> misspellings = new HashMap<>();

	static {
		Ciphering.addOption(dictionaryOptions, "d/dictionary", "name", "use an alternative dictionary");
		misspellings.put("THE", "THI");          misspellings.put("AND", "ND");           misspellings.put("OF", "OFF");
		misspellings.put("TO", "TU");            misspellings.put("IN", "IIN");           misspellings.put("THAT", "DAT");
		misspellings.put("I", "EYE");            misspellings.put("HE", "HEE");           misspellings.put("IT", "EET");
		misspellings.put("IS", "EES");           misspellings.put("HIS", "HEES");         misspellings.put("WAS", "WAZ");
		misspellings.put("FOR", "FIR");          misspellings.put("WITH", "WUTH");        misspellings.put("AS", "AZ");
		misspellings.put("YOU", "U");            misspellings.put("BE", "BEE");           misspellings.put("THEY", "DEY");
		misspellings.put("ARE", "R");            misspellings.put("HIM", "HEEM");         misspellings.put("HAVE", "HAF");
		misspellings.put("ON", "ONN");           misspellings.put("MY", "MI");            misspellings.put("BY", "BI");
		misspellings.put("WHICH", "WEECH");      misspellings.put("THIS", "DIS");         misspellings.put("BUT", "BUTT");
		misspellings.put("SAID", "SED");         misspellings.put("ALL", "AL");           misspellings.put("FROM", "FRIM");
		misspellings.put("ME", "MEE");           misspellings.put("MR", "MYSTER");        misspellings.put("HER", "HIR");
		misspellings.put("OR", "UR");            misspellings.put("THEM", "DEM");         misspellings.put("SHALL", "SHAL");
		misspellings.put("WERE", "WERR");        misspellings.put("THERE", "DER");        misspellings.put("WHEN", "WEN");
		misspellings.put("THEIR", "DEIR");       misspellings.put("AN", "A");             misspellings.put("ONE", "WUN");
		misspellings.put("IF", "IFF");           misspellings.put("LORD", "LURD");        misspellings.put("WILL", "WEEL");
		misspellings.put("UP", "IP");            misspellings.put("SHE", "SHEE");         misspellings.put("OUT", "UT");
		misspellings.put("WHAT", "WAT");         misspellings.put("DO", "DU");            misspellings.put("WHO", "HU");
		misspellings.put("PEOPLE", "PEEPS");     misspellings.put("VERY", "VERRI");       misspellings.put("WE", "VEE");
		misspellings.put("SOME", "SUM");         misspellings.put("MAN", "MAHN");         misspellings.put("CAN", "KIN");
		misspellings.put("BEEN", "BIN");         misspellings.put("MORE", "MOR");         misspellings.put("THEN", "DEN");
		misspellings.put("THOU", "DOU");         misspellings.put("WOULD", "WOOLD");      misspellings.put("OTHER", "UDDER");
		misspellings.put("YOUR", "YR");          misspellings.put("TIME", "TYM");         misspellings.put("HAS", "HAZ");
		misspellings.put("NOW", "NOU");          misspellings.put("ABOUT", "ABOOT");      misspellings.put("GOD", "GID");
		misspellings.put("THESE", "DEEZ");       misspellings.put("MADE", "MAD");         misspellings.put("MANY", "MENNY");
		misspellings.put("COME", "CUM");         misspellings.put("BEFORE", "BFOR");      misspellings.put("OLD", "OULDE");
		misspellings.put("LIKE", "LIK");         misspellings.put("KNOW", "KNO");         misspellings.put("LITTLE", "LITL");
		misspellings.put("DOWN", "DOUN");        misspellings.put("AFTER", "AFTR");       misspellings.put("TWO", "ZWEI");
		misspellings.put("THAN", "DAN");         misspellings.put("ANY", "ENNIE");        misspellings.put("MAY", "MEI");
		misspellings.put("OVER", "OVR");         misspellings.put("CALLED", "KALLD");     misspellings.put("HOUSE", "HOOSE");
		misspellings.put("DAY", "DEI");          misspellings.put("SUCH", "SOOCH");       misspellings.put("THEE", "DEE");
		misspellings.put("SAY", "SEY");          misspellings.put("COULD", "CUD");        misspellings.put("GOOD", "GUD");
		misspellings.put("CAME", "CAM");         misspellings.put("MAKE", "MAK");         misspellings.put("GREAT", "GREIT");
		misspellings.put("US", "OUS");           misspellings.put("WAY", "WEI");          misspellings.put("HAND", "HEND");
		misspellings.put("BECAUSE", "BECOZ");    misspellings.put("GO", "GOH");           misspellings.put("EVEN", "IVIN");
		misspellings.put("DID", "DEED");         misspellings.put("WELL", "WEL");         misspellings.put("MUCH", "MAHTCH");
		misspellings.put("FIRST", "FURST");      misspellings.put("SIR", "SUR");          misspellings.put("AGAIN", "AGEN");
		misspellings.put("ONLY", "OWNLI");       misspellings.put("WHERE", "WHEIR");      misspellings.put("SEE", "CEE");
		misspellings.put("SHOULD", "SHULD");     misspellings.put("HERE", "HEER");        misspellings.put("DEAR", "DEER");
		misspellings.put("KING", "KEENG");       misspellings.put("YOUNG", "YUNG");       misspellings.put("AWAY", "AWEI");
		misspellings.put("SON", "SUN");          misspellings.put("EVERY", "EVRY");       misspellings.put("OUR", "OWER");
		misspellings.put("THINGS", "DINGS");     misspellings.put("ITS", "EETS");         misspellings.put("LADY", "LEIDIE");
		misspellings.put("AGAINST", "GINST");    misspellings.put("NEVER", "NVR");        misspellings.put("TAKE", "TEIK");
		misspellings.put("HIMSELF", "HIMSLF");   misspellings.put("HEAD", "HED");         misspellings.put("SAME", "SAM");
		misspellings.put("BACK", "BECK");        misspellings.put("USED", "YEWSED");      misspellings.put("PLACE", "PLAIS");
		misspellings.put("REPLIED", "RIPLYD");   misspellings.put("CHILDREN", "CHILLUN"); misspellings.put("OWN", "OHN");
		misspellings.put("MIGHT", "MAIT");       misspellings.put("PUT", "POOT");         misspellings.put("THROUGH", "THROO");
		misspellings.put("NIGHT", "NITE");       misspellings.put("THINK", "THEENK");     misspellings.put("NAME", "NOM");
		misspellings.put("ANOTHER", "NOTHR");    misspellings.put("TOO", "TUU");          misspellings.put("HATH", "HAV");
		misspellings.put("EYES", "ISE");         misspellings.put("SAYS", "SEZ");         misspellings.put("WORD", "WIRD");
		misspellings.put("LIFE", "LYF");         misspellings.put("WITHOUT", "WITOUT");   misspellings.put("EARTH", "IRF");
		misspellings.put("FACE", "FAIS");        misspellings.put("WORLD", "WIRLD");      misspellings.put("GENTLEMAN", "GNTLMN");
		misspellings.put("TOOK", "TUK");         misspellings.put("LOOK", "LUK");         misspellings.put("YEARS", "YIRS");
		misspellings.put("DOOR", "DAWR");        misspellings.put("BOY", "BOI");          misspellings.put("GIVE", "GIF");
		misspellings.put("THREE", "TRE");        misspellings.put("BUMBLE", "BUMBL");     misspellings.put("DIFFERENT", "DFFRNT");
		misspellings.put("MUST", "MAST");        misspellings.put("ROOM", "RUUM");        misspellings.put("WHY", "WY");
		misspellings.put("THOSE", "DOSE");       misspellings.put("DONE", "DUN");         misspellings.put("BETWEEN", "BTWIIN");
		misspellings.put("SAYING", "SEYIN");     misspellings.put("FOUND", "FOWND");      misspellings.put("NEW", "NU");
		misspellings.put("PART", "PIRT");        misspellings.put("WHILE", "WYL");        misspellings.put("STILL", "STEEL");
		misspellings.put("THOUGHT", "THAWT");    misspellings.put("BROUGHT", "BRAWT");    misspellings.put("WORDS", "WURTS");
		misspellings.put("WOMAN", "WYMEN");      misspellings.put("EVER", "EVR");         misspellings.put("YET", "YIT");
		misspellings.put("UNDER", "UNNR");       misspellings.put("AMONG", "UMUNG");      misspellings.put("GIRL", "GURL");
		misspellings.put("OFTEN", "FTEN");       misspellings.put("USE", "YUZ");          misspellings.put("TOGETHER", "TOGITHR");
		misspellings.put("FATHER", "PADRE");     misspellings.put("WORK", "WIRK");        misspellings.put("BETTER", "BETTR");
		misspellings.put("WATER", "WATR");       misspellings.put("LIGHT", "LITE");       misspellings.put("NOTHING", "NUTTIN");
		misspellings.put("RIGHT", "RITE");       misspellings.put("LOOKING", "LUKIN");    misspellings.put("THOUGH", "THO");
		misspellings.put("ROUND", "RUND");       misspellings.put("HEARD", "HIRD");
	}

	public static String getDictionaryFilename() {
		return dictionaryFilename;
	}

	public static String[] load(final String[] args) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Dictionary.load(args={})", () -> Ciphering.argStr(args));
		String[] rest = null;
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(dictionaryOptions, args, true);
			rest = line.getArgs();
			
			if (line.hasOption('d')) {
				dictionaryFilename = line.getOptionValue('d');
			} else {
				dictionaryFilename = Corpus.getCorpusFilename();
			}
			LOGGER.trace("dictionaryFilename={}", dictionaryFilename);
			InputStream inputStream = Ciphering.openFile(dictionaryFilename);
			int charCount = 0;
			StringBuilder b = new StringBuilder();
			while (inputStream.available() > 0) {
				char nextCh = Ciphering.translate((char) inputStream.read());
				charCount++;
				if ((nextCh < 'A') || (nextCh > 'Z')) {
					if (b.length() > 0) {
						dictionary.add(b.toString());
						b.setLength(0);
					}
				} else {
					b.append(nextCh);
				}
			}
			if (b.length() > 0) {
				dictionary.add(b.toString());
			}
			inputStream.close();
			LOGGER.trace("dictionary: chars={} words={}", charCount, dictionary.size());
		} catch (ParseException x) {
			LOGGER.trace("PARSE_EXCEPTION", x);
			System.out.println(x.getMessage());
			System.out.println("Try \"--help\" option for details.");
			throw new ErrorException();
		} catch (IOException x) {
			LOGGER.trace("IO_EXCEPTION", x);
			System.out.println("Error while reading corpus file \"" + dictionaryFilename + "\"");
			throw new ErrorException();
		}
		return LOGGER.traceExit(e, rest);
	}

	public static Set<String> createDictionary(String text) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.createDictionary(text={})", () -> Ciphering.trunc(text));
		Set<String> dictionary = new HashSet<>();
		StringBuilder b = new StringBuilder();
		int n = text.length();
		for (int i = 0; i < n; i++) {
			char nextCh = Ciphering.translate(text.charAt(i));
			if ((nextCh < 'A') || (nextCh > 'Z')) {
				if (b.length() > 0) {
					dictionary.add(b.toString());
					b.setLength(0);
				}
			} else {
				b.append(nextCh);
			}
		}
		if (b.length() > 0) {
			dictionary.add(b.toString());
		}
		return LOGGER.traceExit(e, dictionary);
	}

	public static void displayOptions(PrintWriter printWriter) {
		Ciphering.displayOptions(printWriter, dictionaryOptions);
	}

	private static int editLen1 = 0;

	private static int editLen2 = 0;

	private static int[][] edits = null;

	private static int distance(String s1, String s2) {
		int n1 = s1.length() + 1, n2 = s2.length() + 1;
		if ((edits == null) || (n1 > editLen1) || (n2 > editLen2)) {
			editLen1 = n1;
			editLen2 = n2;
			edits = new int[editLen1][editLen2];
		}
		for (int i = 0; i < n1; i++) {
			edits[i][0] = i;
		}
		for (int j = 1; j < n2; j++) {
			edits[0][j] = j;
		}
		for (int i = 1; i < n1; i++) {
			for (int j = 1; j < n2; j++) {
				int u = (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
				edits[i][j] = Math.min(edits[i - 1][j] + 1, Math.min(edits[i][j - 1] + 1, edits[i - 1][j - 1] + u));
			}
		}
		return edits[n1 - 1][n2 - 1];
	}

	public static int calcScore(String word, int len) {
		Set<String> subDictionary = lenDictionary.get(len);
		if (subDictionary == null) {
			subDictionary = new HashSet<>();
			for (String w : dictionary) {
				if (w.length() == len) { subDictionary.add(w); }
			}
			lenDictionary.put(len, subDictionary);
		}
		if (subDictionary.contains(word)) { return 0; }
		int minScore = -1;
		for (String w : subDictionary) {
			int s = distance(word, w);
			if ((minScore == -1) || (s < minScore)) {
				minScore = s;
				if (minScore == 1) { break; }
			}
		}
		return minScore;
	}

	public static int calcScore(String word) {
		int n = word.length();
		if (n > 2) {
			int s = calcScore(word, n);
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n - 1));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 1));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n - 2));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 2));
			return s;
		} else if (n == 2) {
			int s = calcScore(word, n);
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n - 1));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 1));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 2));
			return s;
		} else {
			int s = calcScore(word, n);
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 1));
			if (s == 0) { return 0; }
			s = Math.min(s, calcScore(word, n + 2));
			return s;
		}
	}
	
	public static int score(Set<String> lexicon) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.scoreLexicon(lexicon={})", () -> Ciphering.argStr(lexicon));
		int score = 0;
		for (String word : lexicon) {
			int n = word.length();
			Set<String> subDictionary = lenDictionary.get(n);
			if (subDictionary == null) {
				subDictionary = new HashSet<>();
				for (String w : dictionary) {
					if (w.length() == n) { subDictionary.add(w); }
				}
				lenDictionary.put(n, subDictionary);
			}
			score += calcScore(word);
		}
		return LOGGER.traceExit(e, score);
	}

	public static int scoreWords(String text) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.scoreWords(text={})", () -> Ciphering.trunc(text));
		int score = 0;
		StringBuilder b = new StringBuilder();
		int n = text.length();
		for (int i = 0; i < n; i++) {
			char nextCh = Ciphering.translate(text.charAt(i));
			if ((nextCh < 'A') || (nextCh > 'Z')) {
				if (b.length() > 0) {
					score += calcScore(b.toString());
					b.setLength(0);
				}
			} else {
				b.append(nextCh);
			}
		}
		if (b.length() > 0) {
			score += calcScore(b.toString());
		}
		return LOGGER.traceExit(e, score);
	}

	public static long scoreMonograms(String text) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.scoreMonograms(text={})", () -> Ciphering.trunc(text));
		long[] ngrams = Corpus.calculateNgram1(text);
		Corpus.normalizeNgram(ngrams);
		return LOGGER.traceExit(e, Corpus.dotProduct(ngrams));
	}
	
	public static long scoreDigrams(String text) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.scoreDigrams(text={})", () -> Ciphering.trunc(text));
		long[][] ngrams = Corpus.calculateNgram2(text);
		Corpus.normalizeNgram(ngrams);
		return LOGGER.traceExit(e, Corpus.dotProduct(ngrams));
	}
	
	public static long scoreTrigrams(String text) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.scoreTrigrams(text={})", () -> Ciphering.trunc(text));
		long[][][] ngrams = Corpus.calculateNgram3(text);
		Corpus.normalizeNgram(ngrams);
		return LOGGER.traceExit(e, Corpus.dotProduct(ngrams));
	}
	
	public static long score(String text, int method) {
		EntryMessage e = LOGGER.traceEntry("Dictionary.score(text={}, method={})", Ciphering.trunc(text), method);
		if (method == SCORE_WORDS) {
			return LOGGER.traceExit(e, scoreWords(text));
		} else if (method == SCORE_MONOGRAMS) {
			return LOGGER.traceExit(e, scoreMonograms(text));
		} else if (method == SCORE_DIGRAMS) {
			return LOGGER.traceExit(e, scoreDigrams(text));
		} else if (method == SCORE_TRIGRAMS) {
			return LOGGER.traceExit(e, scoreTrigrams(text));
		} else {
			return LOGGER.traceExit(e, Integer.MAX_VALUE);
		}
	}

	public static void dump() {
		for (String word : dictionary) {
			System.out.println(word);
		}
	}

}
