package za.ac.sun.cs.ciphering.commands.other;

import org.apache.commons.cli.CommandLine;

import za.ac.sun.cs.ciphering.Dictionary;
import za.ac.sun.cs.ciphering.ErrorException;
import za.ac.sun.cs.ciphering.commands.Command;

public class Dict extends Command {

	private static final Dict instance = new Dict();

	public static Dict getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "dict";
	}

	@Override
	public String getDescription() {
		return "Dump the dictionary";
	}

	@Override
	public void handle0(CommandLine line) throws ErrorException {
		Dictionary.dump();
	}

}
