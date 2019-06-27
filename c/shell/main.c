#include <stdio.h>			// general input/output
#include <stdlib.h>			// malloc / free
#include <sys/types.h>		// required for fork / wait
#include <sys/wait.h>		// required for wait
#include <string.h>			// strtok function
#include <unistd.h>			// system calls


// custom
#include "mysh.h"

int main(int argc, char* argv[])
{
	int FLAG = 0;
	while (!FLAG)
	{
		FLAG = prompt();
	}
	
	return 0;
}
