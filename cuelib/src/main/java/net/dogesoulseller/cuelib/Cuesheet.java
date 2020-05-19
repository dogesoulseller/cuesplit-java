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

	public Cuesheet()
	{
		regexes = new CuesheetRegexes();
	}

	public Cuesheet(String cueFilePath)
	{
		regexes = new CuesheetRegexes();
		loadFile(cueFilePath);
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

	private void parseCuesheet(List<String> fileLines)
	{
		// Go through all lines, remove empty ones, and try to detect the line types
		for (String line : fileLines)
		{
			if (line.isBlank() || line.isEmpty())
				continue;

			LineType type = getLineType(line);
			linesInFile.add(Pair.makePair(type, line));
		}

		// TODO: Go through the cleaned results and fill in file+track data
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
