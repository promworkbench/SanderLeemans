package svn55datastochastics;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

public class svn55Rx6Restructure {
	public static void main(String[] args) throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();

		for (Call call : parameters.getCalls()) {

			if (call.getMeasure() instanceof MeasureUEMSC && !call.getStochasticAlgorithm().createsDataModels()) {

				Call newCall = new Call(call.getLogFile(), call.getAlgorithm(), call.getStochasticAlgorithm(),
						call.getRepetition(), new MeasureDUEMSC(), call.getParameters());

				{
					File old = call.getMeasureFile();
					if (old.exists()) {
						File newf = newCall.getMeasureFile();
						newf.getParentFile().mkdirs();
						Files.copy(old, newf);
					}
				}

				{
					File old = call.getMeasureTimeFile();
					if (old.exists()) {
						File newf = newCall.getMeasureTimeFile();
						newf.getParentFile().mkdirs();
						Files.copy(old, newf);
					}
				}
			}
		}
	}
}