package net.dogesoulseller.cuelib;

import java.util.ArrayList;

/**
 * Class containing information about a single cuesheet track
 */
public class Track
{
	public Integer index;
	public String artist;
	public String songwriter;
	public String title;
	public String path;
	public Timespec pregap;
	public Timespec postgap;

	public boolean isValid = false;

	public ArrayList<Pair<Integer, Timespec>> indices;
	public ArrayList<String> miscRemarks;

	public Track()
	{
		indices = new ArrayList<>();
		miscRemarks = new ArrayList<>();
	}
}