#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

// constants
#define N  			       10
#define BUFFER_SIZE     (N/2)

// error macro
#define error_handler(msg) fprintf(stderr, "ERROR: %s\n", msg) 

// condition structure
struct condition_t
{
	int count;
	sem_t sem;
};

// globals
sem_t mutex, next;
int next_count = 0;
int count = 0;

struct condition_t not_empty, not_full;

// prototypes
void  init();
void  cleanup();
void  cpost(struct condition_t*);
void  cwait(struct condition_t*);
void  print_history();
void  producer();
void* consumer(void* params);

// half the number of values that will be produced
int buffer[BUFFER_SIZE];

int history[N*2];
int global_index = 0;

int main(int argc, char* argv[])
{
	// initialize our variables
	init();

	// create our thread variables
	pthread_t child_t;
	pthread_attr_t attr;

	// initialize attributes
	pthread_attr_init(&attr);

	// start the child thread
	pthread_create(&child_t, &attr, consumer, NULL);

	// start our producer
	producer();

	// wait for our child to complete
	pthread_join(child_t, NULL);
	
	// cleanup
	cleanup();

	// print history
	print_history();
}

void cleanup()
{
	sem_destroy(&mutex);
	sem_destroy(&next);
	sem_destroy(&not_full.sem);
	sem_destroy(&not_empty.sem);
}

void init()
{
	sem_init(&mutex, 0, 1);
	sem_init(&next, 0, 0);

	not_full.count = 0;
	sem_init(&not_full.sem, 0, 0);

	not_empty.count = 0;
	sem_init(&not_empty.sem, 0, 0);
}

void cpost(struct condition_t* c)
{
	if (c->count > 0)
	{
		next_count++;
		sem_post(&c->sem);
		sem_wait(&next);
		next_count--;
	}
}

void cwait(struct condition_t* c)
{
	c->count++;
	if (next_count > 0)
		sem_post(&next);
	else
		sem_post(&mutex);
	sem_wait(&c->sem);
	c->count--;
}

void* consumer(void* params)
{
	// sleep to give the producer time to add data
	int current;

	// actual stuff we want to do
	for (int i = 0; i < N; i++)
	{
		sem_wait(&mutex);
		if (count == 0)
			cwait(&not_empty);
		current = i % BUFFER_SIZE;
		printf("Child iteration #%d. buffer[%d]: %d\n", i, current, buffer[current]);
		count--;
		history[global_index++] = 2;
		cpost(&not_full);
		if (next_count > 0)
			sem_post(&next);
		else
			sem_post(&mutex);
	}
} 

void producer()
{
	int data = 0;
	int current;

	for (int i = 0; i < N; i++, data++)
	{
		sem_wait(&mutex);
		if (count == BUFFER_SIZE-1)
			cwait(&not_full);
		current = i % BUFFER_SIZE;
		buffer[current] = data;
		printf("Parent iteration #%d.\n", i);
		count++;
		history[global_index++] = 1;
		cpost(&not_empty);
		if (next_count > 0)
			sem_post(&next);
		else
			sem_post(&mutex);
	}
}

void print_history()
{
	printf("History: ");
	for (int i = 0; i < N*2 - 1; i++)
		printf("%d, ", history[i]);
	printf("%d\n", history[N*2-1]);
}
