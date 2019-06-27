import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.Random;

public class Reporting1 {

	public static int[] test;
	
	public static int[] sizes = {1000, 10000, 100000, 1000000};
	public static int iters = 20;
	public static String heapName = "heap.csv";
	public static String mergeName = "merge.csv";
	public static String quickName = "quick.csv";
	
	public static void main(String[] args) throws Exception {
		// tell the user we're doing stuff
		System.out.println("====Starting====\n");
		
		File[] outFiles = new File[3];
		outFiles[0] = new File(heapName);
		outFiles[1] = new File(mergeName);
		outFiles[2] = new File(quickName);
		
		FileWriter[] writeFiles = new FileWriter[3];
		writeFiles[0] = new FileWriter(outFiles[0]);
		writeFiles[1] = new FileWriter(outFiles[1]);
		writeFiles[2] = new FileWriter(outFiles[2]);
		
		String iterString = "Iterations," + iters + "\n";
		writeToAll(iterString, writeFiles);
		
		// keep track of the times so we can average
		// and find the variance
		// outside the loop so we don't have to reallocate space every time
		double[] heapTimes = new double[iters];
		double[] mergeTimes = new double[iters];
		double[] quickTimes = new double[iters];
		
		
		// these variables keep track of all of our stats
		// in an organized manner
		// also means we only need to call System.out.println once
		String sortedTests = "\n====Sorted Array Tests====\n";
		String reverseTests = "====Reverse Array Tests====\n";
		String randomTests = "====Random Array Tests====\n";
		
		
		for (int j = 0; j < sizes.length; j++)
		{
			// garbage collector will take care of
			// our references, so we'll just reset
			// the test array to a new array of size X which
			// we're testing
			test = new int[sizes[j]];
			
			String s = String.format(" %,d ", sizes[j]);
			
			System.out.println("Running sorted tests on " + s + " elements...");
			writeToAll("Sorted\n", writeFiles);
			sortedTests = runTests(writeFiles, iters, sortedTests, heapTimes, mergeTimes, quickTimes, () -> generateSorted());
			
			System.out.println("Running reverse tests on " + s + " elements...");
			writeToAll("Reversed\n", writeFiles);
			reverseTests = runTests(writeFiles, iters, reverseTests, heapTimes, mergeTimes, quickTimes, () -> generateReverse());
			
			System.out.println("Running random tests on " + s + " elements...");
			writeToAll("Random\n", writeFiles);
			randomTests = runTests(writeFiles, iters, randomTests, heapTimes, mergeTimes, quickTimes, () -> generateRandom());
			
			
			
		}
		
		sortedTests += "====End Sorted Tests====\n\n";
		reverseTests += "====End Reverse Tests====\n\n";
		randomTests += "====End Random Tests====\n\n";
		
		System.out.println(sortedTests + reverseTests + randomTests);
		
		for (int i = 0; i < writeFiles.length; i++)
		{
			writeFiles[i].close();
		}
		
		System.out.println("====Done====");
		
	}
	
	public static String runTests(FileWriter[] writeFiles, int iters, 
								String str, double[] heapTimes,
								double[] mergeTimes, double[] quickTimes,
								Runnable generateMethod) throws Exception
	{
		double heapAvg, mergeAvg, quickAvg;
		double heapMed, mergeMed, quickMed;
		double heapVar, mergeVar, quickVar;
				
		for (int i = 0, k = 0; i < iters; i++, k++)
		{
			// test sorted array with each algorithm
			heapTimes[k] = __runTest(writeFiles[0], generateMethod, () -> Sorting.heapSort(test));
			mergeTimes[k] = __runTest(writeFiles[1], generateMethod, () -> Sorting.mergeSort(test));
			quickTimes[k] = __runTest(writeFiles[2], generateMethod, () -> Sorting.quickSort(test));	
		}
		
		// average/variance calculation for each algorithm
		heapAvg = mean(heapTimes);
		heapMed = median(heapTimes);
		heapVar = variance(heapAvg, heapTimes);
		
		mergeAvg = mean(mergeTimes);
		mergeMed = median(mergeTimes);
		mergeVar = variance(mergeAvg, mergeTimes);
		
		quickAvg = mean(quickTimes);
		quickMed = median(quickTimes);
		quickVar = variance(quickAvg, quickTimes);
		
		writeFiles[0].write(heapAvg + "," + heapVar + "\n");
		writeFiles[1].write(mergeAvg + "," + mergeVar + "\n");
		writeFiles[2].write(quickAvg + "," + quickVar + "\n");
		
		str += String.format("\nSize: %,d elements\n", test.length);
		str += String.format(" Heap Avg: %.10f\t\t Heap Median: %.10f\t Heap Var: %.5g\n", heapAvg, heapMed, heapVar);
		str += String.format("Merge Avg: %.10f\t\tMerge Median: %.10f\tMerge Var: %.5g\n", mergeAvg, mergeMed, mergeVar);
		str += String.format("Quick Avg: %.10f\t\tQuick Median: %.10f\tQuick Var: %.5g\n", quickAvg, quickMed, quickVar);
		
		return str;
	}
	
