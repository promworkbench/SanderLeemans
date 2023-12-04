package svn48healthcare;

import static org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTrace.c;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.deckfour.xes.model.XTrace;
import org.math.plot.utils.Array;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceDuration;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceLength;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType.Type;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTrace.Field;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.Histogram;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorDefault;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.plugins.inductiveminer2.attributes.AttributeVirtual;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;
import org.processmining.plugins.inductiveminer2.attributes.VirtualAttributeFactory;

import gnu.trove.map.hash.THashMap;

public class TraceAttributes {

	private final static IvMDecoratorI decorator = new IvMDecoratorDefault();

	private static VirtualAttributeFactory attributeFactory = new VirtualAttributeFactory() {
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
	};

	public static List<DataRow<Object>> createAttributeData(Collection<XTrace> log, IvMCanceller canceller) {
		List<DataRow<Object>> result = new ArrayList<>();

		Collection<Attribute> attributes = new AttributesInfoImpl(log, attributeFactory).getTraceAttributes();
		for (Attribute attribute : attributes) {
			result.addAll(createAttributeData(log, attribute, log.size(), canceller));
		}

		return result;
	}

	public static List<DataRow<Object>> createAttributeData(Iterable<XTrace> logFiltered, Attribute attribute,
			int numberOfTraces, IvMCanceller canceller) {
		if (attribute.isNumeric()) {
			return createAttributeDataNumeric(logFiltered, attribute, numberOfTraces, canceller);
		} else if (attribute.isBoolean()) {
			return createAttributeDataBoolean(logFiltered, attribute, canceller);
		} else if (attribute.isTime()) {
			return createAttributeDataTime(logFiltered, attribute, numberOfTraces, canceller);
		} else if (attribute.isLiteral()) {
			return createAttributeDataLiteral(logFiltered, attribute, numberOfTraces, canceller);
		} else if (attribute.isDuration()) {
			return createAttributeDataDuration(logFiltered, attribute, numberOfTraces, canceller);
		}

		List<DataRow<Object>> result = new ArrayList<>();
		result.add(new DataRow<Object>(DisplayType.literal("[not supported]"), attribute.getName(), ""));
		return result;
	}

	private static List<DataRow<Object>> createAttributeDataNumeric(Iterable<XTrace> logFiltered, Attribute attribute,
			int numberOfTraces, IvMCanceller canceller) {
		Type attributeType = DisplayType.fromAttribute(attribute);

		List<DataRow<Object>> result = new ArrayList<>();

		//compute correlation and plots
		double[] valuesFiltered;
		{
			double[] values = new double[numberOfTraces];
			int i = 0;
			for (Iterator<XTrace> it = logFiltered.iterator(); it.hasNext();) {
				XTrace trace = it.next();
				double value = AttributeUtils.valueDouble(attribute, trace);

				//store the value
				values[i] = value;

				i++;
			}

			if (canceller.isCancelled()) {
				return result;
			}

			//filter missing values
			valuesFiltered = Correlation.filterMissingValues(values);
		}

		//we assume we always have a fitness value, so we can use the filtered lists

		if (canceller.isCancelled()) {
			return result;
		}

		result.add(c(attribute, Field.tracesWithAttribute, DisplayType.numeric(valuesFiltered.length)));

		//if the list is empty, better fail now and do not attempt the rest
		if (valuesFiltered.length == 0) {
			result.add(c(attribute, Field.min, DisplayType.NA()));
			result.add(c(attribute, Field.average, DisplayType.NA()));
			result.add(c(attribute, Field.median, DisplayType.NA()));
			result.add(c(attribute, Field.max, DisplayType.NA()));
			result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
		} else {
			double min = Array.min(valuesFiltered);
			result.add(c(attribute, Field.min, DisplayType.create(attributeType, min)));

			if (canceller.isCancelled()) {
				return result;
			}

			BigDecimal valuesAverage = Correlation.mean(valuesFiltered);
			assert valuesAverage != null;
			result.add(c(attribute, Field.average, DisplayType.create(attributeType, valuesAverage.doubleValue())));

			if (canceller.isCancelled()) {
				return result;
			}

			result.add(
					c(attribute, Field.median, DisplayType.create(attributeType, Correlation.median(valuesFiltered))));

			if (canceller.isCancelled()) {
				return result;
			}

			double max = Array.max(valuesFiltered);
			result.add(c(attribute, Field.max, DisplayType.create(attributeType, max)));

			if (canceller.isCancelled()) {
				return result;
			}

			if (min == max) {
				result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
			} else {
				double standardDeviation = Correlation.standardDeviation(valuesFiltered, valuesAverage);
				result.add(c(attribute, Field.standardDeviation, DisplayType.create(attributeType, standardDeviation)));

				if (canceller.isCancelled()) {
					return result;
				}
			}

			//create histogram
			BufferedImage image = Histogram.create(valuesFiltered, DisplayType.fromAttribute(attribute), null, false,
					decorator);
			result.add(new DataRow<Object>(DisplayType.image(image), attribute.getName(), "histogram"));
		}

		return result;
	}

