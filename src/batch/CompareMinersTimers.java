package batch;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import batch.miners.Miner;

public class CompareMinersTimers {
	
	public class CompareMinersTimer {
		private AtomicLong addToWhenFinished; 
		private long start;
		private CompareMinersResult result;
		private File file;
		private Miner miner;
		
		public CompareMinersTimer(AtomicLong addToWhenFinished, CompareMinersResult result, File file, Miner miner) {
			this.addToWhenFinished = addToWhenFinished;
			this.result = result;
			this.file = file;
			this.miner = miner;
			start = System.currentTimeMillis();
		}
		
		public long stop() {
			long time = System.currentTimeMillis() - start;
			addToWhenFinished.addAndGet(time);
			if (file != null && miner != null) {
				result.reportMiningTime(file, miner, time);
			}
			return time;
		}
	}
	
	private HashMap<Miner, AtomicLong> totalTimeMiner;
	private Long globalStart;
	private CompareMinersResult result;
	
	public CompareMinersTimers(List<Miner> miners, CompareMinersResult result) {
		this.result = result;
		totalTimeMiner = new HashMap<Miner, AtomicLong>();
		for (Miner miner : miners) {
			totalTimeMiner.put(miner, new AtomicLong());
		}
		globalStart = System.currentTimeMillis();
	}
	
	public CompareMinersTimer startMiningTimer(File file, Miner miner) {
		return new CompareMinersTimer(totalTimeMiner.get(miner), result, file, miner);
	}
	
	public long getGlobalTime() {
		return System.currentTimeMillis() - globalStart;
	}
	
	public long getTimeOfMiner(Miner miner) {
		return totalTimeMiner.get(miner).get();
	}
}
