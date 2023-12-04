package batch;

import java.util.concurrent.Callable;

public class TimeOut {
	
	@SuppressWarnings("deprecation")
	public <X> X runWithHardTimeOut(Callable<X> function, long maxRunTimeSeconds) throws Exception {
		
		if (maxRunTimeSeconds == 0) {
			return function.call();
		}
		
		TimeOutThread<X> t = new TimeOutThread<X>(function);
		//t.setDaemon(true);
        t.start();
        t.join(maxRunTimeSeconds * 1000);
        if (t.isAlive()) {
        	//I know Thread.stop() is deprecated. But how am I supposed to stop this thread otherwise?
        	//It's not waiting, it's running!
            t.stop();
            t = null;
            throw new TimeOutException();
        }
		
        //check whether the thread did not throw an exception
        if (t.getException() != null) {
        	throw t.getException();
        }
        
        if (t.getResult() == null) {
        	throw new Exception("result is null");
        }
        
        return t.getResult();
	}
	
	public class TimeOutException extends Exception {
		private static final long serialVersionUID = 6124458067340740091L;

        public TimeOutException() {
        	super("time out");
        }
    }
	
	private class TimeOutThread<X> extends Thread {
		private Exception exception = null;
		private X result = null;
		private Callable<X> function;
		
		public TimeOutThread(Callable<X> function) {
			super("time out thread");
			this.function = function;
		}
		
		public void run() {
			try {
				result = function.call();
			} catch (Exception e) {
				exception = e;
				e.printStackTrace();
			}
		}
	
		public Exception getException() {
			return exception;
		}
	
		public X getResult() {
			return result;
		}
	}
}
