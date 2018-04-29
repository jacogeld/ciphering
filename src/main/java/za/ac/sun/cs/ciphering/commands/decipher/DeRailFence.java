package za.ac.sun.cs.ciphering.commands.decipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Dictionary;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.encipher.RailFence;

public class DeRailFence extends DecipherCommand {

	private static final DeRailFence instance = new DeRailFence();

	public static DeRailFence getInstance() {
		return instance;
	}

	private DeRailFence() {
		super();
	}

	@Override
	public String getName() {
		return "derailfence";
	}

	@Override
	public String getDescription() {
		return "Decipher text with rail fence";
	}

	@Override
	public String getExplanation() {
		return DeAffine.getInstance().getExplanation();
	}

	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("DeRailFence.handle1(line={})", line);
		plainText = untranslate(cipherText);
		LOGGER.traceExit(e);
	}
	
	public String untranslate(String text) {
		EntryMessage e = LOGGER.traceEntry("DeRailFence.untranslate(text={})", () -> Ciphering.trunc(text));
		long bestScore = -1;
		String untranslated = null;
		for (int r = RailFence.MIN_RAILS; r <= RailFence.MAX_RAILS; r++) {
			String candidate = untranslate(text, r);
			long score = Dictionary.score(candidate, scoreMethod);
			if ((bestScore == -1) || (score < bestScore)) {
				bestScore = score;
				untranslated = candidate;
			}
		}
		LOGGER.trace("bestScore={}", bestScore);
		return LOGGER.traceExit(e, untranslated);
	}

	private String untranslate(String text, int rails) {
		EntryMessage e = LOGGER.traceEntry("DeRailFence.untranslate(text={}, rails={})", Ciphering.trunc(text), rails);
		int n = text.length();
		char[] untranslated = new char[n];
		int index = 0;
		int period = 2 * (rails - 1);
		int period2 = period / 2;
		for (int i = 0; i < n; i += period) {
			untranslated[i] = text.charAt(index++);
		}
		int m = period;
		for (int i = 1; i < period2; i++) {
			for (int j = i, k = --m; j < n; j += period, k += period) {
				untranslated[j] = text.charAt(index++);
				if (k < n) {
					untranslated[k] = text.charAt(index++);
				}
			}
		}
		for (int i = period2; i < n; i += period) {
			untranslated[i] = text.charAt(index++);
		}
		return LOGGER.traceExit(e, new String(untranslated));
	}

}
