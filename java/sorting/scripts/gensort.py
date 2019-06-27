
def main(fname, size):
	f = open(fname, "w")
	for i in range(size):
		f.write(str(i) + "\n")
	return
