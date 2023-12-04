package svn45crimes;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class tkde2021crimes1NoiseLogs {
	public static void main(String[] args) {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getNoiseLogsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File logFile = call.getLogFile();
			File noiseLogFile = call.getNoiseLogFile();

			if (logFile.getName().contains("2018.xes.gz0")) {
				if (!call.isAttempted(noiseLogFile)) {
					System.out.println("add noise " + call.toString());
					try {
						call.getNoise().compute(logFile, call, noiseLogFile);
					} catch (Exception e) {
						Call.setError(noiseLogFile, e);
					}
				}
			}
		}

		System.out.println("done");
	}
}
