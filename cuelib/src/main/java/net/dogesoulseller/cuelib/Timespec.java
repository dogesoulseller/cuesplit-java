package net.dogesoulseller.cuelib;

import java.util.Formatter;

public class Timespec implements Comparable<Timespec>
{
	@Override
	public int compareTo(Timespec other)
	{
		// Check minutes
		if (this.minutes < other.minutes)
		{
			return -1;
		}
		else if (this.minutes > other.minutes)
		{
			return 1;
		}
		else // If minutes are equal, check seconds
		{
			// Check seconds
			if (this.seconds < other.seconds)
			{
				return -1;
			}
			else if (this.seconds > other.seconds)
			{
				return 1;
			}
			else // If seconds are equal, check frames
			{
				// Check frames
				if (this.frames < other.frames)
				{
					return -1;
				}
				else if (this.frames > other.frames)
				{
					return 1;
				}
				else // If frames are equal, the timespecs are equal
				{
					return 0;
				}
			}
		}
	}

	public Integer minutes;
	public Integer seconds;
	public Integer frames;

	public Timespec(Integer _minutes, Integer _seconds, Integer _frames)
	{
		minutes = _minutes;
		seconds = _seconds;
		frames = _frames;
	}

	public String toFfmpegSpec()
	{
		StringBuilder buffer = new StringBuilder();
		Formatter fmt = new Formatter(buffer);

		Integer hours = minutes < 60 ? 0 : minutes / 60;
		Integer newMinutes = minutes < 60 ? minutes : minutes - 60;
		Integer milliseconds = (int) ((double)frames / 75.0 * 1000.0);

		// HH:MM:SS.mmm
		fmt.format("%02d:%02d:%02d.%03d", hours, newMinutes, seconds, milliseconds);

		String ffmpegSpec = buffer.toString();

		fmt.close();

		return ffmpegSpec;
	}
}