package za.ac.sun.cs.ciphering.commands.encipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;

public class RailFence extends EncipherCommand {

	public static final int MIN_RAILS = 2;

	public static final int MAX_RAILS = 20;
	
	private static final RailFence instance = new RailFence();

	public static RailFence getInstance() {
		return instance;
	}

	private RailFence() {
		super();
		Ciphering.addOption(commandOptions, "r/rails", "integer", "number of rails");
	}

	@Override
	public String getName() {
		return "railfence";
	}

	@Override
	public String getDescription() {
		return "Encipher text with rail fence";
	}

	@Override
	public String getExplanation() {
		return "https://en.wikipedia.org/wiki/Rail_fence_cipher\n\n" +
		"The rail fence cipher (also called a zigzag cipher) is a form of transposition " +
		"cipher. It derives its name from the way in which it is encoded. In " +
		"the rail fence cipher, the plain text is written downwards and diagonally " +
		"on successive \"rails\" of an imaginary fence, then moving up when we " +
		"reach the bottom rail. When we reach the top rail, the message is written " +
		"downwards again until the whole plaintext is written out.";
	}
	
	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("RailFence.handle1(line={})", line);
		int rails = -1;
		if (line.hasOption('r')) {
			rails = Integer.parseInt(line.getOptionValue('r'));
		}
		if (rails == -1) {
			rails = Ciphering.randomInt(MIN_RAILS, MAX_RAILS);
			LOGGER.trace("picking new rails={}", rails);
		}
		cipherText = translate(plainText, rails);
		LOGGER.traceExit(e);
	}

	public String translate(String text, int rails) {
		EntryMessage e = LOGGER.traceEntry("RailFence.translate(text={}, rails={})", Ciphering.trunc(text), rails);
		String text0 = Ciphering.filter(text);
		StringBuilder b = new StringBuilder();
		int period = 2 * (rails - 1);
		int period2 = period / 2;
		int n = text0.length();
		for (int i = 0; i < n; i += period) {
			b.append(text0.charAt(i));
		}
		int m = period;
		for (int i = 1; i < period2; i++) {
			for (int j = i, k = --m; j < n; j += period, k += period) {
				b.append(text0.charAt(j));
				if (k < n) {
					b.append(text0.charAt(k));
				}
			}
		}
		for (int i = period2; i < n; i += period) {
			b.append(text0.charAt(i));
		}
		return LOGGER.traceExit(e, b.toString());
	}

}
