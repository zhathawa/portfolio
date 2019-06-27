# compiler
CC=gcc
# common flags
FLAGS=-g -o
# shell .c files
SH=main.c mysh.c
# shell header
SH_H=mysh.h
# shell object
SH_OBJ=main

all: $(SH_OBJ) 

$(SH_OBJ):
	$(CC) $(FLAGS) $(SH_OBJ) $(SH)

clean:
	rm -f $(SH_OBJ)
