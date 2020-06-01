package net.dogesoulseller.cuesplit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Cuesplit
{
	public static void main(String[] args)
	{
		ArrayList<String> argsList = new ArrayList<>();
		Collections.addAll(argsList, args);

		CLIArguments cliArgs = new CLIArguments(argsList);

		checkSupportedCodecs(cliArgs);

		CueSplitter splitter = new CueSplitter(cliArgs);

		ArrayList<ProcessBuilder> processBuilders = new ArrayList<>();

		for (var track : splitter.tracks)
		{
			ArrayList<String> ffmpegStartArgs = new ArrayList<>();
			ffmpegStartArgs.add(cliArgs.ffmpegExecutable);
			ffmpegStartArgs.add("-v");
			ffmpegStartArgs.add("quiet");
			ffmpegStartArgs.add("-nostats");
			ffmpegStartArgs.addAll(track.ffmpegArguments);
			String[] ffmpegStartArgsArr = ffmpegStartArgs.toArray(new String[0]);

			processBuilders.add(new ProcessBuilder(ffmpegStartArgsArr));
		}

		// TODO: Get user preferences about thread count
		for (var procBuilder : processBuilders)
		{
			try
			{
				Process ffmpegProcess = procBuilder.start();

				ffmpegProcess.getInputStream();
				BufferedReader stdError = new BufferedReader(new InputStreamReader(ffmpegProcess.getErrorStream()));

				String line = null;

				while((line = stdError.readLine()) != null)
				{
					System.err.println(line);
				}
			}
			catch (IOException e)
			{
				System.err.println("Failed to start ffmpeg process: " + e.getMessage());
			}
		}
	}

	public static void checkSupportedCodecs(CLIArguments cliArgs)
	{
		ArrayList<String> audioEncoderLines = new ArrayList<>();

		// Check for presence of requested encoder
		try
		{
			String[] ffmpegInfoArgs = {cliArgs.ffmpegExecutable, "-v", "quiet", "-encoders"};
			Process ffmpegInfoProcess = Runtime.getRuntime().exec(ffmpegInfoArgs);
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(ffmpegInfoProcess.getInputStream()));

			// Get output from stdout (where ffmpeg posts its help output)
			String line;
			while ((line = stdOut.readLine()) != null)
			{
				String trimmedLine = line.trim();

				// Line contains audio encoder
				if (trimmedLine.startsWith("A."))
				{
					audioEncoderLines.add(trimmedLine.trim());
				}
			}
		}
		catch (IOException e)
		{
			System.err.println("Failed to start ffmpeg process");
		}

		StringBuilder encoderStringBuilder = new StringBuilder(64);

		encoderStringBuilder.append("Available codecs:");

		boolean hasOpus = false;
		boolean hasVorbis = false;
		boolean hasAAC = false;
		boolean hasFLAC = false;
		boolean hasALAC = false;
		boolean hasMP3 = false;
		boolean hasTTA = false;

		// Find and print supported formats
		for (String encLine : audioEncoderLines)
		{
			if (!hasOpus && encLine.contains("libopus"))
			{
				hasOpus = true;
				encoderStringBuilder.append(" opus");
			}

			if (!hasVorbis && encLine.contains("libvorbis"))
			{
				hasVorbis = true;
				encoderStringBuilder.append(" vorbis");
			}

			if (!hasAAC && encLine.contains("libfdk_aac"))
			{
				hasAAC = true;
				encoderStringBuilder.append(" aac");
			}

			if (!hasFLAC && encLine.contains("flac"))
			{
				hasFLAC = true;
				encoderStringBuilder.append(" flac");
			}

			if (!hasALAC && encLine.contains("alac"))
			{
				hasALAC = true;
				encoderStringBuilder.append(" alac");
			}

			if (!hasMP3 && encLine.contains("libmp3lame"))
			{
				hasMP3 = true;
				encoderStringBuilder.append(" mp3");
			}

			if (!hasTTA && encLine.contains("tta"))
			{
				hasTTA = true;
				encoderStringBuilder.append(" tta");
			}
		}
		encoderStringBuilder.append(" wav");
		System.err.println(encoderStringBuilder.toString());

		switch (cliArgs.formatInfo.format) {
			case AAC:
				if (!hasAAC)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case ALAC:
				if (!hasALAC)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case FLAC:
				if (!hasFLAC)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case MP3:
				if (!hasMP3)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case Opus:
				if (!hasOpus)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case TTA:
				if (!hasTTA)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			case Vorbis:
				if (!hasVorbis)
				{
					System.err.println("Trying to use unsupported codec");
					System.exit(3);
				}
				break;
			default:
				break;
		}
	}
}