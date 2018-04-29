package za.ac.sun.cs.ciphering.commands.decipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Dictionary;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.encipher.Columnar;

public class DeColumnar extends DecipherCommand {

	private static final DeColumnar instance = new DeColumnar();

	public static DeColumnar getInstance() {
		return instance;
	}

	private DeColumnar() {
		super();
	}

	@Override
	public String getName() {
		return "decolumnar";
	}

	@Override
	public String getDescription() {
		return "Decipher text with columnar transposition";
	}

	@Override
	public String getExplanation() {
		return Columnar.getInstance().getExplanation();
	}
	
	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("DeColumnar.handle1(line={})", line);
		plainText = untranslate(cipherText);
		LOGGER.traceExit(e);
	}

	private String untranslate(String text) {
		EntryMessage e = LOGGER.traceEntry("DeColumnar.untranslate(text={})", () -> Ciphering.trunc(text));
		long bestScore = -1;
		String untranslated = null;
		for (int c = Columnar.MIN_COLUMNS; c <= Columnar.MAX_COLUMNS; c++) {
			String candidate = untranslate(text, c);
			if (candidate != null) {
				long score = Dictionary.score(candidate, scoreMethod);
				if ((bestScore == -1) || (score < bestScore)) {
					bestScore = score;
					untranslated = candidate;
				}
			}
		}
		LOGGER.trace("bestScore={}", bestScore);
		return LOGGER.traceExit(e, untranslated);
	}

	private String untranslate(String text, int columns) {
		EntryMessage e = LOGGER.traceEntry("DeColumnar.untranslate(text={}, columns={})", Ciphering.trunc(text), columns);
		int rows = (text.length() + columns - 1) / columns;
		int missing = rows * columns - text.length();
		if (missing != 0) {
			return LOGGER.traceExit(e, null);
		}
		char[][] matrix = new char[rows][columns];
		int i = 0;
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				matrix[r][c] = text.charAt(i++);
			}
		}
		StringBuilder b = new StringBuilder();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				b.append(matrix[r][c]);
			}
		}
		return LOGGER.traceExit(e, b.toString());
	}
	
}
