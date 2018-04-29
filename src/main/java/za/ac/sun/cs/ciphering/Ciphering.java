package za.ac.sun.cs.ciphering;

/*
 * TODO: algorithms to implement
 *   scytale bifid caesar rot13 dvorak adfgx adfgvx
 *   trifid nihilist vic two-square four-square
 *   playfair autokey chaocipher hill vigenere
 *   beaufort gronsfeld alberti enigma
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.commands.Command;
import za.ac.sun.cs.ciphering.commands.decipher.*;
import za.ac.sun.cs.ciphering.commands.encipher.*;
import za.ac.sun.cs.ciphering.commands.other.*;

public class Ciphering {

	private static final Logger LOGGER = LogManager.getLogger("CIPHERING");

	private static final int MODE_L = 0; // letters

	private static final int MODE_LD = 1; // letters + digits

	private static final int MODE_LP = 2; // letters + period

	private static final int MODE_LDP = 3; // letters + digits + period

	private static int alphabetMode = MODE_LDP;

	private static int filterMode = MODE_L;

	private static final Random rng = new Random();

	private static final Options globalOptions = new Options();

	static {
		addOption(globalOptions, "h/help", "topic", true, "explain usage (for the topic, if provided)");
		addOption(globalOptions, "a/alphabet", "mode", "set the alphabet mode\n0 = letters only\n1 = letters + digits\n2 = letters + periods\n3 = letters + digits + periods (default)");
		addOption(globalOptions, "f/filter", "mode", "set the filter mode (default is 0)");
		addOption(globalOptions, "rs/random-seed", "seed", "set the random seed");
	}

	private static final List<Command> commands = new ArrayList<>();

	public static void main(final String[] args) {
		LOGGER.trace("----------------------------------------------------------------------");
		EntryMessage e = LOGGER.traceEntry("Ciphering.main(args={})", () -> argStr(args));
		commands.add(Affine.getInstance());
		commands.add(DeAffine.getInstance());
		commands.add(Atbash.getInstance());
		commands.add(DeAtbash.getInstance());
		commands.add(Columnar.getInstance());
		commands.add(DeColumnar.getInstance());
		commands.add(RailFence.getInstance());
		commands.add(DeRailFence.getInstance());
		commands.add(Dict.getInstance());
		commands.add(Ngrams.getInstance());
		commands.add(Words.getInstance());
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(globalOptions, args, true);
			String[] rest = line.getArgs();

			// Check for the --help command-line option
			if (line.hasOption('h')) {
				LOGGER.trace("detected --help command-line option");
				displayHelp(line.getOptionValue('h'));
				throw new ErrorException();
			}
			
			// Load the random seed
			if (line.hasOption("rs")) {
				rng.setSeed(Long.parseLong(line.getOptionValue("rs")));
			}

			// Load the alphabet mode
			if (line.hasOption('a')) {
				String mode = line.getOptionValue('a').toUpperCase();
				if (mode.equals("0") || mode.equals("L")) {
					alphabetMode = MODE_L;
				} else if (mode.equals("1") || mode.equals("LD")) {
					alphabetMode = MODE_LD;
				} else if (mode.equals("2") || mode.equals("LP")) {
					alphabetMode = MODE_LP;
				} else if (mode.equals("3") || mode.equals("LDP")) {
					alphabetMode = MODE_LDP;
				} else {
					LOGGER.trace("undefined alphabetMode");
					System.out.println("Undefined alphabet mode: \"" + mode + "\"");
					System.out.println("Try \"--help\" option for details.");
					throw new ErrorException();
				}
			}
			LOGGER.trace("alphabetMode={}", alphabetMode);

			// Load the filter mode
			if (line.hasOption('f')) {
				String mode = line.getOptionValue('f').toUpperCase();
				if (mode.equals("0") || mode.equals("L")) {
					filterMode = MODE_L;
				} else if (mode.equals("1") || mode.equals("LD")) {
					filterMode = MODE_LD;
				} else if (mode.equals("2") || mode.equals("LP")) {
					filterMode = MODE_LP;
				} else if (mode.equals("3") || mode.equals("LDP")) {
					filterMode = MODE_LDP;
				} else {
					LOGGER.trace("undefined filterMode");
					System.out.println("Undefined filter mode: \"" + mode + "\"");
					System.out.println("Try \"--help\" option for details.");
					throw new ErrorException();
				}
			}
			LOGGER.trace("filterMode={}", filterMode);

			// Load the corpus
			rest = Corpus.load(rest);

			// Load the dictionary
			rest = Dictionary.load(rest);

			if (rest.length == 0) {
				System.out.println("Missing command");
				System.out.println("Try \"--help\" option for details.");
				throw new ErrorException();
			} else {
				boolean handled = false;
				for (Command command : commands) {
					if (command.handle(rest)) {
						handled = true;
						break;
					}
				}
				if (!handled) {
					System.out.println("Undefined command: \"" + rest[0] + "\"");
					System.out.println("Try \"--help\" option for details.");
					throw new ErrorException();
				}
			}
		} catch (ParseException x) {
			LOGGER.trace("PARSE_EXCEPTION", x);
			System.out.println(x.getMessage());
			System.out.println("Try \"--help\" option for details.");
		} catch (ErrorException x) {
			LOGGER.trace("ERROR_EXCEPTION", x);
		}
		LOGGER.traceExit(e);
	}

	/*----------------------------------------------------------------------
	 * OPEN FILES/RESOURCES
	 *--------------------------------------------------------------------*/

	public static InputStream openFile(String filename) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Ciphering.openFile(filename={})", filename);
		InputStream inputStream;
		try {
			File file = new File(filename);
			if (file.exists()) {
				inputStream = new FileInputStream(file);
			} else {
				inputStream = Ciphering.class.getResourceAsStream("/" + filename);
			}
			if (inputStream == null) {
				System.out.println("Cannot open file \"" + filename + "\"");
				throw new ErrorException();
			}
		} catch (IOException x) {
			LOGGER.trace("IO_EXCEPTION", x);
			System.out.println("Cannot open file \"" + filename + "\"");
			throw new ErrorException();
		}
		return LOGGER.traceExit(e, inputStream);
	}

	/*----------------------------------------------------------------------
	 * OPTION FORMATTING
	 *--------------------------------------------------------------------*/

	private static void displayHelp(String topic) throws ErrorException {
		PrintWriter pw = new PrintWriter(System.out);
		pw.println("Usage: ciphering [OPTION]... COMMAND [COMMAND-OPTION]...");

		if (topic == null) {
			pw.println("Implementation of various ciphers");
			pw.println("\nOptions:");
			displayOptions(pw, globalOptions);
			Corpus.displayOptions(pw);
			Dictionary.displayOptions(pw);

			pw.println("\nCommands:");
			for (Command command : commands) {
				displayOptionLine(pw, command.getName(), command.getDescription());
			}
		} else {
			boolean handled = false;
			for (Command command : commands) {
				if (command.getName().equals(topic)) {
					pw.print("Command " + command.getName());
					pw.println(": " + command.getDescription());
					command.displayOptions(pw);
					handled = true;
					break;
				}
			}
			if (!handled) {
				System.out.println("Unknown topic: \"" + topic + "\"");
				throw new ErrorException();
			}
		}

		pw.println();
		pw.close();
	}

	public static void displayOptions(PrintWriter printWriter, Options options) {
		StringBuilder b = new StringBuilder();
		for (Option option : options.getOptions()) {
			b.append("  -").append(option.getOpt());
			if (option.hasLongOpt()) {
				b.append(",--").append(option.getLongOpt());
			}
			if (option.hasOptionalArg()) {
				b.append(" [<").append(option.getArgName()).append(">]");
			} else if (option.hasArg()) {
				b.append(" <").append(option.getArgName()).append(">");
			}
			String[] descLines = option.getDescription().split("\n");
			for (String desc : descLines) {
				b.append(pad(27 - b.length()));
				b.append(desc);
				printWriter.println(b.toString());
				b.setLength(0);
			}
		}
	}

	public static void displayOptionLine(PrintWriter printWriter, String left, String right) {
		StringBuilder b = new StringBuilder();
		b.append("  ").append(left);
		String[] descLines = right.split("\n");
		for (String desc : descLines) {
			b.append(pad(27 - b.length()));
			b.append(desc);
			printWriter.println(b.toString());
			b.setLength(0);
		}
	}

	public static void wrapText(PrintWriter printWriter, String text, int margin, int lineWidth) {
		StringBuilder b = new StringBuilder();
		wrapText(b, text, margin, lineWidth);
		printWriter.print(b.toString());
	}

	public static void wrapText(StringBuilder stringBuilder, String text, int margin, int lineWidth) {
		StringBuilder marginBuilder = new StringBuilder();
		marginBuilder.append(pad(margin - marginBuilder.length()));
		String marginString = marginBuilder.toString();
		int width = lineWidth - margin;
		while (text.length() > width) {
			int i0 = text.indexOf('\n');
			if (i0 == -1)
				i0 = text.length();
			int i1 = text.lastIndexOf(' ', width);
			if (i1 == -1)
				i1 = text.length();
			int i2 = text.indexOf(' ', width);
			if (i2 == -1)
				i2 = text.length();
			int i = Math.min(i0, Math.min(i1, i2));
			stringBuilder.append(marginString).append(text.substring(0, i)).append('\n');
			text = text.substring(i + 1);
		}
		if (text.length() > 0) {
			stringBuilder.append(marginString).append(text).append('\n');
		}
	}

	public static String pad(char ch, int width) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < width; i++) {
			stringBuilder.append(ch);
		}
		return stringBuilder.toString();
	}

	public static String pad(int width) {
		return pad(' ', width);
	}
	
	public static void addOption(Options options, String name, String argName, String description) {
		addOption(options, name, argName, false, description);
	}

	public static void addOption(Options options, String name, String argName, boolean isOptional, String description) {
		Option option;
		if (name.indexOf('/') >= 0) {
			String[] names = name.split("/");
			option = new Option(names[0], names[1], argName != null, description);
		} else {
			option = new Option(name, argName != null, description);
		}
		if ((argName != null) && isOptional) {
			option.setOptionalArg(true);
		}
		if (argName != null) {
			option.setArgName(argName);
		}
		options.addOption(option);
	}

	/*----------------------------------------------------------------------
	 * ALPHABET FILTERING
	 *--------------------------------------------------------------------*/

	public static char translate(char ch) {
		char tr = Character.toUpperCase(ch);
		switch (alphabetMode) {
		case MODE_L:
			return isLetter(tr) ? tr : ' ';
		case MODE_LD:
			return isLetter(tr) ? tr : isDigit(tr) ? tr : ' ';
		case MODE_LP:
			return isLetter(tr) ? tr : (tr == '.') ? tr : ' ';
		case MODE_LDP:
			return isLetter(tr) ? tr : isDigit(tr) ? tr : (tr == '.') ? tr : ' ';
		}
		return ' ';
	}

	private static final String ALPHABET_L = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String ALPHABET_LD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String ALPHABET_LP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.";

	private static final String ALPHABET_LDP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.";

	public static int filter(char ch) {
		switch (filterMode) {
		case MODE_L:
			return ALPHABET_L.indexOf(Character.toUpperCase(ch));
		case MODE_LD:
			return ALPHABET_LD.indexOf(Character.toUpperCase(ch));
		case MODE_LP:
			return ALPHABET_LP.indexOf(Character.toUpperCase(ch));
		case MODE_LDP:
			return ALPHABET_LDP.indexOf(Character.toUpperCase(ch));
		}
		return -1;
	}

	public static String filter(String text) {
		EntryMessage e = LOGGER.traceEntry("Ciphering.filter(text={})", () -> Ciphering.trunc(text));
		StringBuilder b = new StringBuilder();
		int n = text.length();
		for (int i = 0; i < n; i++) {
			b.append(unfilter(filter(text.charAt(i))));
		}
		return LOGGER.traceExit(e, b.toString());
	}

	public static String filterHard(String text) {
		EntryMessage e = LOGGER.traceEntry("Ciphering.filter(text={})", () -> Ciphering.trunc(text));
		StringBuilder b = new StringBuilder();
		int n = text.length();
		for (int i = 0; i < n; i++) {
			int x = filter(text.charAt(i));
			if (x >= 0) {
				b.append(unfilter(x));
			}
		}
		return LOGGER.traceExit(e, b.toString());
	}
	
	public static char unfilter(int index) {
		if ((index < 0) || (index >= filterSize())) {
			return '_';
		}
		switch (filterMode) {
		case MODE_L:
			return ALPHABET_L.charAt(index);
		case MODE_LD:
			return ALPHABET_LD.charAt(index);
		case MODE_LP:
			return ALPHABET_LP.charAt(index);
		case MODE_LDP:
			return ALPHABET_LDP.charAt(index);
		}
		return '_';
	}

	public static int filterSize() {
		switch (filterMode) {
		case MODE_L:
			return ALPHABET_L.length();
		case MODE_LD:
			return ALPHABET_LD.length();
		case MODE_LP:
			return ALPHABET_LP.length();
		case MODE_LDP:
			return ALPHABET_LDP.length();
		}
		return 0;
	}

	private static boolean isLetter(char ch) {
		return (ch >= 'A') && (ch <= 'Z');
	}

	private static boolean isDigit(char ch) {
		return (ch >= '0') && (ch <= '9');
	}

	/*----------------------------------------------------------------------
	 * FORMATTING FUNCTION PARAMETERS
	 *--------------------------------------------------------------------*/

	public static String argStr(String[] args) {
		StringBuilder b = new StringBuilder();
		b.append('{').append(String.join(", ", args)).append('}');
		return trunc(b.toString());
	}

	public static String argStr(Collection<String> collection) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('{');
		boolean isFirst = true;
		for (String element : collection) {
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(element);
		}
		stringBuilder.append('}');
		return trunc(stringBuilder.toString());
	}

	public static String trunc(String text) {
		if (text.length() > 12) {
			return text.substring(0, 12) + "...";
		} else {
			return text;
		}
	}

	/*----------------------------------------------------------------------
	 * GCD
	 *--------------------------------------------------------------------*/

	public static int gcd(int a, int b) {
		while (b != 0) {
			int c = a % b;
			a = b;
			b = c;
		}
		return a;
	}

	/*----------------------------------------------------------------------
	 * RANDOM NUMBERS
	 *--------------------------------------------------------------------*/

	public static int randomInt(int min, int max) {
		return rng.nextInt(max - min + 1) + min;
	}

	public static int randomInt(int max) {
		return randomInt(0, max);
	}

	public static char randomLetter() {
		return unfilter(randomInt(0, filterSize()));
	}

}