	private static List<DataRow<Object>> createAttributeDataBoolean(Iterable<XTrace> logFiltered, Attribute attribute,
			IvMCanceller canceller) {
		List<DataRow<Object>> result = new ArrayList<>();

		//compute correlation and plots
		int countTrue = 0;
		int countFalse = 0;
		{
			for (Iterator<XTrace> it = logFiltered.iterator(); it.hasNext();) {
				XTrace trace = it.next();
				Boolean value = AttributeUtils.valueBoolean(attribute, trace);

				if (value != null) {
					if (value) {
						countTrue++;
					} else {
						countFalse++;
					}
				}
			}

			if (canceller.isCancelled()) {
				return result;
			}
		}

		result.add(c(attribute, Field.tracesWithAttribute, DisplayType.numeric(countTrue + countFalse)));
		result.add(c(attribute, Field.tracesWithValueTrue, DisplayType.numeric(countTrue)));
		result.add(c(attribute, Field.tracesWithValueFalse, DisplayType.numeric(countFalse)));

		return result;
	}

	private static List<DataRow<Object>> createAttributeDataTime(Iterable<XTrace> logFiltered, Attribute attribute,
			int numberOfTraces, IvMCanceller canceller) {
		Type attributeType = Type.time;

		List<DataRow<Object>> result = new ArrayList<>();

		//compute correlation and plots
		long[] valuesFiltered;
		{
			long[] values = new long[numberOfTraces];
			int i = 0;
			for (Iterator<XTrace> it = logFiltered.iterator(); it.hasNext();) {
				XTrace trace = it.next();
				long value = AttributeUtils.valueLong(attribute, trace);

				//store the value
				values[i] = value;

				i++;
			}

			if (canceller.isCancelled()) {
				return result;
			}

			//filter missing values
			valuesFiltered = Correlation.filterMissingValues(values);
		}

		//we assume we always have a fitness value, so we can use the filtered lists

		if (canceller.isCancelled()) {
			return result;
		}

		result.add(c(attribute, Field.tracesWithAttribute, DisplayType.numeric(valuesFiltered.length)));

		//if the list is empty, better fail now and do not attempt the rest
		if (valuesFiltered.length == 0) {
			result.add(c(attribute, Field.min, DisplayType.NA()));
			result.add(c(attribute, Field.average, DisplayType.NA()));
			result.add(c(attribute, Field.median, DisplayType.NA()));
			result.add(c(attribute, Field.max, DisplayType.NA()));
			result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
		} else {
			long min = NumberUtils.min(valuesFiltered);
			result.add(c(attribute, Field.min, DisplayType.create(attributeType, min)));

			if (canceller.isCancelled()) {
				return result;
			}

			BigDecimal valuesAverage = Correlation.mean(valuesFiltered);
			result.add(c(attribute, Field.average, DisplayType.create(attributeType, valuesAverage.longValue())));

			if (canceller.isCancelled()) {
				return result;
			}

			result.add(c(attribute, Field.median,
					DisplayType.create(attributeType, Math.round(Correlation.median(valuesFiltered)))));

			if (canceller.isCancelled()) {
				return result;
			}

			long max = NumberUtils.max(valuesFiltered);
			result.add(c(attribute, Field.max, DisplayType.create(attributeType, max)));

			if (canceller.isCancelled()) {
				return result;
			}

			if (min == max) {
				result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
			} else {
				double standardDeviation = Correlation.standardDeviation(valuesFiltered, valuesAverage);
				result.add(c(attribute, Field.standardDeviation,
						DisplayType.create(Type.duration, Math.round(standardDeviation))));

				if (canceller.isCancelled()) {
					return result;
				}
			}
		}

		return result;
	}

