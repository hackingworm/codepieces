#include <iostream>

using namespace std;

class Base {
public:
	Base() {}
	virtual ~Base() {}
	void print() {
		cout << "Base" << endl;
	}
};

class Derived: public Base {
public:
	Derived() {}
	virtual ~Derived() {}
	void print() {
		cout << "Derived" << endl;
	}
};

int main(int argc, char** argv) {
	Derived d;
	Derived *dp = &d;
	dp->print();
	Base *bp = &d;
	bp->print();

	return 0;
}
