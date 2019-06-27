
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.lang.Math;

public class Reporting2 {
	
	
	public static int[] hSort = null;
	public static int[] mSort = null;
	public static int[] qSort = null;
	
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("usage: java Reporting2 [input file]");
			return;
		}
		
		try {
			// our input file
			File inFile = new File(args[0]);
			
			// make sure it exists
			if (!inFile.exists())
			{
				System.out.println("Please check that " + args[0] + " exists and try again.");
				return;
			}
			
			int lineCount = 0;
			
			Scanner scan = new Scanner(inFile);
			
			while (scan.hasNextLine())
			{
				// increment scanner
				scan.nextLine();
				
				// increment line count
				lineCount++;
			}
			scan.close();
			// get a new scanner
			scan = new Scanner(inFile);
			
			// allocate our array
			hSort = new int[lineCount];
			mSort = new int[lineCount];
			qSort = new int[lineCount];
			
			int index = 0;
			String num = null;
			int inum;
			
			// populate our array
			while (scan.hasNextLine())
			{
				num = scan.nextLine();
				inum = Integer.valueOf(num);
				hSort[index] = inum;
				mSort[index] = inum;
				qSort[index++] = inum;
			}
			
			int iterations = 20;
			
			//Sorting.mergeSort(toSort);
			SortThread hThread = new SortThread("HS.txt", iterations, hSort, () -> Sorting.heapSort(hSort));
			SortThread mThread = new SortThread("MS.txt", iterations, mSort, () -> Sorting.mergeSort(mSort));
			SortThread qThread = new SortThread("QS.txt", iterations, qSort, () -> Sorting.quickSort(qSort));
			
			//System.out.println("====Original====");
			//printArr(hSort);
			
			hThread.start();
			mThread.start();
			qThread.start();
			
			// wait until we're done sorting
			hThread.join();
			mThread.join();
			qThread.join();
			
			double heapMed = Reporting1.median(hThread.convertedTime);
			double mergeMed = Reporting1.median(mThread.convertedTime);
			double quickMed = Reporting1.median(qThread.convertedTime);
			
			String pstring = "HS = " + heapMed + "; ";
			pstring += "MS = " + mergeMed + "; ";
			pstring += "QS = " + quickMed;
			System.out.println(pstring);
			
			//System.out.println("Done");
			
			
			scan.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printArr(int[] arr)
	{
		for (int i = 0; i < arr.length; i++)
			System.out.println(arr[i]);
	}
	
	private static class SortThread extends Thread {
		private String outFileName;
		private int iters;
		private int[] originalArray;
		private int[] toSort;
		private Runnable method;
		private long startTime;
		private long estimated;
		private int timeIndex;
		private double[] convertedTime;
		
		
		public SortThread(String outFileName, int iters, int[] arr, Runnable method)
		{
			this.outFileName = outFileName;
			this.iters = iters;
			this.method = method;
			this.timeIndex = 0;
			this.convertedTime = new double[iters];
			this.originalArray = new int[arr.length];
			this.toSort = arr;
			this.copyArray(arr, this.originalArray);
		}
		
		private void copyArray(int[] from, int[] to)
		{
			for (int i = 0; i < to.length; i++)
				to[i] = from[i];
		}
		
		private void writeToFile(int[] arr) throws IOException
		{
			File outFile = new File(outFileName);
			FileWriter outWrite = new FileWriter(outFile);
			
			for (int i = 0; i < arr.length; i++)
				outWrite.write(arr[i] + "\n");
			
			outWrite.close();
		}
		
		public void run()
		{
			for (int i = 0; i < this.iters; i++)
			{
				startTime = System.nanoTime();
				method.run();
				estimated = System.nanoTime() - startTime;
				convertedTime[timeIndex++] = estimated * Math.pow(10, -9);
				
				if (i == 0)
				{
					try {
						writeToFile(this.toSort);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// don't need to copy
				if (i == this.iters-1)
					continue;
				
				// reset our array for another run
				this.copyArray(this.originalArray, this.toSort);
				
			}
			
		}
	}
}