	private static List<DataRow<Object>> createAttributeDataLiteral(Iterable<XTrace> logFiltered, Attribute attribute,
			int numberOfTraces, IvMCanceller canceller) {
		assert !attribute.isVirtual();

		List<DataRow<Object>> result = new ArrayList<>();

		int numberOfTracesWithAttribute = 0;
		{
			for (Iterator<XTrace> it = logFiltered.iterator(); it.hasNext();) {
				XTrace trace = it.next();

				if (trace.getAttributes().containsKey(attribute.getName())) {
					numberOfTracesWithAttribute++;
				}
			}
		}

		if (canceller.isCancelled()) {
			return result;
		}

		result.add(c(attribute, Field.tracesWithAttribute, DisplayType.numeric(numberOfTracesWithAttribute)));

		ArrayList<String> valueSet = new ArrayList<>(attribute.getStringValues());
		result.add(c(attribute, Field.numberOfDifferentValues, DisplayType.numeric(valueSet.size())));

		if (valueSet.isEmpty()) {
			result.add(c(attribute, Field.first, DisplayType.NA()));
			result.add(c(attribute, Field.last, DisplayType.NA()));
		} else {
			int first = 0;
			int last = 0;
			for (int i = 1; i < valueSet.size(); i++) {
				if (valueSet.get(first).toLowerCase().compareTo(valueSet.get(i).toLowerCase()) > 0) {
					first = i;
				} else if (valueSet.get(last).toLowerCase().compareTo(valueSet.get(i).toLowerCase()) < 0) {
					last = i;
				}
			}
			result.add(c(attribute, Field.first, DisplayType.literal(valueSet.get(first))));
			result.add(c(attribute, Field.last, DisplayType.literal(valueSet.get(last))));
		}

		return result;
	}

	private static List<DataRow<Object>> createAttributeDataDuration(Iterable<XTrace> logFiltered, Attribute attribute,
			int numberOfTraces, IvMCanceller canceller) {
		List<DataRow<Object>> result = new ArrayList<>();

		Type attributeType = Type.duration;

		//compute correlation and plots
		long[] valuesFiltered;
		{
			long[] values = new long[numberOfTraces];
			int i = 0;
			for (Iterator<XTrace> it = logFiltered.iterator(); it.hasNext();) {
				XTrace trace = it.next();
				long value = AttributeUtils.valueLong(attribute, trace);

				//store the value
				values[i] = value;

				i++;
			}

			if (canceller.isCancelled()) {
				return result;
			}

			//filter missing values
			valuesFiltered = Correlation.filterMissingValues(values);
		}

		//we assume we always have a fitness value, so we can use the filtered lists

		if (canceller.isCancelled()) {
			return result;
		}

		result.add(c(attribute, Field.tracesWithAttribute, DisplayType.numeric(valuesFiltered.length)));

		//if the list is empty, better fail now and do not attempt the rest
		if (valuesFiltered.length == 0) {
			result.add(c(attribute, Field.min, DisplayType.NA()));
			result.add(c(attribute, Field.average, DisplayType.NA()));
			result.add(c(attribute, Field.median, DisplayType.NA()));
			result.add(c(attribute, Field.max, DisplayType.NA()));
			result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
		} else {
			long min = NumberUtils.min(valuesFiltered);
			result.add(c(attribute, Field.min, DisplayType.create(attributeType, min)));

			if (canceller.isCancelled()) {
				return result;
			}

			BigDecimal valuesAverage = Correlation.mean(valuesFiltered);
			result.add(c(attribute, Field.average, DisplayType.create(attributeType, valuesAverage.longValue())));

			if (canceller.isCancelled()) {
				return result;
			}

			result.add(c(attribute, Field.median,
					DisplayType.create(attributeType, Math.round(Correlation.median(valuesFiltered)))));

			if (canceller.isCancelled()) {
				return result;
			}

			long max = NumberUtils.max(valuesFiltered);
			result.add(c(attribute, Field.max, DisplayType.create(attributeType, max)));

			if (canceller.isCancelled()) {
				return result;
			}

			if (min == max) {
				result.add(c(attribute, Field.standardDeviation, DisplayType.NA()));
			} else {
				double standardDeviation = Correlation.standardDeviation(valuesFiltered, valuesAverage);
				result.add(c(attribute, Field.standardDeviation,
						DisplayType.create(attributeType, Math.round(standardDeviation))));
			}
		}

		return result;
	}
}
