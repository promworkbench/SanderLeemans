package org.processmining.cohortanalysis.chain;

import java.util.ArrayList;
import java.util.Arrays;

import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputer;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputerImpl;
import org.processmining.plugins.inductiveVisualMiner.attributes.IvMVirtualAttributeFactory;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceDuration;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceFitness;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceLength;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfCompleteEvents;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfLogMoves;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfModelMoves;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeVirtual;

import gnu.trove.map.hash.THashMap;

public class CohortsConfiguration {

	private final AlignmentComputer alignmentComputer = new AlignmentComputerImpl();

	public IvMVirtualAttributeFactory getVirtualAttributes() {
		return new IvMVirtualAttributeFactory() {
			public Iterable<AttributeVirtual> createVirtualTraceAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						new VirtualAttributeTraceDuration(), //
						new VirtualAttributeTraceLength(), //
				}));
			}

			public Iterable<AttributeVirtual> createVirtualEventAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						//
				}));
			}

			public Iterable<AttributeVirtual> createVirtualIvMTraceAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						new VirtualAttributeTraceNumberOfCompleteEvents(), //
						new VirtualAttributeTraceFitness(), //
						new VirtualAttributeTraceNumberOfModelMoves(), //
						new VirtualAttributeTraceNumberOfLogMoves(), //
				}));
			}

			public Iterable<AttributeVirtual> createVirtualIvMEventAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						//
				}));
			}
		};
	}

	protected AlignmentComputer getAlignmentComputer() {
		return alignmentComputer;
	}
}
