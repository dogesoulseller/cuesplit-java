package net.dogesoulseller.cuelib;

import java.util.ArrayList;

/**
 * Class containing information about a single cuesheet file
 */
public class File
{
	public String catalog;
	public String artist;
	public String title;
	public String path;
	public FileType format;

	public String comment;
	public String compilation;
	public String composer;
	public String discID;
	public String discNumber;
	public String replaygainAlbumGain;
	public String replaygainAlbumPeak;
	public String totalDiscs;
	public String date;
	public String genre;

	public boolean isValid = false;

	public ArrayList<Track> tracksInFile;
	public ArrayList<String> miscRemarks;

	public File()
	{
		tracksInFile = new ArrayList<>();
		miscRemarks = new ArrayList<>();
	}
}