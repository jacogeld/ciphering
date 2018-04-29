package za.ac.sun.cs.ciphering.commands;

import org.apache.commons.cli.CommandLine;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Corpus;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.encipher.Affine;

public class Scytale extends Command {

	private static final Scytale instance = new Scytale();

	public static Scytale getInstance() {
		return instance;
	}

	private Scytale() {
		Ciphering.addOption(commandOptions, "i/index", "integer", "set the plaintext index");
		Ciphering.addOption(commandOptions, "t/text", "string", "set the plaintext");
	}

	@Override
	public String getName() {
		return "scytale";
	}

	@Override
	public String getDescription() {
		return "Encode text with scytale cipher";
	}

	@Override
	public String getExplanation() { return
		"https://en.wikipedia.org/wiki/Scytale\n\n" +
		"In cryptography, a scytale (rhymes approximately with Italy; also " +
		"transliterated skytale, from Greek \"baton\") is a tool used to " +
		"perform a transposition cipher, consisting of a cylinder with a strip " +
		"of parchment wound around it on which is written a message. The " +
		"ancient Greeks, and the Spartans in particular, are said to have used " +
		"this cipher to communicate during military campaigns.";
	}
	
	@Override
	public void handle0(CommandLine line) throws ErrorException {
		int plainTextIndex = -1;
		String plainText = null;

		if (line.hasOption('i')) {
			plainTextIndex = Integer.parseInt(line.getOptionValue('i'));
		}
		if (line.hasOption('t')) {
			plainText = line.getOptionValue('t');
		}
		if (plainText == null) {
			if (plainTextIndex == -1) {
				plainTextIndex = (int) (Math.random() * Corpus.getSize());
				LOGGER.trace("picking new plainTextIndex={}", plainTextIndex);
			}
			plainText = Corpus.get(plainTextIndex);
		} else if (plainTextIndex != -1) {
			System.out.println("Cannot specify both plaintext and plaintext index");
			throw new ErrorException();
		}
		int n = Ciphering.filterSize();
		String cipherText = Affine.getInstance().translate(plainText, n - 1, n - 1);
		System.out.println("--- plain text ---");
		System.out.println(plainText);
		System.out.println("--- cipher text ---");
		System.out.println(cipherText);
	}

}
