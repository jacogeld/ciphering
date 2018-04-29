package za.ac.sun.cs.ciphering.commands.decipher;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Dictionary;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.encipher.Affine;

public class DeAffine extends DecipherCommand {

	private static final DeAffine instance = new DeAffine();

	private final Affine affineInstance = Affine.getInstance();
	
	public static DeAffine getInstance() {
		return instance;
	}
	
	private DeAffine() {
		super();
	}

	@Override
	public String getName() {
		return "deaffine";
	}

	@Override
	public String getDescription() {
		return "Decipher text with affine";
	}

	@Override
	public String getExplanation() {
		return Affine.getInstance().getExplanation();
	}

	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("DeRailFence.handle1(line={})", line);
		plainText = untranslate(cipherText);
		LOGGER.traceExit(e);
	}
	
	public String untranslate(String text) {
		EntryMessage e = LOGGER.traceEntry("DeAffine.untranslate(text={})", () -> Ciphering.trunc(text));
		Set<String> lexicon = Dictionary.createDictionary(text);
		int bestA = -1, bestB = -1, bestScore = -1;
		int n = Ciphering.filterSize();
		for (int A = 1; A < n; A++) {
			if (Ciphering.gcd(n, A) > 1) { continue; }
			for (int B = 0; B < n; B++) {
				Set<String> translatedLexicon = affineInstance.translate(lexicon, A, B);
				int score = Dictionary.score(translatedLexicon);
				if ((bestScore == -1) || (score < bestScore)) {
					bestA = A;
					bestB = B;
					bestScore = score;
				}
			}
		}
		String untranslated = affineInstance.translate(text, bestA, bestB);
		return LOGGER.traceExit(e, untranslated);
	}

}
