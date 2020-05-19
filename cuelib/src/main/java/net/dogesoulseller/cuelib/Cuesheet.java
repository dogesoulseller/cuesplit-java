package net.dogesoulseller.cuelib;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Cuesheet
{
	private CuesheetRegexes regexes;

	public Cuesheet()
	{
		regexes = new CuesheetRegexes();
	}

	public Cuesheet(String cueFilePath)
	{
		regexes = new CuesheetRegexes();
		loadFile(cueFilePath);
	}

	private void parseCuesheet(List<String> fileLines)
	{
		for (String line : fileLines)
		{

		}
	}

	public void loadFile(String cueFilePath)
	{
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
