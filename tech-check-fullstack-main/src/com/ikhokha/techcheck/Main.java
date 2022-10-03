package com.ikhokha.techcheck;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;



public class Main

{

	private static PrintWriter pw = null; //Used a file name unlikely to be duplicated, this can be randomly generated. This file only exists during processing. 

	public static void main(String[] args)

	{

		Map<String, Integer> totalResults = new HashMap<>();
		File mainFile1 = mergeFiles("docs"); //declaring the use file as the merged document


		Path path = Paths.get("tempfile1927364.txt");

		
		long lines = 0;
		try {	//Counting the number of lines in the reference file

			lines = Files.lines(path).count();

		} catch (IOException e) {
			e.printStackTrace();
		}

		CommentAnalyzer commentAnalyzer = new CommentAnalyzer(mainFile1);
		Map<String, Integer> fileResults = commentAnalyzer.analyze();
		addReportResults(fileResults, totalResults);


		System.out.println("RESULTS\n--------");
		totalResults.forEach((k,v) -> System.out.println(k + " : " + v));

		System.out.println("");
		System.out.println("Number of Total Comments: "+lines);
		
		//Cleanup
		cleanText(mainFile1); //execute temp file deleting


	}

	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) 

	{

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			target.put(entry.getKey(), entry.getValue());
		}

	}


	private static File mergeFiles(String dir)  //Created an object that merges all the files into one manageable and readable file,  

	{											//returning the single file as opposed to creating arrays. (this releases stress on the RAM)
		// Input Dir is the associated dir, set this way in the case there is a user input required for this field later on

		File returnFile = null; 
		try 
		{

			pw = new PrintWriter ("tempfile1927364.txt"); //Initialize printwriter, the filename is irrelevant as it gets deleted upon completion
			File docPath = new File(dir);
			File [] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt")); //creation of array of files in folder

			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			Thread t1 = null; //initializing thread here as to use it later outside the for loop

			BufferedReader br = null; 
			for (int i = 0; i < commentFiles.length; i++) { //creating a loop that executes the same number of times as there are files in the folder

				br = new BufferedReader (new FileReader(commentFiles[i])) ; //initializing buffered reader to each file, incrementing per file, amending initialized temp file
				RunnableClass rc = new RunnableClass(br); //initializing the runnable class containing br in for loop, this creates a thread per file
				t1 = new Thread(rc, "Thread1");
				
				Set<Thread> threadSet = Thread.getAllStackTraces().keySet(); //checks the number of active threads
				
				do //will issue a 10ms delay if the number of threads currently active is more than 10, this will create a limit to thread being 10
				{
					//System.out.println(threadSet.size());
					TimeUnit.MILLISECONDS.sleep(10);
				}
				while(threadSet.size()>10);
				
				t1.start(); //Only initialize new thread once there is a thread available. 
				
				

			}

			//br.close(); //close open object
			System.out.print("Thinking..");
			do
			{
				System.out.print(".");
				TimeUnit.MILLISECONDS.sleep(250);
			}
			while(t1.isAlive()); //Checks if there are active threads, this is considered thinking. Made it a little active by adding a . for each 250ms thinking
								//This was required as the PrintWriter was closing prior to the end of the thread, causing an issue with the total number of written lines 
			
			System.out.println("");
			System.out.println("Here you go: ");
			System.out.println("");
			br.close();
			pw.flush(); // clear write to cache
			pw.close(); // close opened object
			returnFile = new File("tempfile1927364.txt"); //declaring the file post amendment to be returned as the usable file. 
		}
		catch(Exception e)
		{

			System.out.println("An error has occured.");
			System.out.println( e);

		}

		return returnFile;

	}

	private static void cleanText(File x1) //object used to delete the temp file once all processing is completed with it

	{
		
		File myObj = x1;
		if (myObj.delete()) //deleting created file that temporarily held consolidated data
		{ 
			System.out.println("");
			System.out.println("Cache Cleared.");

		} 
		else 
		{
			System.out.println("");
			System.out.println("Failed to Clear Cache.");

		}

	}

	static class RunnableClass implements Runnable { //Threading class
		private BufferedReader bread = null;

		RunnableClass(BufferedReader bread) {
			this.bread = bread; 
		}

		@Override
		public void run() {

			try {
				synchronized (bread) {
					String line = bread.readLine();
					while (line != null)
					{
						pw.println(line); //writing to the file as a process of each thread

						line = bread.readLine();

					}

					//line = bread.readLine();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}


