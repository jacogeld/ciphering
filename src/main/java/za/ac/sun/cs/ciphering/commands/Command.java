package za.ac.sun.cs.ciphering.commands;

import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

import za.ac.sun.cs.ciphering.Ciphering;
import za.ac.sun.cs.ciphering.ErrorException;

public abstract class Command {

	protected final Logger LOGGER = LogManager.getLogger("CIPHERING");

	protected final Options commandOptions = new Options();

	public abstract String getName();
	
	public abstract String getDescription();
	
	public String getExplanation() { return null; }
	
	public final boolean handle(final String[] args) throws ErrorException {
		EntryMessage e = LOGGER.traceEntry("{}.handle(args=...)", getClass().getName());
		if (!args[0].equals(getName())) {
			return LOGGER.traceExit(e, false);
		}
		String[] rest = Arrays.copyOfRange(args, 1, args.length);
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(commandOptions, rest, false);
			rest = line.getArgs();
			if (rest.length != 0) {
				System.out.println("Multiple commands not allowed: " + String.join(" ", rest));
				System.out.println("Try \"--help\" option for details.");
				throw new ErrorException();
			}
			handle0(line);
		} catch (ParseException x) {
			LOGGER.trace("PARSE_EXCEPTION", x);
			System.out.println(x.getMessage());
			System.out.println("Try \"--help\" option for details.");
			throw new ErrorException();
		}
		return LOGGER.traceExit(e, true);
	}

	protected abstract void handle0(CommandLine line) throws ErrorException;
	
	public void displayOptions(PrintWriter printWriter) {
		String explanation = getExplanation();
		if (explanation != null) {
			printWriter.println();
			Ciphering.wrapText(printWriter, explanation, 2, 70);
		}
		if (commandOptions.getOptions().size() > 0) {
			printWriter.println("\nOptions for \"" + getName() + "\":");
			Ciphering.displayOptions(printWriter, commandOptions);
		} else {
			printWriter.println("\nThis command does not accept any special options.");
		}
	}

}
