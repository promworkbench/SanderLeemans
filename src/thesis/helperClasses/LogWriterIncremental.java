package thesis.helperClasses;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import gnu.trove.iterator.TIntIterator;

public interface LogWriterIncremental {

	public void writeTrace(XTrace trace);

	public void writeTrace(int[] trace, String[] int2activity);

	public void writeTrace(TIntIterator iterator, String[] int2activity);

	public void startTrace();

	public void endTrace();

	public void writeEvent(String name, String lifeCycle);

	public void writeEvent(XEvent event);

	public void close();
}
