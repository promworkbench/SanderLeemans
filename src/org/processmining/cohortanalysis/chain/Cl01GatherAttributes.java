package org.processmining.cohortanalysis.chain;

import java.util.Collection;
import java.util.Iterator;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.attributes.IvMVirtualAttributeFactory;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;

public class Cl01GatherAttributes extends DataChainLinkComputationAbstract<CohortsConfiguration> {

	@Override
	public String getName() {
		return "gather attributes";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Gathering attributes..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.input_log };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.attributes_info, IvMObject.classifiers, IvMObject.selected_classifier,
				IvMObject.classifier_for_gui };
	}

	public IvMObjectValues execute(CohortsConfiguration configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		XLog log = inputs.get(IvMObject.input_log);

		IvMVirtualAttributeFactory virtualAttributes = configuration.getVirtualAttributes();
		AttributesInfo info = new AttributesInfoImpl(log, virtualAttributes);
		Collection<Attribute> attributes = info.getEventAttributes();

		String[] names = new String[attributes.size()];
		Iterator<Attribute> it = attributes.iterator();
		for (int i = 0; i < names.length; i++) {
			names[i] = it.next().getName();
		}
		Pair<AttributeClassifier[], AttributeClassifier> p = AttributeClassifiers.getAttributeClassifiers(log, names,
				true);
		AttributeClassifier[] attributeClassifiers = p.getA();
		AttributeClassifier[] firstClassifier = new AttributeClassifier[] { p.getB() };

		return new IvMObjectValues().//
				s(IvMObject.attributes_info, info).//
				s(IvMObject.classifiers, attributeClassifiers).//
				s(IvMObject.selected_classifier, firstClassifier).//
				s(IvMObject.classifier_for_gui, firstClassifier);
	}
}