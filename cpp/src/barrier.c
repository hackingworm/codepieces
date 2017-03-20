void barrier(void) {
	__asm__ __volatile__("": : :"memory");
}
