package za.ac.sun.cs.ciphering.commands.encipher;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;

public class Affine extends EncipherCommand {

	private static final Affine instance = new Affine();

	public static Affine getInstance() {
		return instance;
	}

	private Affine() {
		super();
		Ciphering.addOption(commandOptions, "a", "integer", "set the affine multiplier");
		Ciphering.addOption(commandOptions, "b", "integer", "set the affine offset");
	}

	@Override
	public String getName() {
		return "affine";
	}

	@Override
	public String getDescription() {
		return "Encipher text with affine";
	}

	@Override
	public String getExplanation() {
		return "https://en.wikipedia.org/wiki/Affine_cipher\n\n" +
		"The affine cipher is a type of monoalphabetic substitution cipher, " +
		"wherein each letter in an alphabet is mapped to its numeric equivalent, " +
		"encrypted using a simple mathematical function, and converted back " +
		"to a letter. The formula used means that each letter encrypts to one " +
		"other letter, and back again, meaning the cipher is essentially a standard " +
		"substitution cipher with a rule governing which letter goes to which. " +
		"As such, it has the weaknesses of all substitution ciphers. Each letter " +
		"is enciphered with the function (ax + b) mod 26, where b is the magnitude " +
		"of the shift.";
	}
	
	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("Affine.handle1(line={})", line);
		int affineA = -1, affineB = -1;
		int n = Ciphering.filterSize();

		// Affine multiplier option
		if (line.hasOption('a')) {
			affineA = Integer.parseInt(line.getOptionValue('a'));
		}
		if (affineA == -1) {
			do {
				affineA = Ciphering.randomInt(n - 1);
			} while (Ciphering.gcd(n, affineA) > 1);
			LOGGER.trace("picking new affineA={}", affineA);
		}
		if (Ciphering.gcd(n, affineA) != 1) {
			System.out.println("Affine multiplier (=" + affineA + ") is not co-prime with " + n);
			throw new ErrorException();
		}

		// Affine multiplier offset
		if (line.hasOption('b')) {
			affineB = Integer.parseInt(line.getOptionValue('b'));
		}
		if (affineB == -1) {
			affineB = Ciphering.randomInt(n - 1);
			LOGGER.trace("picking new affineB={}", affineB);
		}

		// Create the ciphertext
		cipherText = translate(plainText, affineA, affineB);
		
		LOGGER.traceExit(e);
	}

	public String translate(String text, int A, int B) {
		EntryMessage e = LOGGER.traceEntry("Affine.translate(text={}, A={}, B={})", Ciphering.trunc(text), A, B);
		int n = Ciphering.filterSize();
		StringBuilder b = new StringBuilder();
		int m = text.length();
		for (int i = 0; i < m; i++) {
			char ch = text.charAt(i);
			int tr = Ciphering.filter(ch);
			if (tr == -1) {
				b.append(ch);
			} else {
				b.append(Ciphering.unfilter((A * tr + B) % n));
			}
		}
		return LOGGER.traceExit(e, b.toString());
	}

	public Set<String> translate(Set<String> lexicon, int A, int B) {
		EntryMessage e = LOGGER.traceEntry("Affine.translate(lexicon={}, A={}, B={})", lexicon, A, B);
		Set<String> translatedLexicon = new HashSet<>();
		for (String word : lexicon) {
			translatedLexicon.add(translate(word, A, B));
		}
		return LOGGER.traceExit(e, translatedLexicon);
	}

}
