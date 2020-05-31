package net.dogesoulseller.cuesplit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Cuesplit {
	public static void main(String[] args) {
		ArrayList<String> argsList = new ArrayList<>();
		Collections.addAll(argsList, args);

		CLIArguments cliArgs = new CLIArguments(argsList);

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
}