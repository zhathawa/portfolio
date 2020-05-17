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
void  producer();
void* consumer(void* params);

// half the number of values that will be produced
int buffer[BUFFER_SIZE];

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
	// if we have someone waiting
	// on this semaphore, do all this stuff
	if (c->count > 0)
	{
		// alert global we're waiting
		next_count++;

		// let the other process/thread go
		sem_post(&c->sem);

		// put ourselves on the queue
		sem_wait(&next);

		// we're off the queue
		next_count--;
	}
}

void cwait(struct condition_t* c)
{
	// make sure we increment
	// our cv count so we know
	// people are waiting
	c->count++;

	// if next_count is greater than 0, someone is waiting on us
	// otherwise, release the mutex
	if (next_count > 0)
		sem_post(&next);
	else
		sem_post(&mutex);

	// wait on our condition variable
	sem_wait(&c->sem);

	// make sure we decrement
	c->count--;
}

void* consumer(void* params)
{
	// sleep to give the producer time to add data
	int current;

	// actual stuff we want to do
	for (int i = 0; i < N; i++)
	{
		// get exclusive access
		sem_wait(&mutex);

		// buffer's empty, nothing to read
		if (count == 0)
			cwait(&not_empty);
		
		// read
		current = i % BUFFER_SIZE;
		printf("Child iteration #%d. buffer[%d]: %d\n", i, current, buffer[current]);
		count--;

		// make sure the producer knows
		// we have room in the buffer
		cpost(&not_full);

		// release a lock we might be holding
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
		// make sure we have exclusive access
		sem_wait(&mutex);

		// we're full, so wait
		if (count == BUFFER_SIZE-1)
			cwait(&not_full);

		// write
		current = i % BUFFER_SIZE;
		buffer[current] = data;
		printf("Parent iteration #%d.\n", i);

		// increment our count tracking variable
		count++;

		// make sure the consumer knows that it can go
		cpost(&not_empty);

		// release the lock we might be holding
		if (next_count > 0)
			sem_post(&next);
		else
			sem_post(&mutex);
	}
}
