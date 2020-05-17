#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

// error handler macro
#define error(str) { fprintf(stderr, "%s\n", str); }

// buffer size constant
// if this get's modified, need to // modify the toWrite array
#define BUFFER_SIZE   4

// globals
int version = 0;
int read_count = 0;

// strings to print
char* toWrite[] =
	{
		"Roses are red.",
		"Violet's are blue.",
		"Threads honestly seem,",
		"Rather cool."
	};

// where we gonna write to / read from
char* buffer = NULL;

// condition struct
struct condition_t
{
	int count;
	sem_t sem;
};

// declare our global condition variables
struct condition_t readcv;
struct condition_t writecv;

sem_t mutex;

// prototypes
void  cleanup();
void  cpost(struct condition_t* c);
void  cwait(struct condition_t* c);
void  init();
void* reader(void* args);
void* writer(void* args);

int main(int argc, char* argv[])
{
	// initialize globals
	init();
	
	// thread variables
	pthread_t t0, t1, t2;
	pthread_attr_t attr;
	pthread_attr_init(&attr);
	
	// initialize our id parameters
	// to pass our reader threads
	void* id1 = malloc(sizeof(int));
	if (id1 == NULL)
	{
		error("Could not allocate ID variable 1.")
		return -2;
	}

	*((int*)id1) = 1;
	
	void* id2 = malloc(sizeof(int));
	if (id2 == NULL)
	{
		error("Could not allocate ID variable 2.")
		return -3;
	}
	*((int*)id2) = 2;
	
	// make all our threads
	pthread_create(&t0, &attr, writer, NULL);
	pthread_create(&t1, &attr, reader, id1);
	pthread_create(&t2, &attr, reader, id2);

	// wait until our threads are complete
	pthread_join(t0, NULL);
	pthread_join(t1, NULL);
	pthread_join(t2, NULL);
	
	// cleanup
	free(id1);
	free(id2);

	// cleanup our globals
	cleanup();
	return 0;
}

void init()
{
	// point our buffer at an actual memory location
	buffer = malloc(sizeof(char) * 100);
	if (buffer == NULL)
	{
		error("Could not allocate buffer memory.");
		return;
	}

	sem_init(&mutex, 0, 1);

	readcv.count = 0;
	sem_init(&readcv.sem, 0, 0);

	writecv.count = 0;
	sem_init(&writecv.sem, 0, 0);
}

void cleanup()
{
	free(buffer);
	sem_destroy(&mutex);
	sem_destroy(&readcv.sem);
	sem_destroy(&writecv.sem);
}

void cpost(struct condition_t* c)
{
	sem_wait(&mutex);
	if (c->count > 0)
	{
		sem_post(&(c->sem));
	}
	sem_post(&mutex);
}

void cwait(struct condition_t* c)
{
	sem_wait(&mutex);
	c->count++;
	sem_post(&mutex);
	sem_wait(&(c->sem));
	sem_wait(&mutex);
	c->count--;
	sem_post(&mutex);
}

void* reader(void* args)
{
	// find the id of the thread
	int id = *((int*)args);

	// initialize our current version
	// -1 so it's different than our 
	// version variable and we'll actually print the first thing in the buffer
	int current_version = -1;

	sem_wait(&mutex);
	read_count++;
	if (read_count == 2)
	{
		read_count = 0;
		sem_post(&mutex);
		cpost(&writecv);
	}
	else
		sem_post(&mutex);

	while (current_version < BUFFER_SIZE)
	{
		cwait(&readcv);

		sem_wait(&mutex);
		read_count++;
		sem_post(&mutex);
		
		// actually read
		printf("Reader #%d: %s\n", id, buffer);
		
		sem_wait(&mutex);
		read_count--;
		current_version = version;
		if (read_count == 0)
		{
			sem_post(&mutex);
			cpost(&writecv);
		}
		else
			sem_post(&mutex);
	}
}

void* writer(void* args)
{
	cwait(&writecv);
	while (version < BUFFER_SIZE)
	{
		// update buffer and version
		sprintf(buffer, "%s", toWrite[version]);
		sem_wait(&mutex);
		version++;
		sem_post(&mutex);
		
		// release write lock
		cpost(&readcv);
		cpost(&readcv);

		cwait(&writecv);
	}

	cpost(&readcv);
	cpost(&readcv);
}
