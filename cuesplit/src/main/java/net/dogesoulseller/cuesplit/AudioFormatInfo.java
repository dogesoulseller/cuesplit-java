package net.dogesoulseller.cuesplit;

public class AudioFormatInfo
{
	public AudioFormat format;
	public Integer quality;
	public Integer kbps;

	public String getOpusQuality()
	{
		switch (quality)
		{
			case 10:
				return "256k";
			case 9:
				return "224k";
			case 8:
				return "192k";
			case 7:
				return "160k";
			case 6:
				return "128k";
			case 5:
				return "112k";
			case 4:
				return "96k";
			case 3:
				return "64k";
			case 2:
				return "48k";
			case 1:
				return "40k";
			case 0:
				return "32k";
			default:
				return "256k";
		}
	}

	public String getFormatExtension()
	{
		switch (format)
		{
			case ALAC:
			case AAC:
				return "m4a";
			case FLAC:
				return "flac";
			case MP3:
				return "mp3";
			case TTA:
				return "tta";
			case WAV:
				return "wav";
			case Opus:
			case Vorbis:
				return "ogg";
			default:
				return "mka";
		}
	}
}
