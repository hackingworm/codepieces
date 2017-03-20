package test;

class Cooper {
	long minQ, maxQ;
	long quantity, produceCalled, consumeCalled;

	Cooper(int minQ, int maxQ, int initQ) {
		this.minQ = minQ;
		this.maxQ = maxQ;
		quantity = initQ;
	}

	long getQuantity() {
		return quantity;
	}

	long getProduceCalled() {
		return produceCalled;
	}

	long getConsumeCalled() {
		return consumeCalled;
	}

	synchronized void produce() {
		if (maxQ > quantity) {
			++quantity;
			++produceCalled;
			// System.out.println("Produce " + quantity
			// 					+ " " + produceCalled);
		}
	}

	synchronized void consume() {
		if (minQ < quantity) {
			--quantity;
			++consumeCalled;
			// System.out.println("Consume " + quantity
			// 					+ " " + consumeCalled);
		}
	}
}

public class Worker extends Thread {
	enum Role {
		PRODUCER,
		CONSUMER
	};

	Role role;

	Cooper cooper;

	static volatile boolean stop;
	static final int PERIOD = 100;

	Worker(Role role, Cooper cooper) {
		this.role = role;
		this.cooper = cooper;
	}

	public void run() {
		while(!stop) {
			if (Role.PRODUCER == role) {
				cooper.produce();
				// Thread.yield();
			} else {
				cooper.consume();
				// Thread.yield();
			}
		}
	}

	public static void main(String[] args) {
		Cooper cooper = new Cooper(0, 1000000, 0);

		Worker producer = new Worker(Role.PRODUCER, cooper);
		Worker consumer = new Worker(Role.CONSUMER, cooper);

		producer.start();
		consumer.start();

		try {
			Thread.sleep(PERIOD);
			stop = true;

			consumer.join();
			producer.join();
		} catch (InterruptedException ie) {
			System.err.println(ie);
		}

		System.out.println(cooper.getQuantity()
							+ " " + cooper.getProduceCalled()
							+ " " + cooper.getConsumeCalled());
	}
}
