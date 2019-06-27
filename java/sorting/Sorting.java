import java.lang.Math;

public class Sorting {

	public static void heapSort(int[] toSort)
	{
		MaxHeap h = new MaxHeap(toSort);
		
		int endUnsorted = toSort.length - 1;
		while (endUnsorted > 0)
		{
			toSort[endUnsorted] = h.pop();
			endUnsorted--;
		}
	}
	
	public static void mergeSort(int[] toSort)
	{
		__merge_sort(toSort, 0, toSort.length-1);
	}
	
	public static void quickSort(int[] toSort)
	{
		__quick_sort(toSort, 0, toSort.length-1);
	}
	
	// helper methods
	private static void __merge_sort(int[] toSort, int left, int right)
	{
		if (left >= right)
			return;
		
		int mid = (left + right) / 2;
		
		__merge_sort(toSort, left, mid);
		__merge_sort(toSort, mid+1, right);
		
		__merge(toSort, left, mid, right);
	}
	
	private static void __merge(int[] toSort, int left, int mid, int right)
	{
		// create a left temp array and a right temp array
		int lSize = mid - left + 1;
		int rSize = right - mid;
		
		int[] l = new int[lSize];
		int[] r = new int[rSize];
		
		// copy the appropriate indices of toSort
		// into our temp arrays
		
		// first the left
		for (int i = 0; i < lSize; i++)
		{
			l[i] = toSort[left + i];
		}
		
		for (int i = 0; i < rSize; i++)
		{
			r[i] = toSort[i + mid + 1];
		}
		
		// some tracking vars
		int i = 0, j = 0, k = left;
		
		while (i < lSize && j < rSize)
		{
			// if element in left tmp array
			// is larger than right tmp array
			if (l[i] <= r[j])
			{
				// add from left array
				toSort[k] = l[i];
				i++;
			}
			else
			{
				// add right array
				toSort[k] = r[j];
				j++;
			}
			
			// increment where we are in our original
			// array
			k++;
		}
		
		// copy any remaining elements back into
		// the original array
		while (i < lSize)
		{
			toSort[k] = l[i];
			i++;
			k++;
		}
		
		while (j < rSize)
		{
			toSort[k] = r[j];
			j++;
			k++;
		}
		
		// we done
		return;
	}
	
	
	private static int partition(int[] toSort, int left, int right)
	{
		int p = pivot(toSort, left, right);
		
		int i = left-1;
		int j = right + 1;
		
		while (true)
		{
			do
			{
				i++;
			} while (toSort[i] < p);
			
			do 
			{
				j--;
			} while (toSort[j] > p);
			
			if (i < j)
				swap(toSort, i, j);
			else
				return j;
		}
	}
	
	// return the median element of three randomly
	// selected elements
	private static int pivot(int[] arr, int left, int right)
	{
		//return arr[(left + right) / 2];
		
		int one = randIndex(arr.length-1, left, right);
		int two = randIndex(arr.length-1, left, right);
		int three = randIndex(arr.length-1, left, right);

		
		if (arr[one] <= arr[two])
		{
			if (arr[two] <= arr[three])
			{
				return arr[two];
			}
			else if (arr[one] <= arr[three])
			{
				return arr[three];
			}
			else
				return arr[one];
		}
		else
		{
			if (arr[one] <= arr[three])
			{
				// greater than two and less than 3
				return arr[one];
			}
			else if (arr[two] <= arr[three])
			{
				// one was greater than two
				// three was greater than two
				return arr[three];
			}
			else
				return arr[two];
		}
	}
	
	private static void __quick_sort(int[] toSort, int left, int right)
	{
		if (left >= right)
			return;
		
		int split = partition(toSort, left, right);
		
		__quick_sort(toSort, left, split);
		__quick_sort(toSort, split+1, right);
	}
	
	
	private static int randIndex(int arrSize, int left, int right)
	{
		return (int) (Math.random() * (right - left) + left);
	}
	
	private static void swap(int[] arr, int i, int j)
	{
		int tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}
}

