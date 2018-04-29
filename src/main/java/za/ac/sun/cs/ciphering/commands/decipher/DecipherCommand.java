package za.ac.sun.cs.ciphering.commands.decipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Dictionary;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.Command;

public abstract class DecipherCommand extends Command {

	protected String cipherText = null;
	
	protected String plainText = null;
	
	protected int scoreMethod = Dictionary.SCORE_WORDS;
	
	protected DecipherCommand() {
		super();
		Ciphering.addOption(commandOptions, "t/text", "string", "set the ciphertext");
		Ciphering.addOption(commandOptions, "sw/score-words", null, "score using words");
		Ciphering.addOption(commandOptions, "s1/score-1", null, "score using 1-grams");
		Ciphering.addOption(commandOptions, "s2/score-2", null, "score using 2-grams");
		Ciphering.addOption(commandOptions, "s3/score-3", null, "score using 3-grams");
	}

	@Override
	protected final void handle0(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("DecipherCommand.handle0(line={})", line);
		
		if (line.hasOption('t')) {
			cipherText = line.getOptionValue('t');
		}
		if (cipherText == null) {
			System.out.println("No ciphertext specified");
			throw new ErrorException();
		}

		int sw = line.hasOption("sw") ? 1 : 0;
		int s1 = line.hasOption("s1") ? 1 : 0;
		int s2 = line.hasOption("s2") ? 1 : 0;
		int s3 = line.hasOption("s3") ? 1 : 0;
		if (sw + s1 + s2 + s3 > 1) {
			System.out.println("At most one scoring method allowed");
			throw new ErrorException();
		}
		if (sw == 1) {
			scoreMethod = Dictionary.SCORE_WORDS;
		} else if (s1 == 1) {
			scoreMethod = Dictionary.SCORE_MONOGRAMS;
		} else if (s2 == 1) {
			scoreMethod = Dictionary.SCORE_DIGRAMS;
		} else if (s3 == 1) {
			scoreMethod = Dictionary.SCORE_TRIGRAMS;
		}

		handle1(line);

		System.out.println("--- cipher text ---");
		System.out.println();
		System.out.println(cipherText);
		System.out.println();
		System.out.println("--- plain text ---");
		System.out.println();
		System.out.println(plainText);
		System.out.println();
		LOGGER.traceExit(e);
	}

	protected abstract void handle1(CommandLine line) throws ErrorException;

}
