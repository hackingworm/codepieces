#include <errno.h>
#include <sched.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#if 1
static const int StackSize = (1 << 11);
static char stack[StackSize] __attribute__ ((aligned(1 << 12)));

static int work(void *arg) {
	long int tid = syscall(SYS_gettid);
	printf("%d/%ld %s\n", getpid(), tid, (char*)arg);
	sleep(1);
	return tid;
}
#endif

int main(int argc, char **argv) {
	#if 1
	int tid = clone(work, stack + StackSize,
			CLONE_THREAD | CLONE_SIGHAND | CLONE_VM, argv[0]);
	if (-1 == tid) {
		perror(NULL);
		return -1;
	}

	sleep(1);
	printf("%d/%ld %d\n", getpid(), syscall(SYS_gettid), tid);
	return 0;
	#else
	long tid = syscall(SYS_clone, NULL, 
				CLONE_THREAD | CLONE_SIGHAND);
	if (-1 == tid) {
		perror(NULL);
		return -1;
	}

	#if 1
	long newTid = syscall(SYS_gettid);
	printf("%d %ld/%ld\n", __LINE__, newTid, tid);
	if (0 == tid) {
		printf("%d %d/%ld\n", __LINE__, getpid(), tid);
		sleep(1);
		return tid;
	}
	#endif

	printf("%d %d/%ld\n", __LINE__, getpid(), tid);

	#if 0
	int status;
	if (-1 == waitpid(tid, &status, 0)) {
		perror(NULL);
		return -1;
	} else {
		printf("%d\n", status);
		return status;
	}
	#endif
	sleep(1);
	return 0;
	#endif
}
