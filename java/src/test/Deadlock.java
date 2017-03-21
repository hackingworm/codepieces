package test;

class Product {
	int lowWM, highWM, quantity;

	Product(int lowWM, int highWM, int quantity) {
		this.lowWM = lowWM;
		this.highWM = highWM;
		this.quantity = quantity;
	}

	int getLowWM() {
		return lowWM;
	}

	int getHighWM() {
		return highWM;
	}

	int setQuantity(int quantity) {
		return this.quantity = quantity;
	}

	int getQuantity() {
		return quantity;
	}
}

class DL implements Runnable {
	int id;
	Product a, b;
	int remained;

	DL(int id, Product a, Product b, int remained) {
		this.id = id;
		this.a = a;
		this.b = b;
		this.remained = remained;
	}

	public void run() {
		try {
			for (;;) {
				int qa;
				synchronized(a) {
					while (a.getLowWM() == a.getQuantity()) {
						a.wait();
					}

					qa = a.getQuantity();
					a.setQuantity(a.getLowWM());
					a.notify();
				}

				int earned = qa - a.getLowWM(), qb, vacancy;
				synchronized(b) {
					while (b.getHighWM() == b.getQuantity()) {
						b.wait();
					}

					qb = b.getQuantity();
					vacancy = b.getHighWM() - qb;
					if (earned < vacancy) {
						vacancy = earned;
					}
					b.setQuantity(qb + vacancy);
					b.notify();
				}

				remained = earned - vacancy;
				System.out.println(id + ": " + qa + "/" + earned + " " + qb + "/" + vacancy + " " + remained);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}

public class Deadlock {
	public static void main(String[] args) {
		Product a = new Product(1, 5, 3), b = new Product(2, 6, 4);
		new Thread(new DL(1, a, b, 0)).start();
		new Thread(new DL(2, b, a, 0)).start();
	}
}
