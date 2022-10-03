package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CommentAnalyzer {

	private File file;

	public CommentAnalyzer(File file) {
		this.file = file;
	}

	public Map<String, Integer> analyze() {

		Map<String, Integer> resultsMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

			String line = null;
			while ((line = reader.readLine()) != null) {

				if (line.length() < 15) {							//Individual If statements as these checks are not dependant on one another and individually validated

					incOccurrence(resultsMap, "SHORTER_THAN_15"); //A number check can be included using a if statement and a series of checks to evaluate what to do based on file value

				} 
				
				String[][] arrStr = compareIfs();
				
				int arrayLen = arrStr.length;
				for( int j = 0; j < arrayLen; j++) //loop that executes up to the number or parameters to be checked
				{
					if (line.toLowerCase().contains(arrStr[j][1].toLowerCase())) {

						incOccurrence(resultsMap, arrStr[j][0]);

					} 
				}
				
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error processing file: " + file.getAbsolutePath());
			e.printStackTrace();
		}

		return resultsMap;

	}

	private static String[][] compareIfs()
	{
		File docPath = new File("compareParam//Param.txt"); //created a custom format parameters file. amending this file will adjust the output
		BufferedReader br = null; 

		Path path = Paths.get("compareParam//Param.txt");

		long lines = 0;
		try {	//Counting the number of lines in the reference file

			lines = Files.lines(path).count();

		} catch (IOException e) {
			e.printStackTrace();
		}
		String[][] strArr = new String[(int)lines][2]; //declaring the array to the number of lines checked, a 2D array where column1 is the Naming convention and column2 is the searchable value


		try 
		{
			br = new BufferedReader (new FileReader(docPath)) ; //initializing buffered reader to each file, incrementing per file, amending initialized temp file

			String line = br.readLine();
			String [] arrline = line.split("#"); //splitting the read line using the # delimiter

			int cnt = 0;
			while (line != null) //array population
			{

				strArr[cnt][0] = arrline[0];
				strArr[cnt][1] = arrline[1];

				try {
					line = br.readLine();
					arrline = line.split("#");
				} catch (Exception e) {}

				cnt++;
			}

			//line = br.readLine().split("#");


			br.close(); //close open object
		}
		catch (Exception e)
		{
			System.out.println(e);
		}


		return strArr;
	}

	/**
	 * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
	 * @param countMap the map that keeps track of counts
	 * @param key the key for the value to increment
	 */
	private void incOccurrence(Map<String, Integer> countMap, String key) {

		countMap.putIfAbsent(key, 0);
		countMap.put(key, countMap.get(key) + 1);
	}

}
