#include <stdio.h>

void atomic_add(int i, int *v)			
{									
	unsigned long tmp;						
	int result;							
									
	asm volatile("// atomic_add\n"				
"1:	ldxr	%w0, %2\n"						
"	add	%w0, %w0, %w3\n"				
"	stxr	%w1, %w0, %2\n"						
"	cbnz	%w1, 1b"						
	: "=&r" (result), "=&r" (tmp), "+Q" (*v)
	: "Ir" (i));							
}									

int atomic_add_return(int i, int *v)		
{									
	unsigned long tmp;						
	int result;							
									
	asm volatile("// atomic_add_return\n"			
"1:	ldxr	%w0, %2\n"						
"	add	%w0, %w0, %w3\n"				
"	stlxr	%w1, %w0, %2\n"						
"	cbnz	%w1, 1b\n"						
"	dmb	ish"
	: "=&r" (result), "=&r" (tmp), "+Q" (*v)
	: "Ir" (i)							
	: "memory");							
									
	return result;							
}

int main(void) {
	int v = 0;
	atomic_add(1, &v);
	printf("%d\n", v);
	printf("%d\n", atomic_add_return(2, &v));
	return 0;
}
