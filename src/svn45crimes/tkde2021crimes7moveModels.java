package svn45crimes;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class tkde2021crimes7moveModels {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();

		int i = -1;

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			for (Measure measure : parameters.getMeasures()) {
				{
					File newf = call.getMeasureFile(measure);
					File oldf = call.getMeasureFileOld(measure);
					//File newf = call.getDiscoveredModelFile();
					//File oldf = call.getDiscoveredModelFileOld();
					newf.getParentFile().mkdirs();
					if (oldf.exists()) {
						ProcessBuilder p = new ProcessBuilder("svn", "rename", "-q", oldf.getAbsolutePath(),
								newf.getAbsolutePath());
						p.inheritIO();
						p.start().waitFor();

						i++;
					}
				}
				{
					File newf = call.getMeasureTimeFile(measure);
					File oldf = call.getMeasureTimeFileOld(measure);
					//File newf = call.getDiscoveredModelTimeFile();
					//File oldf = call.getDiscoveredModelTimeFileOld();
					newf.getParentFile().mkdirs();
					if (oldf.exists()) {
						ProcessBuilder p = new ProcessBuilder("svn", "rename", "-q", oldf.getAbsolutePath(),
								newf.getAbsolutePath());
						p.inheritIO();
						p.start().waitFor();

						i++;
					}
				}
			}

			if (i > 100) {
				System.out.println("committing ...");
				ProcessBuilder p = new ProcessBuilder("svn", "commit", "-m", "m",
						parameters.getMeasuresDirectory().getAbsolutePath());
				p.inheritIO();
				p.start().waitFor();
				System.out.println("done ...");
				i = -1;
			}
		}
	}
}