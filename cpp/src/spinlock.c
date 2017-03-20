#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string.h>
#include <sys/time.h>

#if defined(X86)
static inline void doze(void) {
	__asm__ __volatile__("rep;nop": : : "memory");
} 

static inline void wakeup(void) {}
#elif defined(AARCH64)
static inline void doze(void) {
	__asm__ __volatile__("wfe": : :"memory");
}

static inline void wakeup(void) {
	__asm__ __volatile__("sev": : :"memory");
}
#endif

static void spinLock(volatile int *lock) {
	for (;;) {
		unsigned int count = 0;

		while (*lock) {
			++count;
			if (0xfff & count) {
			 	doze();
			} else {
				extern int nanosleep(const struct timespec *req,
										struct timespec *rem);
				const struct timespec req = {0, 1000000};
				nanosleep(&req, NULL);
			}
		}

		int locked = 1, returned;

		__atomic_exchange(lock, &locked, &returned, __ATOMIC_RELAXED);

		if (0 == returned) {
			break;
		}
	}
}

static void spinUnlock(volatile int *lock) {
	*lock = 0;
	wakeup();
}

static int lock = 0;
static volatile long long start = 0;
static long long stop;

static void* inc(void* index) {
	long long loopCount = 0;

	for (;;) {
		if (stop > start) {
			spinLock(&lock);
			if (stop > start) {
				++start;
				++loopCount;
			}
			spinUnlock(&lock);
		} else {
			printf("%2d %19lld\n", *((int*)index), loopCount);
			return NULL;
		}
	}

}

int main(int argc, char** argv) {
	int num;

	if (2 > argc) {
		fprintf(stderr, "Usage: %s numOfThreads stopValue\n", argv[0]);
		return -1;
	}

	num = atoi(argv[1]);
	stop = atoll(argv[2]);

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

	fprintf(stderr, "Time elapsed %lldus, result %lld\n",
			 (long long)(stopTime.tv_sec  - startTime.tv_sec)
				* 1000000
			+ stopTime.tv_usec - startTime.tv_usec, start);

	return 0;
}
