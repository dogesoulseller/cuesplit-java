package net.dogesoulseller.cuelib;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexTest
{
	private CuesheetRegexes regexes;

	@Before
	public void setup()
	{
		regexes = new CuesheetRegexes();
	}

	@Test
	public void catalogRegex()
	{
		Assert.assertTrue(regexes.catalog.matcher("CATALOG 000000000001").matches());
		Assert.assertTrue(regexes.catalog.matcher("	CATALOG  000000000001").matches());
		Assert.assertFalse(regexes.catalog.matcher("FILE \"testcatalog.mp3\" WAVE").matches());
	}

	@Test
	public void fileRegex()
	{
		Assert.assertTrue(regexes.file.matcher("FILE \"testfile.mp3\" MP3").matches());
		Assert.assertTrue(regexes.file.matcher("FILE \"testfile.mp3\" AIFF").matches());
		Assert.assertTrue(regexes.file.matcher("FILE testfile.mp3 MP3").matches());
		Assert.assertTrue(regexes.file.matcher("FILE testfile.mp3 WAVE").matches());
		Assert.assertFalse(regexes.file.matcher("REM DATE FILE.mp3").matches());
	}

	@Test
	public void indexRegex()
	{
		Assert.assertTrue(regexes.index.matcher(" INDEX 01 00:01:01 ").matches());
		Assert.assertFalse(regexes.index.matcher(" INDEX 000 00:00:00").matches());
		Assert.assertFalse(regexes.index.matcher(" INDEX 01 0:00:01").matches());
	}

	@Test
	public void performerRegex()
	{
		Assert.assertTrue(regexes.performer.matcher(" PERFORMER Doge").matches());
		Assert.assertTrue(regexes.performer.matcher(" PERFORMER \"Doge\"").matches());
		Assert.assertFalse(regexes.performer.matcher(" PERFORMER ").matches());
	}

	@Test
	public void gapRegex()
	{
		Assert.assertTrue(regexes.pregap.matcher(" PREGAP 00:01:00 ").matches());
		Assert.assertFalse(regexes.pregap.matcher(" PREGAP 001 00:10:00").matches());
		Assert.assertFalse(regexes.pregap.matcher(" PREGAP 0:01:00 ").matches());
		Assert.assertFalse(regexes.pregap.matcher(" INDEX 01 00:01:01 ").matches());

		Assert.assertTrue(regexes.postgap.matcher(" POSTGAP 00:01:00 ").matches());
		Assert.assertFalse(regexes.postgap.matcher(" POSTGAP 001 00:10:00").matches());
		Assert.assertFalse(regexes.postgap.matcher(" POSTGAP 0:01:00 ").matches());
		Assert.assertFalse(regexes.postgap.matcher(" INDEX 01 00:01:01 ").matches());
	}

	@Test
	public void remarkRegex()
	{
		Assert.assertTrue(regexes.remark.matcher(" REM").matches());
		Assert.assertTrue(regexes.remark.matcher(" REM DATE ").matches());
		Assert.assertFalse(regexes.remark.matcher(" FILE \"REM\" ").matches());
	}

	@Test
	public void songwriterRegex()
	{
		Assert.assertTrue(regexes.songwriter.matcher("SONGWRITER Doge ").matches());
		Assert.assertTrue(regexes.songwriter.matcher("SONGWRITER \"DOGE\"").matches());
		Assert.assertFalse(regexes.songwriter.matcher("REM SONGWRITER").matches());
	}

	@Test
	public void titleRegex()
	{
		Assert.assertTrue(regexes.title.matcher("TITLE Doge ").matches());
		Assert.assertTrue(regexes.title.matcher("TITLE \"Doge the test\"").matches());
		Assert.assertFalse(regexes.title.matcher("REM TITLE").matches());
		Assert.assertFalse(regexes.title.matcher("FILE Title.mp3 WAVE").matches());
	}

	@Test
	public void audioTrackRegex()
	{
		Assert.assertTrue(regexes.audioTrack.matcher("TRACK 01 AUDIO ").matches());
		Assert.assertFalse(regexes.audioTrack.matcher("TRACK 030 AUDIO").matches());
		Assert.assertFalse(regexes.audioTrack.matcher("REM TRACKTITLE TITLE").matches());
		Assert.assertFalse(regexes.audioTrack.matcher("TRACK 01 BINARY").matches());
	}

	@Test
	public void filetypeRegexes()
	{
		Assert.assertTrue(regexes.filetypeAIFF.matcher("FILE \"doge man.aiff\" AIFF").matches());
		Assert.assertTrue(regexes.filetypeAIFF.matcher("FILE dogeman.aiff AIFF").matches());
		Assert.assertFalse(regexes.filetypeAIFF.matcher("FILE doge.mp3 MP3 ").matches());
		Assert.assertFalse(regexes.filetypeAIFF.matcher("FILE doge.flac WAVE ").matches());

		Assert.assertTrue(regexes.filetypeMP3.matcher("FILE \"doge man.mp3\" MP3").matches());
		Assert.assertTrue(regexes.filetypeMP3.matcher("FILE dogeman.mp3 MP3 ").matches());
		Assert.assertFalse(regexes.filetypeMP3.matcher("FILE doge.aiff AIFF ").matches());
		Assert.assertFalse(regexes.filetypeMP3.matcher("FILE doge.flac WAVE ").matches());

		Assert.assertTrue(regexes.filetypeWave.matcher("FILE \"doge man.wav\" WAVE").matches());
		Assert.assertTrue(regexes.filetypeWave.matcher("FILE dogeman.flac WAVE ").matches());
		Assert.assertFalse(regexes.filetypeWave.matcher("FILE doge.aiff AIFF ").matches());
		Assert.assertFalse(regexes.filetypeWave.matcher("FILE doge.mp3 MP3 ").matches());
	}

	@Test
	public void additionalInfoRemarkRegexes()
	{
		Assert.assertTrue(regexes.remarkDate.matcher(" REM DATE 1997 ").matches());
		Assert.assertTrue(regexes.remarkDate.matcher("REM DATE 2003").matches());
		Assert.assertTrue(regexes.remarkDate.matcher("REM DATE goodenough").matches());
		Assert.assertFalse(regexes.remarkDate.matcher("REM GENRE Pop").matches());
		Assert.assertFalse(regexes.remarkDate.matcher("FILE date.mp3 MP3").matches());

		Assert.assertTrue(regexes.remarkGenre.matcher(" REM GENRE Synthyorsomething").matches());
		Assert.assertTrue(regexes.remarkGenre.matcher(" REM GENRE Pop").matches());
		Assert.assertFalse(regexes.remarkGenre.matcher(" REM DATE 1990").matches());
		Assert.assertFalse(regexes.remarkGenre.matcher(" FILE genre.mp3 MP3 ").matches());
	}
}
