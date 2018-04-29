package za.ac.sun.cs.ciphering.commands;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Corpus;
import za.ac.sun.cs.ciphering.ErrorException;

public class Substitution extends Command {

	private static final Substitution instance = new Substitution();

	public static Substitution getInstance() {
		return instance;
	}

	private Substitution() {
		Ciphering.addOption(commandOptions, "k", "keyword", "set the keyword for the substitution");
		Ciphering.addOption(commandOptions, "i/index", "integer", "set the plaintext index");
		Ciphering.addOption(commandOptions, "t/text", "string", "set the plaintext");
	}

	@Override
	public String getName() {
		return "substitution";
	}

	@Override
	public String getDescription() {
		return "Encode text with a substitution cipher";
	}

	@Override
	public String getExplanation() { return
		"https://en.wikipedia.org/wiki/Affine_cipher\n\n" +
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
	public void handle0(CommandLine line) throws ErrorException {
		int affineA = -1, affineB = -1, plainTextIndex = -1;
		String plainText = null;
		int n = Ciphering.filterSize();

		if (line.hasOption('a')) {
			affineA = Integer.parseInt(line.getOptionValue('a'));
		}
		if (affineA == -1) {
			do {
				affineA = (int) (Math.random() * n);
			} while (Ciphering.gcd(n, affineA) > 1);
			LOGGER.trace("picking new affineA={}", affineA);
		}
		if (Ciphering.gcd(n, affineA) != 1) {
			System.out.println("Affine multiplier (=" + affineA + ") is not co-prime with " + n);
			throw new ErrorException();
		}

		if (line.hasOption('b')) {
			affineB = Integer.parseInt(line.getOptionValue('b'));
		}
		if (affineB == -1) {
			affineB = (int) (Math.random() * n);
			LOGGER.trace("picking new affineB={}", affineB);
		}

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
		String cipherText = translate(plainText, affineA, affineB);
		System.out.println("--- plain text ---");
		System.out.println(plainText);
		System.out.println("--- cipher text ---");
		System.out.println(cipherText);
	}

	public String translate(String text, int A, int B) {
		EntryMessage e = LOGGER.traceEntry("Affine.translate(text={}, A={}, B={})", text, A, B);
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
		Set<String> decodedLexicon = new HashSet<>();
		for (String word : lexicon) {
			decodedLexicon.add(translate(word, A, B));
		}
		return LOGGER.traceExit(e, decodedLexicon);
	}

}
