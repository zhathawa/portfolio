
public class MaxHeap {
	// use an array based implementation
	private int[] items;
	
	// keep track of how many items we have
	private int numItems;
	
	// max sure we don't get too full
	private int maxSize;
	
	// constructor
	public MaxHeap(int maxSize)
	{
		items = new int[maxSize];
		this.setNumItems(0);
		this.setMaxSize(maxSize);
	}
	
	public MaxHeap(int[] arr)
	{
		items = arr;
		this.setNumItems(arr.length);
		this.setMaxSize(arr.length);
		this.buildHeap();
	}
	
	public int[] getItems()
	{
		return items;
	}
	
	public void setItems(int[] items)
	{
		this.items = items;
	}
	
	public int getNumItems()
	{
		return numItems;
	}
	
	public void setNumItems(int numItems)
	{
		this.numItems = numItems;
	}
	
	public int getMaxSize()
	{
		return maxSize;
	}
	
	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	// build heap in place
	public void buildHeap()
	{
		/*
		for (int i = 0; i < arr.length; i++)
			insert(arr[i]);
		*/
		
		for (int i = (items.length-2)/2; i >= 0; i--)
			siftDown(i);
	}
	
	// insert a node into our heap
	public void insert(int val)
	{
		// need to increase the size of our heap
		if (numItems == maxSize)
		{
			maxSize = maxSize * 2;
			
			// create new array
			int[] new_items = new int[maxSize];
			
			// copy our array
			for (int i = 0; i < numItems; i++)
			{
				new_items[i] = items[i];
			}
			
			// make our items array the newly expanded array
			items = new_items;
		}
		
		// add our value to the end of the heap
		items[numItems] = val;
		
		// sift the item we just added so it's in the appropriate place
		siftUp(numItems);
		
		// increment the number of items we have
		numItems++;	
	}

	// remove the max element from the heap
	public int pop()
	{
		// copy our max
		int max = items[0];
		
		// overwrite our max element with the last element
		// of the complete tree
		items[0] = items[--numItems];
		
		// make sure the actual max is on top
		// and the element we just moved gets sifted
		// to the appropriate place
		siftDown(0);
		
		// return max
		return max;
	}
	
	// find the median
	public double median()
	{
		// use a double
		// when we have an even number of elements,
		// we'll need to average
		// set the initial median to the max element
		// this way we have something if we only have one element
		double med = items[0];
		
		// num iterations * 2
		int total_items = numItems;
		
		// pop until we reach left hand item to the median
		for (int i = 0; i < (total_items / 2); i++)
		{
			med = pop();
		}
		
		// average left hand / right hand items to get median
		// for an array with an even number of elements
		if ((total_items % 2) == 0)
			return (med + pop()) / 2.0;
		
		
		// return median
		return pop();
	}
	
	// move given node down to correct location
	public void siftDown(int node)
	{
		// get value to compare
		int val = items[node];
		
		// parent
		int p = node;
		
		// left child
		int c = 2 * p + 1;
		
		// while we still have items to compare
		while (c < numItems)
		{
			// right child bigger than left child
			if (c + 1 < numItems && items[c] < items[c+1])
			{
				c = c + 1;
			}
			
			// quit
			if (val >= items[c])
				break;
			
			// otherwise...swap
			items[p] = items[c];
			items[c] = val;
			
			// update indices
			p = c;
			c = 2 * p + 1;
		}
		
		// final place
		items[p] = val;
	}
	
	// move given node up to correct location
	public void siftUp(int node)
	{
		// value to compare
		int val = items[node];
		
		// child
		int c = node;
		
		// parent
		int p = (node - 1) / 2;
		
		// while we still have items to compare
		while (p >= 0)
		{
			// we've reached appropriate place
			// don't actually need to move it now
			if (val <= items[p])
				break;
			
			// otherwise swap
			items[c] = items[p];
			items[p] = val;
			
			// update indices
			c = p;
			p = (c - 1) / 2;
		}
	}

	// sanity check
	public void printHeap()
	{
		for (int i = 0; i < numItems; i++)
		{
			System.out.printf("%d ", items[i]);
		}
		System.out.println();
	}
}
