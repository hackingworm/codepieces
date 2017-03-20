#include <stdio.h>
#include <stdlib.h>

class Entity {
	static int nextId;
	int id;

	void* operator new(size_t size, int step) {
		nextId += step;
		return malloc(size);
	}

public:
	static Entity* instantiate(int step) {
		Entity* entity = new(step) Entity();
		entity->id = Entity::nextId;
		return entity;
	}

	int getId() {
		return id;
	}
};

int Entity::nextId = 0;

int main(void) {
	for (int i = 1; i < 4; i++) {
		Entity* entity = Entity::instantiate(i);
		printf("0x%llx %d\n", (unsigned long long)entity,
			entity->getId());
		delete entity;
	}

	return 0;
}
