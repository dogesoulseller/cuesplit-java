package net.dogesoulseller.cuesplit;

import java.util.ArrayList;
import java.util.Collections;

public class Cuesplit
{
	public static void main(String[] args)
	{
		ArrayList<String> argsList = new ArrayList<>();
		Collections.addAll(argsList, args);

		CLIArguments cliArgs = new CLIArguments(argsList);

		CueSplitter splitter = new CueSplitter(cliArgs);

		System.out.println("Test");
	}
}