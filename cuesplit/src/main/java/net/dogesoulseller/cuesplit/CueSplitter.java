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

			// Assemble filename
			StringBuilder filenameBuffer = new StringBuilder();
			Formatter fmt = new Formatter(filenameBuffer);

			fmt.format("%s/%02d. %s.%s",
				args.outputDir,
				track.trackInfo.index,
				track.trackInfo.title != null ? track.trackInfo.title : "",
				args.formatInfo.getFormatExtension());

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

			// If not forcing lossless, use specified options or defaults
			if (!args.forceLossless)
			{
				// Best to let FFmpeg autodetect wave
				if (args.formatInfo.format != AudioFormat.WAV)
				{
					currentFfmpegArgs.add("-c:a");
					switch (args.formatInfo.format)
					{
						default:
						case FLAC:
							currentFfmpegArgs.add("flac");
							break;
						case ALAC:
							currentFfmpegArgs.add("alac");
							break;
						case TTA:
							currentFfmpegArgs.add("tta");
							break;
						case AAC:
							currentFfmpegArgs.add("libfdk_aac");
							break;
						case MP3:
							currentFfmpegArgs.add("libmp3lame");
							break;
						case Vorbis:
							currentFfmpegArgs.add("libvorbis");
							break;
						case Opus:
							currentFfmpegArgs.add("libopus");
							break;
					}

					if (args.formatInfo.quality != null) // Use user quality settings
					{
						switch (args.formatInfo.format)
						{
							case FLAC:
								currentFfmpegArgs.add("-compression_level");
								currentFfmpegArgs.add((args.formatInfo.quality == null
									|| args.formatInfo.quality >= 10) ? "12" : args.formatInfo.quality.toString());
								break;
							case AAC:
								currentFfmpegArgs.add("-vbr");
								currentFfmpegArgs.add(Integer.toString(args.formatInfo.quality / 2 > 5 ? 5 : args.formatInfo.quality / 2));
								break;
							case MP3:
								currentFfmpegArgs.add("-q:a");
								Integer mp3Q = args.formatInfo.quality > 10 ? 0 : args.formatInfo.quality % 10;
								mp3Q = mp3Q == 10 ? 9 : 10;
								currentFfmpegArgs.add(mp3Q.toString());
								break;
							case Vorbis:
								currentFfmpegArgs.add("-q:a");
								currentFfmpegArgs.add(args.formatInfo.quality > 10 ? "10" : args.formatInfo.quality.toString());
								break;
							case Opus:
								currentFfmpegArgs.add("-b:a");
								currentFfmpegArgs.add(args.formatInfo.getOpusQuality());
								break;
							default:
								break;
						}
					}
					else if (args.formatInfo.kbps != null) // Use bitrate with clamping
					{
						Integer clampedBitrate = 0;
						switch (args.formatInfo.format)
						{
							case AAC:
								currentFfmpegArgs.add("-b:a");
								clampedBitrate = args.formatInfo.kbps < 1 ? 1 : args.formatInfo.kbps;
								currentFfmpegArgs.add(clampedBitrate.toString() + "k");
								break;
							case MP3:
								currentFfmpegArgs.add("-b:a");
								clampedBitrate = args.formatInfo.kbps < 1 ? 1 : args.formatInfo.kbps;
								clampedBitrate = args.formatInfo.kbps > 320 ? 320 : args.formatInfo.kbps;
								currentFfmpegArgs.add(clampedBitrate.toString() + "k");
								break;
							case Vorbis:
								currentFfmpegArgs.add("-b:a");
								clampedBitrate = args.formatInfo.kbps < 47 ? 47 : args.formatInfo.kbps;
								clampedBitrate = args.formatInfo.kbps > 500 ? 500 : args.formatInfo.kbps;
								currentFfmpegArgs.add(clampedBitrate.toString() + "k");
								break;
							case Opus:
								currentFfmpegArgs.add("-b:a");
								clampedBitrate = args.formatInfo.kbps < 6 ? 6 : args.formatInfo.kbps;
								clampedBitrate = args.formatInfo.kbps > 510 ? 510 : args.formatInfo.kbps;
								currentFfmpegArgs.add(clampedBitrate.toString() + "k");
								break;
							default:
								break;
						}
					}
					else // Use sane defaults
					{
						switch (args.formatInfo.format)
						{
							case FLAC:
								currentFfmpegArgs.add("-compression_level");
								currentFfmpegArgs.add("10");
								break;
							case AAC:
								currentFfmpegArgs.add("-b:a");
								currentFfmpegArgs.add("256k");
								break;
							case MP3:
								currentFfmpegArgs.add("-q:a");
								currentFfmpegArgs.add("0");
								break;
							case Vorbis:
								currentFfmpegArgs.add("-q:a");
								currentFfmpegArgs.add("6");
								break;
							case Opus:
								currentFfmpegArgs.add("-b:a");
								currentFfmpegArgs.add("192k");
								break;
							default:
								break;
						}
					}
				}
			}
			else // Else force lossless
			{
				// Best to let FFmpeg autodetect
				if (args.formatInfo.format != AudioFormat.WAV)
				{
					currentFfmpegArgs.add("-c:a");
					switch (args.formatInfo.format)
					{
						default:
						case FLAC:
							currentFfmpegArgs.add("flac");
							break;
						case ALAC:
							currentFfmpegArgs.add("alac");
							break;
						case TTA:
							currentFfmpegArgs.add("tta");
							break;
					}
					currentFfmpegArgs.add("-compression_level");
					currentFfmpegArgs.add((args.formatInfo.quality == null || args.formatInfo.quality >= 10) ? "12" : args.formatInfo.quality.toString());
				}
			}

			// Don't overwrite existing files if specified
			if (args.noOverwrite)
			{
				currentFfmpegArgs.add("-n");
			}
			else
			{
				currentFfmpegArgs.add("-y");
			}

			// Finally append path and push to main array
			currentFfmpegArgs.add(outputFilePath);
			track.ffmpegArguments.addAll(currentFfmpegArgs);
		}
	}
}