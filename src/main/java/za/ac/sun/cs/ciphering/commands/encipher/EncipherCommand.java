package za.ac.sun.cs.ciphering.commands.encipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.Corpus;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.Command;

public abstract class EncipherCommand extends Command {

	public static final int SCRUB_NONE = 0;
	
	public static final int SCRUB_SOFT = 1;
	
	public static final int SCRUB_HARD = 2;
	
	protected int scrubbing = SCRUB_NONE;
	
	protected int plainTextIndex = -1;
	
	protected int plainTextMinLength = 0;
	
	protected String plainText = null;

	protected String cipherText = null;
	
	protected EncipherCommand() {
		super();
		Ciphering.addOption(commandOptions, "m/min", "integer", "set the minimum plaintext length");
		Ciphering.addOption(commandOptions, "i/index", "integer", "set the plaintext index");
		Ciphering.addOption(commandOptions, "t/text", "string", "set the plaintext");
		Ciphering.addOption(commandOptions, "ss/scrub-soft", null, "remove non-cipher letters from plaintext");
		Ciphering.addOption(commandOptions, "sh/scrub-hard", null, "also remove spaces");
	}

	@Override
	protected final void handle0(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("EncipherCommand.handle0(line={})", line);
		
		if (line.hasOption('m')) {
			plainTextMinLength = Integer.parseInt(line.getOptionValue('m'));
			LOGGER.trace("command-line: set plainTextMinLength={}", plainTextMinLength);
		}
		
		if (line.hasOption('i')) {
			plainTextIndex = Integer.parseInt(line.getOptionValue('i'));
			LOGGER.trace("command-line: set plainTextIndex={}", plainTextIndex);
		}

		if (line.hasOption('t')) {
			plainText = line.getOptionValue('t');
		}
		if (plainText == null) {
			if (plainTextIndex == -1) {
				do {
					plainTextIndex = Ciphering.randomInt(Corpus.getSize() - 1);
					plainText = Corpus.get(plainTextIndex);
				} while (plainText.length() < plainTextMinLength);
				LOGGER.trace("picking new plainTextIndex={}", plainTextIndex);
			} else {
				plainText = Corpus.get(plainTextIndex);
			}
		} else if (plainTextIndex != -1) {
			System.out.println("Cannot specify both plaintext and plaintext index");
			throw new ErrorException();
		}
		if (plainText.length() < plainTextMinLength) {
			System.out.println("Plaintext is too short");
			throw new ErrorException();
		}

		int ss = line.hasOption("ss") ? 1 : 0;
		int sh = line.hasOption("sh") ? 1 : 0;
		if (ss + sh > 1) {
			System.out.println("At most one scrubbing method allowed");
			throw new ErrorException();
		}
		if (ss == 1) {
			scrubbing = SCRUB_SOFT;
		} else if (sh == 1) {
			scrubbing = SCRUB_HARD;
		} else {
			scrubbing = SCRUB_NONE;
		}
			
		if (scrubbing == SCRUB_SOFT) {
			plainText = Ciphering.filter(plainText);
		} else if (scrubbing == SCRUB_HARD) {
			plainText = Ciphering.filterHard(plainText);
		}
		
		handle1(line);

		System.out.println("--- plain text ---");
		System.out.println();
		System.out.println(plainText);
		System.out.println();
		System.out.println("--- cipher text ---");
		System.out.println();
		System.out.println(cipherText);
		System.out.println();
		LOGGER.traceExit(e);
	}

	protected abstract void handle1(CommandLine line) throws ErrorException;

}
