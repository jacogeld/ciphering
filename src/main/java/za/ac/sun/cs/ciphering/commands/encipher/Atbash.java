package za.ac.sun.cs.ciphering.commands.encipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;

public class Atbash extends EncipherCommand {

	private static final Atbash instance = new Atbash();

	public static Atbash getInstance() {
		return instance;
	}

	private Atbash() {
		super();
	}

	@Override
	public String getName() {
		return "atbash";
	}

	@Override
	public String getDescription() {
		return "Encipher text with atbash";
	}

	@Override
	public String getExplanation() {
		return "https://en.wikipedia.org/wiki/Atbash\n\n" +
		"Atbash is a monoalphabetic " +
		"substitution cipher originally used to encrypt the Hebrew alphabet. " +
		"It can be modified for use with any known writing system with a standard " +
		"collating order. The Atbash cipher is a particular type of monoalphabetic " +
		"cipher formed by taking the alphabet (or abjad, syllabary, etc.) and " +
		"mapping it to its reverse, so that the first letter becomes the last " +
		"letter, the second letter becomes the second to last letter, and so on.";
	}
	
	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Atbash.handle1(line={})", line);
		int n = Ciphering.filterSize();
		cipherText = Affine.getInstance().translate(plainText, n - 1, n - 1);
		LOGGER.traceExit(e);
	}

}
