package caise2020multilevel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel2AcceptingPetriNet;
import org.processmining.multilevelminer.plugins.MultiLevelModelImportPlugin;

import com.google.common.io.Files;

import thesis.helperClasses.FakeContext;

public class Perform3FlattenModels {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getFlattenedModelsDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		ArrayUtils.reverse(files);

		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (int runNr : parameters.getRuns()) {
					for (int foldNr : parameters.getFolds()) {

						Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);

						File modelFile = call.getModelFile();
						File flattenedModelFile = call.getFlattenedModelFile();

						if (modelFile.exists() && !flattenedModelFile.exists()) {

							System.out.println(call);

							if (modelFile.getName().endsWith(".apnml")) {
								Files.copy(modelFile, flattenedModelFile);
							} else if (modelFile.getName().endsWith(".dfm")) {
								Files.copy(modelFile, flattenedModelFile);
							} else if (modelFile.getName().endsWith(".mlm")) {

								if (!Perform4Measures.isError(modelFile)) {
									FileInputStream stream = new FileInputStream(modelFile);
									MultiLevelModel model = MultiLevelModelImportPlugin.importFromStream(context,
											stream);
									AcceptingPetriNet net = MultiLevelModel2AcceptingPetriNet.convert(model,
											new Canceller() {
												public boolean isCancelled() {
													return false;
												}
											});
									net.exportToFile(new FakeContext(), flattenedModelFile);
								} else {
									Files.copy(modelFile, flattenedModelFile);
								}
							} else if (modelFile.getName().endsWith(".tree")) {
								Files.copy(modelFile, flattenedModelFile);
							} else if (modelFile.getName().endsWith(".bpmn")) {
								Files.copy(modelFile, flattenedModelFile);
							}
						}
					}
				}
			}
		}
	}
}