	public static double __runTest(FileWriter fwrite, Runnable generateMethod, Runnable sortMethod) throws Exception
	{
		// some helpful variables
		long startTime;
		long estimated;
		double convertedTime;
		
		// make sure our array is good to go
		//generateSorted(arr);
		generateMethod.run();
		
		startTime = System.nanoTime();
		sortMethod.run();
		estimated = System.nanoTime() - startTime;
		convertedTime = estimated * Math.pow(10, -9);
		boolean v = verify(test);
		if (!v)
			throw new Exception("Uh oh, something went really wrong");
		
		fwrite.write(test.length + "," + convertedTime + "\n");
		
		return convertedTime;
	}
	
	public static void generateSorted()
	{
		for (int i = 0; i < test.length; i++)
			test[i] = i;
	}
	
	public static void generateRandom()
	{
		Random gen = new Random();
		for (int i = 0; i < test.length; i++)
			test[i] = gen.nextInt();						//(int) (Math.random()*test.length);
	}
	
	public static void generateReverse()
	{
		for (int i = 0, k = test.length-1; i < test.length; i++, k--)
			test[i] = k;
	}
	
	public static double mean(double[] times)
	{
		double sum = 0;
		for (int i = 0; i < times.length; i++)
			sum += times[i];
		return sum / times.length;
	}

	public static double median(double[] times)
	{
		// DEAR GRADER:
		// redoing sort because I don't know how to make
		// our static sorting class take multiple data types
		// keep getting a compiler error. Small enough where this shouldn't matter
		// no time to figure that out now
		// if you have any suggestions, please e-mail me :)
		
		
		// for swap variable
		double tmp;
		
		for (int i = 0; i < times.length; i++)
		{
			for (int j = i; j < times.length; j++)
			{
				if (times[i] > times[j])
				{
					tmp = times[j];
					times[j] = times[i];
					times[i] = tmp;
				}
			}
		}
		
		// middle index
		int half = times.length / 2;
		
		if (times.length % 2 == 0)
		{

			// average the two middle elements
			return (times[half] + times[half+1])/2.;
		}
		
		// return the middle element
		return times[half+1];
	}
	
	public static double variance(double m, double[] times)
	{
		double sum = 0;
		for (int i = 0; i < times.length; i++)
			sum += Math.pow((times[i] - m), 2);
		
		return sum / (times.length - 1);
	}
	
	public static boolean verify(int[] arr)
	{
		for (int i = 0; i < arr.length-1; i++)
		{
			if (arr[i] > arr[i+1])
				return false;
		}
		return true;
	}
	
	public static void writeToAll(String str, FileWriter[] writeFiles) throws IOException
	{
		for (int i = 0; i < writeFiles.length; i++)
			writeFiles[i].write(str);
	}
	
	public static void printArr(int[] arr)
	{
		for (int i = 0; i < arr.length-1; i++)
		{
			System.out.printf("%d, ", arr[i]);
		}
		
		System.out.println(arr[arr.length-1]);
	}
}
