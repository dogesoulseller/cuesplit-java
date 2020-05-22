package net.dogesoulseller.cuesplit;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;

import net.dogesoulseller.cuelib.Cuesheet;
import net.dogesoulseller.cuelib.Timespec;
import net.dogesoulseller.cuelib.Track;

/**
 * Class handling splitting a cuesheet with a file into multiple tracks
 */
public class CueSplitter
{
	public Cuesheet sheet;
	public ArrayList<SplitterTrack> tracks;

	private Timespec findTrackStartTimespec(Track track)
	{
		// Try to find index 01
		for (var idx : track.indices)
		{
			// First = track index
			if (idx.getFirst() == 1) // First index usually contains the audio
			{
				return idx.getSecond();
			}
		}

		// Failing that, find index 00
		for (var idx : track.indices)
		{
			// First = track index
			if (idx.getFirst() == 0)
			{
				return idx.getSecond();
			}
		}

		// If neither were found, return a zero timestamp
		return new Timespec(0, 0, 0);
	}

	public CueSplitter(CLIArguments args)
	{
		sheet = new Cuesheet(args.inputFile);
		tracks = new ArrayList<>();

		// Collect all tracks and find their start and end timespecs
		for (var file : sheet.filesInCuesheet)
		{
			for (int i = 0; i < file.tracksInFile.size(); i++)
			{
				Track currentTrack = file.tracksInFile.get(i);

				Timespec startTimespec = findTrackStartTimespec(currentTrack);
				Timespec endTimespec = null;

				// If the next track access would be in bounds, get the timespec from there
				if (i + 1 < file.tracksInFile.size())
				{
					var nextTrack = file.tracksInFile.get(i+1);
					endTimespec = findTrackStartTimespec(nextTrack);
				}

				this.tracks.add(new SplitterTrack(currentTrack, file, startTimespec, endTimespec));
			}
		}

		// Generate the commands for conversion
		for (var track : tracks)
		{
			track.ffmpegArguments = new ArrayList<>();
			ArrayList<String> currentFfmpegArgs = new ArrayList<>();

			// Get the cuesheet path and join it with the file name of the audio file
			String absoluteFilePath = Paths.get(Paths.get(args.inputFile).getParent().toString(), track.fileInfo.path).toString();
			String fileName = Paths.get(track.fileInfo.path).getFileName().toString();
			int extensionStart = fileName.lastIndexOf(".");

			// Assemble filename
			StringBuilder filenameBuffer = new StringBuilder();
			Formatter fmt = new Formatter(filenameBuffer);

			fmt.format("%02d. %s.%s",
				track.trackInfo.index,
				track.trackInfo.title != null ? track.trackInfo.title : "",
				args.forceLossless ? ".flac" : fileName.substring(extensionStart+1));

			String outputFilePath = filenameBuffer.toString();
			fmt.close();

			// If has a valid end time - i.e. not the last track, use the "-to" option for specifying end time
			if (track.endTime != null)
			{
				String[] currentFfmpegArgsTmp = {"-i", absoluteFilePath, "-ss", track.startTime.toFfmpegSpec(),
					"-to", track.endTime.toFfmpegSpec(), "-map", "0:a"};

				Collections.addAll(currentFfmpegArgs, currentFfmpegArgsTmp);
			}
			else // Else assume it's the last track
			{
				String[] currentFfmpegArgsTmp = {"-i", absoluteFilePath, "-ss", track.startTime.toFfmpegSpec(), "-map", "0:a"};
				Collections.addAll(currentFfmpegArgs, currentFfmpegArgsTmp);
			}

			// If not forcing lossless, copy format
			if (!args.forceLossless)
			{
				currentFfmpegArgs.add("-c:a copy");
			}
			else // Else force FLAC
			{
				currentFfmpegArgs.add("-c:a flac");
			}

			// Finally append path and push to main array
			currentFfmpegArgs.add(outputFilePath);
			track.ffmpegArguments.addAll(currentFfmpegArgs);
		}
	}
}