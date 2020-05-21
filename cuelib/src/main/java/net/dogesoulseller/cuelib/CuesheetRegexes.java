package net.dogesoulseller.cuelib;

import java.util.regex.Pattern;

/**
 * Class containing the various precompiled regexes
 * needed to parse a cuesheet
 */
public class CuesheetRegexes
{
	public Pattern catalog;
	public Pattern file;
	public Pattern index;
	public Pattern performer;
	public Pattern postgap;
	public Pattern pregap;
	public Pattern remark;
	public Pattern songwriter;
	public Pattern title;
	public Pattern audioTrack;

	public Pattern filetypeWave;
	public Pattern filetypeAIFF;
	public Pattern filetypeMP3;

	public Pattern remarkDate;
	public Pattern remarkGenre;

	public Pattern duplicateWhitespace;

	public CuesheetRegexes()
	{
		catalog = Pattern.compile("^\\s*CATALOG\\s+\\d+\\s*", Pattern.CASE_INSENSITIVE);
		file = Pattern.compile("^\\s*FILE\\s+\"?.*\"?\\s*", Pattern.CASE_INSENSITIVE);
		index = Pattern.compile("^\\s*INDEX\\s+\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s*", Pattern.CASE_INSENSITIVE);
		performer = Pattern.compile("^\\s*PERFORMER\\s+\"?.+\"?\\s*", Pattern.CASE_INSENSITIVE);
		postgap = Pattern.compile("^\\s*POSTGAP\\s+\\d{2}:\\d{2}:\\d{2}\\s*", Pattern.CASE_INSENSITIVE);
		pregap = Pattern.compile("^\\s*PREGAP\\s+\\d{2}:\\d{2}:\\d{2}\\s*", Pattern.CASE_INSENSITIVE);
		remark = Pattern.compile("^\\s*REM.*", Pattern.CASE_INSENSITIVE);
		songwriter = Pattern.compile("^\\s*SONGWRITER\\s+\"?.+\"?\\s*", Pattern.CASE_INSENSITIVE);
		title = Pattern.compile("^\\s*TITLE\\s+\"?.+\"?\\s*", Pattern.CASE_INSENSITIVE);
		audioTrack = Pattern.compile("^\\s*TRACK\\s+\\d{2}+\\s++AUDIO\\s*", Pattern.CASE_INSENSITIVE);

		filetypeWave = Pattern.compile("^\\s*FILE\\s+\"?.*\"?\\s++WAVE\\s*", Pattern.CASE_INSENSITIVE);
		filetypeAIFF = Pattern.compile("^\\s*FILE\\s+\"?.*\"?\\s++AIFF\\s*", Pattern.CASE_INSENSITIVE);
		filetypeMP3 = Pattern.compile("^\\s*FILE\\s+\"?.*\"?\\s++MP3\\s*", Pattern.CASE_INSENSITIVE);

		remarkDate = Pattern.compile("^\\s*REM\\s+DATE\\s+.*\\s*", Pattern.CASE_INSENSITIVE);
		remarkGenre = Pattern.compile("^\\s*REM\\s+GENRE\\s+.*\\s*", Pattern.CASE_INSENSITIVE);

		duplicateWhitespace = Pattern.compile("\\s+");
	}
}