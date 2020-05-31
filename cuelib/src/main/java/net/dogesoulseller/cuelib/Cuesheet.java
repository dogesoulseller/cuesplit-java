package net.dogesoulseller.cuelib;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.nio.charset.StandardCharsets;

/**
 * Class containing information about a cuesheet
 */
public class Cuesheet
{
	private CuesheetRegexes regexes;

	private ArrayList<Pair<LineType, String>> linesInFile;

	public ArrayList<net.dogesoulseller.cuelib.File> filesInCuesheet;

	public Cuesheet()
	{
		linesInFile = new ArrayList<Pair<LineType, String>>();
		filesInCuesheet = new ArrayList<net.dogesoulseller.cuelib.File>();
		regexes = new CuesheetRegexes();
	}

	public Cuesheet(String cueFilePath)
	{
		linesInFile = new ArrayList<Pair<LineType, String>>();
		filesInCuesheet = new ArrayList<net.dogesoulseller.cuelib.File>();
		regexes = new CuesheetRegexes();
		loadFile(cueFilePath);
	}

	private String trimDuplicateWhitespace(String line)
	{
		return regexes.duplicateWhitespace.matcher(line).replaceAll(" ");
	}

	private LineType getLineType(String line)
	{
		// CATALOG
		if (regexes.catalog.matcher(line).matches())
			return LineType.Catalog;

		// FILE
		if (regexes.file.matcher(line).matches())
			return LineType.File;

		// INDEX
		if (regexes.index.matcher(line).matches())
			return LineType.Index;

		// PERFORMER
		if (regexes.performer.matcher(line).matches())
			return LineType.Performer;

		// POSTGAP
		if (regexes.postgap.matcher(line).matches())
			return LineType.Postgap;

		// PREGAP
		if (regexes.pregap.matcher(line).matches())
			return LineType.Pregap;

		// REM
		if (regexes.remark.matcher(line).matches())
		{
			// REM DATE
			if (regexes.remarkDate.matcher(line).matches())
				return LineType.RemarkDate;

			// REM GENRE
			if (regexes.remarkGenre.matcher(line).matches())
				return LineType.RemarkGenre;

			// REM COMMENT
			if (regexes.remarkComment.matcher(line).matches())
				return LineType.RemarkComment;

			// REM COMPILATION
			if (regexes.remarkCompilation.matcher(line).matches())
				return LineType.RemarkCompilation;

			// REM COMPOSER
			if (regexes.remarkComposer.matcher(line).matches())
				return LineType.RemarkComposer;

			// REM DISCID
			if (regexes.remarkDiscID.matcher(line).matches())
				return LineType.RemarkDiscID;

			// REM DISCNUMBER
			if (regexes.remarkDiscNumber.matcher(line).matches())
				return LineType.RemarkDiscNumber;

			// REM REPLAYGAIN_ALBUM_GAIN
			if (regexes.remarkReplaygainAlbumGain.matcher(line).matches())
				return LineType.RemarkReplaygainAlbumGain;

			// REM REPLAYGAIN_ALBUM_PEAK
			if (regexes.remarkReplaygainAlbumPeak.matcher(line).matches())
				return LineType.RemarkReplaygainAlbumPeak;

			// REM TOTALDISCS
			if (regexes.remarkTotalDiscs.matcher(line).matches())
				return LineType.RemarkTotalDiscs;

			// Just REM
			return LineType.Remark;
		}

		// SONGWRITER
		if (regexes.songwriter.matcher(line).matches())
			return LineType.Songwriter;

		// TITLE
		if (regexes.title.matcher(line).matches())
			return LineType.Title;

		// TRACK .. AUDIO
		if (regexes.audioTrack.matcher(line).matches())
			return LineType.AudioTrack;

		// Type is unknown if not match was found
		return LineType.Unknown;
	}

