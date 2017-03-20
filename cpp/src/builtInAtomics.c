#include <stdio.h>

int main(void) {
	int i = 0;

	__atomic_thread_fence(__ATOMIC_RELAXED);
	__atomic_add_fetch(&i, 1, __ATOMIC_RELAXED);
	__atomic_thread_fence(__ATOMIC_CONSUME);
	__atomic_add_fetch(&i, 1, __ATOMIC_CONSUME);
	__atomic_thread_fence(__ATOMIC_ACQUIRE);
	__atomic_add_fetch(&i, 1, __ATOMIC_ACQUIRE);
	__atomic_thread_fence(__ATOMIC_RELEASE);
	__atomic_add_fetch(&i, 1, __ATOMIC_RELEASE);
	__atomic_thread_fence(__ATOMIC_ACQ_REL);
	__atomic_add_fetch(&i, 1, __ATOMIC_ACQ_REL);
	__atomic_thread_fence(__ATOMIC_SEQ_CST);
	__atomic_add_fetch(&i, 1, __ATOMIC_SEQ_CST);
		
	printf("%d\n", i);

	return 0;
}
