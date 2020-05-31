package net.dogesoulseller.cuesplit;

import java.util.ArrayList;

public class CLIArguments
{
	public String ffmpegExecutable;
	public String inputFile;
	public String outputDir;
	public boolean forceLossless = false;
	public AudioFormatInfo formatInfo;
	public boolean noOverwrite = false;

	private static String helpMessage = String.join("\n", "Usage:",
	"[java run commands] options... [input file]",
	"Arguments:",
	"-b | --bitrate - output bitrate in kbps (only valid for lossy encodings)",
	"-e | --ffmpeg-executable - custom ffmpeg executable location",
	"-f | --format - output format [flac, wav, tta, alac, opus, vorbis, mp3, aac] (default: flac)",
	"-h | --help - view help message",
	"-i | --input - input file as an alternative to the [input file] specifier",
	"-l | --lossless - force lossless output (ignores format specified and outputs FLAC if a lossy format was specified)",
	"-o | --output - output directory; if not specified, defaults to working directory",
	"-q | --quality - output quality 0-10, where 0 is the worst, 10 is the best. For lossy encodings, specifies the audio quality, for lossless, the compression quality",
	"--no-overwrite - don't overwrite existing files");

	/**
	 * Usage:
	 * [java run commands] options... [input file]
	 * Arguments:
	 * -b | --bitrate - output bitrate in kbps (only valid for lossy encodings)
	 * -e | --ffmpeg-executable - custom ffmpeg executable location
	 * -f | --format - output format [flac, wav, tta, alac, opus, vorbis, mp3, aac] (default: flac)
	 * -h | --help - view help message
	 * -i | --input - input file as an alternative to the [input file] specifier
	 * -l | --lossless - force lossless output (ignores format specified and outputs FLAC if a lossy format was specified)
	 * -o | --output - output directory; if not specified, defaults to working directory
	 * -q | --quality - output quality 0-10, where 0 is the worst, 10 is the best. For lossy encodings, specifies the audio quality, for lossless, the compression quality
	 * --no-overwrite - don't overwrite existing files
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
			formatInfo = new AudioFormatInfo();

			for (int i = 0; i < args.size(); i++)
			{
				var currentArg = args.get(i);

				lastArg = i;

				// -b || --bitrate
				if (currentArg.compareTo("-b") == 0 || currentArg.compareTo("--bitrate") == 0)
				{
					formatInfo.kbps = Integer.parseInt(args.get(i+1));
				}

				// -e || --ffmpeg-executable
				if (currentArg.compareTo("-e") == 0 || currentArg.compareTo("--ffmpeg-executable") == 0)
				{
					ffmpegExecutable = args.get(i+1);
				}

				// -f | --format
				if (currentArg.compareTo("-f") == 0 || currentArg.compareTo("--format") == 0)
				{
					String formatStr = args.get(i+1);

					if (formatStr.matches("(?i)flac"))
					{
						formatInfo.format = AudioFormat.FLAC;
					}
					else if (formatStr.matches("(?i)wav"))
					{
						formatInfo.format = AudioFormat.WAV;
					}
					else if (formatStr.matches("(?i)tta"))
					{
						formatInfo.format = AudioFormat.TTA;
					}
					else if (formatStr.matches("(?i)alac"))
					{
						formatInfo.format = AudioFormat.ALAC;
					}
					else if (formatStr.matches("(?i)opus"))
					{
						formatInfo.format = AudioFormat.Opus;
					}
					else if (formatStr.matches("(?i)vorbis"))
					{
						formatInfo.format = AudioFormat.Vorbis;
					}
					else if (formatStr.matches("(?i)mp3"))
					{
						formatInfo.format = AudioFormat.MP3;
					}
					else if (formatStr.matches("(?i)aac"))
					{
						formatInfo.format = AudioFormat.AAC;
					}
					else
					{
						System.err.println("Unknown format " + formatStr);
						System.exit(1);
					}
				}

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

				// -q || --quality
				if (currentArg.compareTo("-q") == 0 || currentArg.compareTo("--quality") == 0)
				{
					formatInfo.quality = Integer.parseInt(args.get(i+1));
				}

				// --no-overwrite
				if (currentArg.compareTo("--no-overwrite") == 0)
				{
					noOverwrite = true;
				}

			}
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println("Failed to parse arguments - not enough arguments passed to " + args.get(lastArg));
			System.exit(1);
		}

		// Set ffmpeg executable path to default
		if (ffmpegExecutable == null)
		{
			boolean isWindows = System.getProperty("os.name").matches("Windows.*");
			ffmpegExecutable = isWindows ? "ffmpeg.exe" : "ffmpeg";
		}

		// Set format to FLAC if no format was set by user
		if (formatInfo.format == null)
		{
			formatInfo.format = AudioFormat.FLAC;
		}

		// If lossless encoding was forced and a lossy format was specified, default to FLAC
		if (forceLossless && (formatInfo.format == AudioFormat.Opus || formatInfo.format == AudioFormat.Vorbis ||
		  formatInfo.format == AudioFormat.MP3 || formatInfo.format == AudioFormat.AAC))
		{
			formatInfo.format = AudioFormat.FLAC;
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