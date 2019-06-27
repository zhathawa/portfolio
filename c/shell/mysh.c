#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/wait.h>
#include <string.h>

// custom libraries
#include "mysh.h"

#define MAX_LINE		80 
#define BUFFER_SIZE		255

int prompt()
{
	// print the prompt
	printPrompt();

	// take user input

	// we're only going to read 80 characters of a command
	// but we're going to allocate a larger space in case the user goes over
	// hopefully this will grab all the input, so that the stdin is completely cleared
	char* buffer = malloc(sizeof(char)*BUFFER_SIZE);

	// make sure that we actually have a pointer
	if (buffer == NULL)
	{
		fprintf(stderr, "Error: Could not allocate buffer for user input.\n");
		return 1;
	}
	
	// actually read our output
	char* tmp_buf = fgets(buffer, BUFFER_SIZE, stdin);
	
	// make sure we obtained readable input
	if (tmp_buf == NULL)
	{
		fprintf(stderr, "Error: Could not read input from stdin stream.\n");
		return 2;
	}

	// allow for 40 arguments
	// unreasonably large, but, :)
	const int argc = MAX_LINE/2 + 1;
	char* argv[argc];

	// counter variable
	int cnt = 0;

	// parse our arguments
	cmdParse(argc, argv, buffer, &cnt);

	// don't need our buffer anymore
	free(buffer);

	// okay we have our arguments now let's check if we have exit or cd
	// these two functions should be called immediately instead of forking
	// into a parent and child process
	if (strcmp("exit", argv[0]) == 0)
	{
		free_args(argv, cnt);
		exit(0);
	}
	if (strcmp("cd", argv[0]) == 0)
	{
		// first we need to get the path we should change to
		//char* path = find_path(argv[1]);
		int i = chdir(argv[1]);
		
		free_args(argv, cnt);
		return 0;
	}

	// let's say that we got the above working
	// now we need to fork into parent / child processes
	pid_t pid = fork();
	
	if (pid == 0)
	{
		// child behavior
		execvp(argv[0], argv);
	}
	else
	{
		// wait for our child process
		wait(NULL);
	}
	
	// free our heap
	free_args(argv, cnt);

	return 0;
}

void free_args(char* argv[], int len)
{
	for (int i = 0; i < len; i++)
		free(argv[i]);
}

void printPrompt()
{
	printf("\nosh> ");
}

// gonna have to work out the kinks here. Not sure why this isn't working appropriately
void cmdParse(int argc, char* argv[], char* input, int* cnt)
{
	/*
		General premise:
			1. Iterate through input buffer and use strtok to find white space
			2. Store each string separated by white space into a separate
				bucket of the argv array
	*/
	// let's cut out new lines
	char* newline_index = strchr(input, '\n');
	
	// did we find a newline?
	if (newline_index != NULL)
	{
		*newline_index = ' ';
	}
	
	// fill argv[0] with the name of the command
	// make a delim var
	const char* delim = " ";
	char* buffer[1];
	size_t len;
	buffer[0] = strtok(input, delim);
	if (buffer[0] != NULL)
	{
		len = strlen(buffer[0]);
		argv[0] = malloc(sizeof(char) * len + 1);
		argv[0] = strcpy(argv[0], buffer[0]);
	}
	// increment cnt
	(*cnt)++;
	
	// now loop over our array until we're out of tokens
	// create a buffer variable to check things out first
	for (int i = 1; i < argc; i++)
	{
		buffer[0] = strtok(NULL, delim);

		if (buffer[0] != NULL)
		{
			len = strlen(buffer[0]);
			argv[i] = malloc(sizeof(char) * len + 1);
			argv[i] = strcpy(argv[i], buffer[0]);
			(*cnt)++;
		}
		else
			argv[i] = NULL;
	}

	// ensure that our final argument is NULL
	argv[argc-1] = NULL;
}
