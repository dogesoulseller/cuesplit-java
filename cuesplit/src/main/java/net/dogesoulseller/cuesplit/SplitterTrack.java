package net.dogesoulseller.cuesplit;

import java.util.ArrayList;

import net.dogesoulseller.cuelib.File;
import net.dogesoulseller.cuelib.Timespec;
import net.dogesoulseller.cuelib.Track;

/**
 * Class containing information about a single track, the start time, and optionally end time
 */
public class SplitterTrack
{
	public File fileInfo;
	public Track trackInfo;
	public Timespec startTime;
	public Timespec endTime;
	public ArrayList<String> ffmpegArguments;

	public SplitterTrack(Track trackInfo, File fileInfo, Timespec startTime, Timespec endTime) {
		this.trackInfo = trackInfo;
		this.fileInfo = fileInfo;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}