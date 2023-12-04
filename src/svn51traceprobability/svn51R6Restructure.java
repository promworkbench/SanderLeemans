package svn51traceprobability;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

public class svn51R6Restructure {
	public static void main(String[] args) throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();

		for (Call call : parameters.getCalls()) {
			{
				File old = call.getMeasureFileOld();
				if (old.exists()) {
					File newf = call.getMeasureFile();
					newf.getParentFile().mkdirs();
					Files.copy(old, newf);
				}
			}

			{
				File old = call.getMeasureTimeFileOld();
				if (old.exists()) {
					File newf = call.getMeasureTimeFile();
					newf.getParentFile().mkdirs();
					Files.copy(old, newf);
				}
			}
		}
	}
}