To run:
	My test:
		javac Reporting1.java
		java Reporting1
		Output: Three csv files with the times / variances. Post-processing
				was done in LibreOffice Calc

	Class test:
		javac Reporting2.java
		java Reporting2 [input file]
		Output:
			If no input file, program will halt.
			If input file cannot be found, program will halt.
			
			If file found, launch three threads, one to run each sorting
			algorithm.
			
			Output files: HS.txt, MS.txt, QS.txt

			Output files will be in the current directory you're running
			the program from.
		
			Previous iterations of output files can be found in the 
			output directory.

	Directories:
	============
		output: previous iteration of Reporting2
		scripts: Generate files to use for tests.
			Includes: gen.py and gensort.py.
				python gen.py [size] [output file]
					creates file to simulate unsorted input

				python gensort.py [output file] [size]
					creates file to simulate sorted input

		testfiles: contains generated files from python scripts in scripts/
					directory		
