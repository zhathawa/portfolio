import random
import sys

def main():
	size = int(sys.argv[1])
	output = sys.argv[2]
	o = open(output, "w")
	for i in range(size):
		o.write(str(random.randint(0, size)) + "\n")
	o.close()
	return


main()
