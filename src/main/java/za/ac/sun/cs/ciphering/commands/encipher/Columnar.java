package za.ac.sun.cs.ciphering.commands.encipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;

public class Columnar extends EncipherCommand {

	public static final int MIN_COLUMNS = 3;

	public static final int MAX_COLUMNS = 20;

	public static final int PAD_SPACE = 0;
	
	public static final int PAD_RANDOM = 1;
	
	private static final Columnar instance = new Columnar();

	public static Columnar getInstance() {
		return instance;
	}

	private Columnar() {
		super();
		Ciphering.addOption(commandOptions, "c/columns", "integer", "set number of columns");
		Ciphering.addOption(commandOptions, "rp/random-pad", null, "use random letters to pad");
	}

	@Override
	public String getName() {
		return "columnar";
	}

	@Override
	public String getDescription() {
		return "Encipher text with columnar transposition";
	}

	@Override
	public String getExplanation() {
		return "https://en.wikipedia.org/wiki/Transposition_cipher\n\n"
				+ "A transposition cipher is a method of encryption by which the "
				+ "positions held by units of plaintext (which are commonly characters "
				+ "or groups of characters) are shifted according to a regular system, "
				+ "so that the ciphertext constitutes a permutation of the plaintext. "
				+ "In a columnar transposition, the message is written out in rows of a "
				+ "fixed length, and then read out again column by column, and the "
				+ "columns are chosen in some scrambled order. Both the width of the "
				+ "rows and the permutation of the columns are usually defined by a " + "keyword.";
	}

	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Columnar.handle1(line={})", line);
		int columns = -1;
		int padding = PAD_SPACE;

		if (line.hasOption('c')) {
			columns = Integer.parseInt(line.getOptionValue('c'));
		}
		if (columns == -1) {
			columns = Ciphering.randomInt(MIN_COLUMNS, MAX_COLUMNS);
			LOGGER.trace("picking new columns={}", columns);
		}

		if (line.hasOption("rp")) {
			padding = PAD_RANDOM;
		}
		
		if (scrubbing == SCRUB_HARD) {
			padding = PAD_RANDOM;
		}
		
		cipherText = translate(plainText, columns, padding);
		LOGGER.traceExit(e);
	}

	public String translate(String text, int columns, int padding) {
		EntryMessage e = LOGGER.traceEntry("Columnar.translate(text={}, columns={}, padding={})", Ciphering.trunc(text), columns, padding);
		int rows = (text.length() + columns - 1) / columns;
		int missing = rows * columns - text.length();
		if (padding == PAD_SPACE) {
			text = text + Ciphering.pad('_', missing);
		} else if (padding == PAD_RANDOM) {
			for (int i = 0; i < missing; i++) {
				text = text + Ciphering.randomLetter();
			}
		}
		LOGGER.trace("text={}", text);
		char[][] matrix = new char[rows][columns];
		int i = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				matrix[r][c] = text.charAt(i++);
			}
		}
		StringBuilder b = new StringBuilder();
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				b.append(matrix[r][c]);
			}
		}
		return LOGGER.traceExit(e, b.toString());
	}

}
