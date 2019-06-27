#ifndef MYSH_H
#define MYSH_H

// standard libraries
#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/wait.h>
#include <string.h>

// prototypes
int prompt();
void printPrompt();
void cmdParse(int, char**, char*, int*);
void free_args(char**, int);




#endif
