package thesis.helperClasses;

import java.io.File;
import java.io.FileReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.processmining.plugins.InductiveMiner.Function;

import com.raffaeleconforti.efficientlog.XAttributeContinuousImpl;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class CSVLogParserIncremental {

	public static void parseTraces(File file, String traceIdAttribute, Function<XTrace, Object> traceCallback)
			throws Exception {

		//first, gather the last events of every trace
		String[] attributeNames;
		int traceIdColumn;
		TObjectIntMap<String> trace2lastRow;
		{
			FileReader r = new FileReader(file);
			CSVParser parser = CSVParser.parse(r, CSVFormat.RFC4180);
			Iterator<CSVRecord> it = parser.iterator();

			if (!it.hasNext()) {
				return;
			}

			//read header
			CSVRecord header = it.next();
			traceIdColumn = -1;
			attributeNames = new String[header.size()];
			for (int column = 0; column < header.size(); column++) {
				String attributeName = header.get(column);
				while (ArrayUtils.contains(attributeNames, attributeName)) {
					//CSV files might contain duplicate attributes, while XES cannot
					attributeName += "1";
				}
				attributeNames[column] = attributeName;

				if (traceIdAttribute.equals(attributeName)) {
					traceIdColumn = column;
				}
			}

			if (traceIdColumn == -1) {
				return;
			}

			trace2lastRow = new TObjectIntHashMap<>();
			int rowNr = 1;
			while (it.hasNext()) {
				CSVRecord row = it.next();
				trace2lastRow.put(row.get(traceIdColumn), rowNr);
				rowNr++;
			}

			parser.close();
			r.close();
		}

		//second, emit traces
		{
			FileReader r = new FileReader(file);
			CSVParser parser = CSVParser.parse(r, CSVFormat.RFC4180);
			Iterator<CSVRecord> it = parser.iterator();
			XFactoryNaiveImpl factory = new XFactoryNaiveImpl();

			//pass over header
			it.next();

			//read events and traces
			int rowNr = 1;
			THashMap<String, XTrace> id2trace = new THashMap<>();
			while (it.hasNext()) {
				CSVRecord row = it.next();

				String traceId = row.get(traceIdColumn);

				//create trace if it is not present yet
				XTrace trace = id2trace.get(traceId);
				if (trace == null) {
					trace = factory.createTrace();
					trace.getAttributes().put(traceIdAttribute, new XAttributeLiteralImpl(traceIdAttribute, traceId));
					id2trace.put(traceId, trace);
				}

				XEvent event = factory.createEvent();

				//parse the attributes
				for (int column = 0; column < row.size(); column++) {
					if (column != traceIdColumn && row.isSet(column)) {
						String value = row.get(column);

						//try boolean
						short valueBoolean = parseBoolean(value);
						if (valueBoolean != 0) {
							event.getAttributes().put(attributeNames[column],
									new XAttributeBooleanImpl(attributeNames[column], valueBoolean == 1));
						} else {
							//try long
							long valueLong = NumberUtils.toLong(value, Long.MIN_VALUE);
							if (valueLong != Long.MIN_VALUE) {
								event.getAttributes().put(attributeNames[column],
										new XAttributeDiscreteImpl(attributeNames[column], valueLong));
							} else {
								//try double
								double valueDouble = NumberUtils.toDouble(value, -Double.MAX_VALUE);
								if (valueDouble != -Double.MAX_VALUE) {
									event.getAttributes().put(attributeNames[column],
											new XAttributeContinuousImpl(attributeNames[column], valueDouble));
								} else {
									//try timed
									Date valueDate = parseDate(value);
									if (valueDate != null) {
										event.getAttributes().put(attributeNames[column],
												new XAttributeTimestampImpl(attributeNames[column], valueDate));
									} else {
										//literal
										event.getAttributes().put(attributeNames[column],
												new XAttributeLiteralImpl(attributeNames[column], value));
									}
								}
							}
						}
					}
				}

				trace.add(event);

				//if the trace ended, report it and delete it
				if (trace2lastRow.get(traceId) == rowNr) {
					traceCallback.call(trace);
					id2trace.remove(traceId);
				}

				rowNr++;
			}
		}
	}

	/**
	 * return values: 0 = no boolean present, 1 = true, 2 = false
	 * 
	 * @param value
	 * @return
	 */
	private static short parseBoolean(String value) {
		if (value.toLowerCase().equals("true")) {
			return 1;
		}
		if (value.toLowerCase().equals("false")) {
			return 2;
		}
		return 0;
	}

	/**
	 * Adapted from F. Mannhardt.
	 * 
	 */
	public static final Pattern INVALID_MS_PATTERN = Pattern.compile("(:[0-5][0-9]\\.[0-9]{3})[0-9]*$");
	public static final SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy-M-d H:mm:ss.SSS");

	public static Date parseDate(String value) {
		return parseDate(value, customDateFormat);
	}

	public static Date parseDate(String value, SimpleDateFormat dateFormat) {
		if (value != null && dateFormat != null) {
			ParsePosition pos = new ParsePosition(0);
			Date date = dateFormat.parse(value, pos);

			// Fix if there are more than 3 digits for ms for example 44.00.540000, do not return and
			// ensure string is formatted to 540 ms instead of 540000 ms
			if (date != null && !INVALID_MS_PATTERN.matcher(value).find()) {
				return date;
			} else {
				String fixedValue = INVALID_MS_PATTERN.matcher(value).replaceFirst("$1");
				pos.setIndex(0);
				date = dateFormat.parse(fixedValue, pos);
				if (date != null) {
					return date;
				}
			}
		}
		return null;
	}
}