#include <sched.h>
#include <atomic>

using namespace std;

atomic<int> flag(0);

void atomic_inc(int& i) {
	for (;;) {
		int expected = 0;
		if (flag.compare_exchange_strong(expected, 1, memory_order_acquire)) {
			++i;
			flag.store(0, memory_order_release);
			break;
		} else {
			sched_yield();
		}
	}
}
