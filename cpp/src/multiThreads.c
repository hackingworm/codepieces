#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

static int useAddFetch = 0;
static volatile long long start = 0;
static long long stop;
static volatile int busy = 0;

static void* inc(void* index) {
	long long loopCount = 0;
	for (;;) {
		if (useAddFetch) {
			long long result =
				__atomic_add_fetch(&start, 1, __ATOMIC_RELAXED);
				
			if (stop < result) {
				printf("%2d %19lld\n", *((int*)index), loopCount);
				return NULL;
			}

			++loopCount;
	
			// printf("%2d %4d\n", *((int*)index), result);
		} else {
			int expected = 0, desired = 1;
			if (__atomic_compare_exchange(&busy, &expected, &desired, 1,
				__ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE)) {
				++start;
				long long result = start;
				__atomic_clear(&busy, __ATOMIC_RELEASE);
	
				if (stop < result) {
					printf("%2d %19lld\n", *((int*)index), loopCount);
					return NULL;
				}
	
				++loopCount;
	
				// printf("%2d %19lld\n", *((int*)index), result);
			} else {
				// fprintf(stderr, "%s\n", expected? "Busy": "");
	
				extern int pthread_yield(void);
				// pthread_yield();
			}
		}
	}
}

int main(int argc, char** argv) {
	int start = 1, num;

	if (1 < argc && 0 == strcmp("--useAddFetch", argv[1])) {
		useAddFetch = 1;
		++start;
	}

	if (start + 2 > argc) {
		return -1;
	}

	num = atoi(argv[start]);
	stop = atoll(argv[start + 1]);

	pthread_t *threads = malloc(num * sizeof(pthread_t));
	int *threadIDs = malloc(num * sizeof(int));

	struct timeval startTime;
	gettimeofday(&startTime, NULL);

	for (int index = 0; index < num; ++index) {
		threadIDs[index] = index;
		int result = pthread_create(&threads[index],
				(const pthread_attr_t*)NULL, inc,
				(void*)&threadIDs[index]);
		assert(0 == result);
	}

	for (int index = 0; index < num; ++index) {
		int result = pthread_join(threads[index], NULL);
		assert(0 == result);
	}

	struct timeval stopTime;
	gettimeofday(&stopTime, NULL);

	fprintf(stderr, "Time elapsed %lldus\n",
			 (long long)(stopTime.tv_sec  - startTime.tv_sec)
				* 1000000
			+ stopTime.tv_usec - startTime.tv_usec);

	return 0;
}
