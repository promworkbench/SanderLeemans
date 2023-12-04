package batch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSingleton {
	private static ExecutorService service;

	private ThreadPoolSingleton() {

	}

	public static ExecutorService getInstance() {
		if (service == null) {
			service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		}
		return service;
	}
	
	public static void shutdown() {
		service.shutdown();
		service = null;
	}
}
