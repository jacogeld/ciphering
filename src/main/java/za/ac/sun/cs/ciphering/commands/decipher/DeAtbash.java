package za.ac.sun.cs.ciphering.commands.decipher;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.encipher.Affine;
import za.ac.sun.cs.ciphering.commands.encipher.Atbash;

public class DeAtbash extends DecipherCommand {

	private static final DeAtbash instance = new DeAtbash();

	public static DeAtbash getInstance() {
		return instance;
	}

	private DeAtbash() {
		super();
	}

	@Override
	public String getName() {
		return "deatbash";
	}

	@Override
	public String getDescription() {
		return "Decipher text with atbash";
	}

	@Override
	public String getExplanation() {
		return Atbash.getInstance().getExplanation();
	}
	
	@Override
	protected final void handle1(CommandLine line) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("DeAtbash.handle1(line={})", line);
		int n = Ciphering.filterSize();
		plainText = Affine.getInstance().translate(cipherText, n - 1, n - 1);
		LOGGER.traceExit(e);
	}
	
}
