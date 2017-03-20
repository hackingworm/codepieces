#include <stdio.h>
#include <assert.h>
#include <pthread.h>
#include <asm/prctl.h>

static void *getFG(void *registers) {
	extern int arch_prctl(int code, unsigned long *addr);

	unsigned long *regs = (unsigned long*)registers;

	assert(0 == arch_prctl(ARCH_GET_FS, &regs[0]));
	assert(0 == arch_prctl(ARCH_GET_GS, &regs[1]));

	return NULL;
}

static void printFG(unsigned long registers[2]) {
	printf("FS 0x%016lx, GS 0x%016lx\n", registers[0], registers[1]);
}

int main(void) {
	unsigned long registers[2];

	getFG((void*)registers);
	printFG(registers);

	pthread_t thread;
	pthread_create(&thread, (const pthread_attr_t*)NULL, getFG, (void*)registers);
	assert(0 == pthread_join(thread, NULL));

	printFG(registers);

	return 0;
}
