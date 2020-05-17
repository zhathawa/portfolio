
CC 		:= gcc
FLAGS	:= -std=gnu99 -g -o
LIBS 	:= -lpthread
OBJS 	:= prob1 \
		   prob1.history \
		   prob2

all: $(OBJS)

%: %.c
	$(CC) $(FLAGS) $@ $< $(LIBS)

clean:
	rm $(OBJS)