	private FileType getFileType(String line)
	{
		// WAVE - all types other than MP3 and AIFF
		if (regexes.filetypeWave.matcher(line).matches())
			return FileType.Wave;

		// MP3
		if (regexes.filetypeMP3.matcher(line).matches())
			return FileType.MP3;

		// AIFF
		if (regexes.filetypeAIFF.matcher(line).matches())
			return FileType.AIFF;

		// Type is invalid - cuesheet does not match spec
		return FileType.Invalid;
	}

	private Timespec parseTimespec(String line, LineType type)
	{
		String trimmedLine = trimDuplicateWhitespace(line).trim();

		// Index field is xxx xx tt
		// Other time fields are xxx tt
		String timeFields = type == LineType.Index ? trimmedLine.split(" ")[2] : trimmedLine.split(" ")[1];

		// Time fields are mm:ss:ff so the split is at :
		String[] msfSpecs = timeFields.split(":");
		return new Timespec(Integer.parseInt(msfSpecs[0]), Integer.parseInt(msfSpecs[1]), Integer.parseInt(msfSpecs[2]));
	}

	private String parseFileStringContents(String line, FileType fType)
	{
		// Remove leading and trailing whitespace
		line = line.trim();

		// Remove file type specifier
		switch (fType)
		{
			case Wave:
			case AIFF:
				line = line.substring(0, line.length()-4);
				break;
			case MP3:
				line = line.substring(0, line.length()-3);
			default:
				break;
		}

		// Trim again
		line = line.trim();

		// If has quoted contents, then get whatever is between quotes
		int firstIdx = line.indexOf('"');
		if (firstIdx != -1)
		{
			int secondIdx = line.lastIndexOf('"');
			if (secondIdx == -1)
				return "INVALID";

			return line.substring(firstIdx+1, secondIdx);
		}
		else // Else, trim duplicate whitespace, split by spaces, and get the second field
		{
			return trimDuplicateWhitespace(line).split(" ")[1];
		}
	}

	private String parseStringContents(String line, LineType lType)
	{
		line = line.trim();

		// If has quoted contents, then get whatever is between quotes
		int firstIdx = line.indexOf('"');
		if (firstIdx != -1)
		{
			int secondIdx = line.lastIndexOf('"');
			if (secondIdx == -1)
				return "INVALID";

			return line.substring(firstIdx+1, secondIdx);
		}
		else // Else, trim duplicate whitespace and split by spaces
		{
			switch (lType) // If the line is a remark, pick third field
			{
				case RemarkComment:
				case RemarkCompilation:
				case RemarkComposer:
				case RemarkDate:
				case RemarkDiscID:
				case RemarkDiscNumber:
				case RemarkGenre:
				case RemarkReplaygainAlbumGain:
				case RemarkReplaygainAlbumPeak:
				case RemarkTotalDiscs:
				case Remark:
					return trimDuplicateWhitespace(line).split(" ")[2];

				default: // If it isn't a remark, pick second field
					return trimDuplicateWhitespace(line).split(" ")[1];
			}
		}
	}

