package net.dogesoulseller.cuesplit;

import java.util.ArrayList;

public class CLIArguments
{
	public String inputFile;
	public String outputDir;
	public boolean forceLossless = false;

	private static String helpMessage = String.join("\n", "Usage:",
	"[java run commands] options... [input file]",
	"Arguments:",
	"-h | --help - view help essage",
	"-i | --input - input file as an alternative to the [input file] specifier",
	"-l | --lossless - force lossless output",
	"-o | --output - output directory; if not specified, defaults to working directory");

	/**
	 * Usage:
	 * [java run commands] options... [input file]
	 * Arguments:
	 * -h | --help - view help essage
	 * -i | --input - input file as an alternative to the [input file] specifier
	 * -l | --lossless - force lossless output
	 * -o | --output - output directory; if not specified, defaults to working directory
	 * @param args arguments taken directly from main function
	 */
	public CLIArguments(ArrayList<String> args)
	{
		int lastArg = 0;

		if (args.size() == 0)
		{
			System.err.println("No arguments passed");
			System.err.println(helpMessage);
			System.exit(1);
		}

		try
		{
			for (int i = 0; i < args.size(); i++)
			{
				var currentArg = args.get(i);

				lastArg = i;

				// -h | --help
				if (currentArg.compareTo("-h") == 0 || currentArg.compareTo("--help") == 0)
				{
					System.err.println(helpMessage);
					System.exit(1);
				}

				// -i | --input
				if (currentArg.compareTo("-i") == 0 || currentArg.compareTo("--input") == 0)
				{
					inputFile = args.get(i+1);
					i++;
					continue;
				}

				// -l | --lossless
				if (currentArg.compareTo("l") == 0 || currentArg.compareTo("--lossless") == 0)
				{
					forceLossless = true;
					continue;
				}

				// -o | --output
				if (currentArg.compareTo("-o") == 0 || currentArg.compareTo("--output") == 0)
				{
					outputDir = args.get(i+1);
					i++;
					continue;
				}

			}
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println("Failed to parse arguments - not enough arguments passed to " + args.get(lastArg));
			System.exit(1);
		}

		// Set input file to the last arg
		if (inputFile == null)
		{
			inputFile = args.get(args.size() - 1);
		}

		// Set output dir to user directory
		if (outputDir == null)
		{
			outputDir = System.getProperty("user.dir");
		}


	}
}