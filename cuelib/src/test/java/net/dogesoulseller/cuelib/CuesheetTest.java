package net.dogesoulseller.cuelib;

import org.junit.Assert;
import org.junit.Test;

public class CuesheetTest
{
	/**
	 * Test file 0 is the file for the soundtrack of Touhou 10
	 * It has a single FILE command and follows the spec
	 */
	@Test
	public void parseTestFile0()
	{
		var cuePath = getClass().getClassLoader().getResource("testCuesheet0.cue").getPath();

		Cuesheet sheet = new Cuesheet(cuePath);

		// File 0 should have a single FILE spec
		Assert.assertEquals(1, sheet.filesInCuesheet.size());

		// Define this here as a shorter way of getting the first file's info
		var fileSpec = sheet.filesInCuesheet.get(0);

		// Basic file parse results
		Assert.assertEquals("Shanghai Alice Gengakudan", fileSpec.artist);
		Assert.assertEquals("Touhou Fuujinroku ~ Mountain of Faith", fileSpec.title);
		Assert.assertEquals("Touhou Fuujinroku ~ Mountain of Faith.flac", fileSpec.path);
		Assert.assertEquals(FileType.Wave, fileSpec.format);
		Assert.assertEquals(18, fileSpec.tracksInFile.size());

		// For the tracks, check the most possible error points: first, last, and random in the middle
		var firstTrackSpec = fileSpec.tracksInFile.get(0);
		var lastTrackSpec = fileSpec.tracksInFile.get(fileSpec.tracksInFile.size()-1);
		var middleTrackSpec = fileSpec.tracksInFile.get(7);

		// There is no track-specific artist info in this test file
		Assert.assertNull(firstTrackSpec.artist);
		Assert.assertNull(middleTrackSpec.artist);
		Assert.assertNull(lastTrackSpec.artist);

		// Check track titles
		Assert.assertEquals("Fuuinsareshi Kamigami", firstTrackSpec.title);
		Assert.assertEquals("Fall of Fall ~ Akimeku Taki", middleTrackSpec.title);
		Assert.assertEquals("Player's Score", lastTrackSpec.title);

		// Every track has a single index
		Assert.assertEquals(1, firstTrackSpec.indices.size());
		Assert.assertEquals(1, middleTrackSpec.indices.size());
		Assert.assertEquals(1, lastTrackSpec.indices.size());

		// Expected timespecs
		Timespec firstTimespec = new Timespec(0, 0, 0);
		Timespec middleTimespec = new Timespec(27, 16, 37);
		Timespec lastTimespec = new Timespec(73, 20, 39);

		// Check index timespecs
		Assert.assertTrue(firstTimespec.compareTo(firstTrackSpec.indices.get(0).getSecond()) == 0);
		Assert.assertTrue(middleTimespec.compareTo(middleTrackSpec.indices.get(0).getSecond()) == 0);
		Assert.assertTrue(lastTimespec.compareTo(lastTrackSpec.indices.get(0).getSecond()) == 0);

		// Check that every index is nr 1
		Assert.assertEquals(1, firstTrackSpec.indices.get(0).getFirst().intValue());
		Assert.assertEquals(1, middleTrackSpec.indices.get(0).getFirst().intValue());
		Assert.assertEquals(1, lastTrackSpec.indices.get(0).getFirst().intValue());
	}

	/**
	 * Test file 1 is the file for the soundtrack of Touhou 10, just like file 0
	 * It has a single FILE command and follows the spec, but uses UTF-8 encoded Japanese characters
	 */
	@Test
	public void parseTestFile1()
	{
		var cuePath = getClass().getClassLoader().getResource("testCuesheet1.cue").getPath();

		Cuesheet sheet = new Cuesheet(cuePath);

		// File 1 should have a single FILE spec
		Assert.assertEquals(1, sheet.filesInCuesheet.size());

		// Define this here as a shorter way of getting the first file's info
		var fileSpec = sheet.filesInCuesheet.get(0);

		// Basic file parse results
		Assert.assertEquals("上海アリス幻樂団", fileSpec.artist);
		Assert.assertEquals("東方風神録　～ Mountain of Faith", fileSpec.title);
		Assert.assertEquals("Touhou Fuujinroku ~ Mountain of Faith.flac", fileSpec.path);
		Assert.assertEquals(FileType.Wave, fileSpec.format);
		Assert.assertEquals(18, fileSpec.tracksInFile.size());

		// For the tracks, check the most possible error points: first, last, and random in the middle
		var firstTrackSpec = fileSpec.tracksInFile.get(0);
		var lastTrackSpec = fileSpec.tracksInFile.get(fileSpec.tracksInFile.size()-1);
		var middleTrackSpec = fileSpec.tracksInFile.get(7);

		// There is no track-specific artist info in this test file
		Assert.assertNull(firstTrackSpec.artist);
		Assert.assertNull(middleTrackSpec.artist);
		Assert.assertNull(lastTrackSpec.artist);

		// Check track titles
		Assert.assertEquals("封印されし神々", firstTrackSpec.title);
		Assert.assertEquals("フォールオブフォール　～ 秋めく滝", middleTrackSpec.title);
		Assert.assertEquals("プレイヤーズスコア", lastTrackSpec.title);

		// Every track has a single index
		Assert.assertEquals(1, firstTrackSpec.indices.size());
		Assert.assertEquals(1, middleTrackSpec.indices.size());
		Assert.assertEquals(1, lastTrackSpec.indices.size());

		// Expected timespecs
		Timespec firstTimespec = new Timespec(0, 0, 0);
		Timespec middleTimespec = new Timespec(27, 16, 37);
		Timespec lastTimespec = new Timespec(73, 20, 39);

		// Check index timespecs
		Assert.assertTrue(firstTimespec.compareTo(firstTrackSpec.indices.get(0).getSecond()) == 0);
		Assert.assertTrue(middleTimespec.compareTo(middleTrackSpec.indices.get(0).getSecond()) == 0);
		Assert.assertTrue(lastTimespec.compareTo(lastTrackSpec.indices.get(0).getSecond()) == 0);

		// Check that every index is nr 1
		Assert.assertEquals(1, firstTrackSpec.indices.get(0).getFirst().intValue());
		Assert.assertEquals(1, middleTrackSpec.indices.get(0).getFirst().intValue());
		Assert.assertEquals(1, lastTrackSpec.indices.get(0).getFirst().intValue());
	}
}