	private void parseCuesheet(List<String> fileLines)
	{
		// Go through all lines, remove empty ones, and try to detect the line types
		for (String line : fileLines)
		{
			if (line.isBlank() || line.isEmpty())
				continue;

			LineType type = getLineType(line);
			this.linesInFile.add(Pair.makePair(type, line));
		}

		boolean inFile = false;
		File currentFile = new File();
		Track currentTrack = new Track();

		for (var lineInfo : linesInFile)
		{
			LineType type = lineInfo.getFirst();
			String line = lineInfo.getSecond();

			if (inFile)
			{
				switch (type)
				{
					case Postgap:
						currentTrack.postgap = parseTimespec(line, LineType.Postgap);
						break;
					case Pregap:
						currentTrack.pregap = parseTimespec(line, LineType.Pregap);
						break;
					case Index:
						// A track specification must have at least one index specifier to be valid
						currentTrack.isValid = true;

						Integer idx = Integer.parseInt(trimDuplicateWhitespace(line).trim().split(" ")[1]);
						Timespec indexTimespec = parseTimespec(line, LineType.Index);
						currentTrack.indices.add(Pair.makePair(idx, indexTimespec));
						break;
					case AudioTrack:
						// A file should have at least one track to be valid
						currentFile.isValid = true;

						Integer newTrackIdx = Integer.parseInt(line.trim().split(" ")[1]);
						if (currentTrack.isValid) // Push the finished track and start a new one
						{
							currentFile.tracksInFile.add(currentTrack);
							currentTrack = new Track();
							currentTrack.index = newTrackIdx;
						}
						else // Else, ignore the present track and start a new one
						{
							currentTrack = new Track();
							currentTrack.index = newTrackIdx;
						}
						break;
					case Performer:
						currentTrack.artist = parseStringContents(line, LineType.Performer);
						break;
					case Songwriter:
						currentTrack.songwriter = parseStringContents(line, LineType.Songwriter);
						break;
					case Title:
						currentTrack.title = parseStringContents(line, LineType.Title);
						break;
					case Remark:
						// TODO: Parse specific remarks
						currentTrack.miscRemarks.add(line);
						break;
					case File:
						// If encountered another file, start a fresh file
						// However, this is technically out of spec and shouldn't happen,
						// but some applications do this regardless

						// If current file is valid, it can be pushed to the array
						if (currentFile.isValid)
						{
							filesInCuesheet.add(currentFile);
							currentFile = new File();
							currentTrack = new Track();
						}
						else // Else, ignore the present file and make a new state
						{
							currentFile = new File();
							currentTrack = new Track();
						}
						break;
					default:
						break;
				}
			}
			else
			{
				switch (type)
				{
					case File: // Switch to file mode
						inFile = true;
						currentFile.format = getFileType(line);
						currentFile.path = parseFileStringContents(line, currentFile.format);
						break;
					case Catalog:
						currentFile.catalog = parseStringContents(line, LineType.Catalog);
						break;
					case Performer:
						currentFile.artist = parseStringContents(line, LineType.Performer);
						break;
					case Title:
						currentFile.title = parseStringContents(line, LineType.Title);
						break;
					case RemarkComment:
						currentFile.comment = parseStringContents(line, LineType.RemarkComment);
						break;
					case RemarkCompilation:
						currentFile.compilation = parseStringContents(line, LineType.RemarkCompilation);
						break;
					case RemarkComposer:
						currentFile.composer = parseStringContents(line, LineType.RemarkComposer);
						break;
					case RemarkDate:
						currentFile.date = parseStringContents(line, LineType.RemarkDate);
						break;
					case RemarkDiscID:
						currentFile.discID = parseStringContents(line, LineType.RemarkDiscID);
						break;
					case RemarkDiscNumber:
						currentFile.discNumber = parseStringContents(line, LineType.RemarkDiscNumber);
						break;
					case RemarkGenre:
						currentFile.genre = parseStringContents(line, LineType.RemarkGenre);
						break;
					case RemarkReplaygainAlbumGain:
						currentFile.replaygainAlbumGain = parseStringContents(line, LineType.RemarkReplaygainAlbumGain);
						break;
					case RemarkReplaygainAlbumPeak:
						currentFile.replaygainAlbumPeak = parseStringContents(line, LineType.RemarkReplaygainAlbumPeak);
						break;
					case RemarkTotalDiscs:
						currentFile.totalDiscs = parseStringContents(line, LineType.RemarkTotalDiscs);
						break;
					case Remark:
						currentFile.miscRemarks.add(line);
						break;
					default:
						break;
				}
			}
		}

		// Push final file and final track
		if (currentTrack.isValid)
			currentFile.tracksInFile.add(currentTrack);

		if (currentFile.isValid)
			filesInCuesheet.add(currentFile);
	}

	/**
	 * Load a cuesheet into the current object
	 * @param cueFilePath path to cue file
	 */
	public void loadFile(String cueFilePath)
	{
		linesInFile.clear();
		List<String> fileLines = Collections.emptyList();

		try
		{
			fileLines = Files.readAllLines(Paths.get(cueFilePath), StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		parseCuesheet(fileLines);
	}

}
