#include <iostream>

using namespace std;

class Base {
public:
	virtual ~Base() {
		cout << "~Base()" << endl;
	}
};

class Derived: public Base {
	~Derived() {
		cout << "~Derived()" << endl;
	}
};

int main (int argc, char** argv) {
	const int *cp;
	int *p;
	p = const_cast<int*>(cp);

	Base *bp1 = new Base();
	Derived *dp1;
	dp1 = static_cast<Derived*>(bp1);

	Derived *dp2 = new Derived();
	Base *bp2 = static_cast<Base*>(dp2);
	bp2 = dynamic_cast<Base*>(dp2);

	delete bp2;

	int i = -1;
	const int ic = i;
	const int *pic = &ic;
	int *const cpi = const_cast<int*>(&ic);

	cout << sizeof(Base) << " " << sizeof(Derived) << endl;

	return 0;
}
	
