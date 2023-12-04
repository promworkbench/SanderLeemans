package thesis.helperClasses;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.processmining.plugins.InductiveMiner.Function;

public class XFactoryIncrementalEvent extends XFactoryIncrementalTrace {

	private final Function<XEvent, Object> eventCallback;

	/**
	 * The callback is called whenever a trace is parsed. The XLogs that result
	 * from this factory are useless.
	 * 
	 * @param traceCallback
	 *            This callback is called every time a trace is parsed. The
	 *            trace itself would not contain any information, thus is not
	 *            returned.
	 */
	public XFactoryIncrementalEvent(Function<XEvent, Object> eventCallback, final Runnable traceCallback) {
		super(traceCallback != null ? new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {
				traceCallback.run();
				return null;
			}
		} : null);
		this.eventCallback = eventCallback;
	}

	@Override
	public XTrace createTrace() {
		return new XTraceNonStoring();
	}

	private class XTraceNonStoring implements XTrace {

		public XAttributeMap getAttributes() {
			return new XAttributeMapImpl();
		}

		public void setAttributes(XAttributeMap attributes) {

		}

		public boolean hasAttributes() {
			return false;
		}

		public Set<XExtension> getExtensions() {
			return null;
		}

		public int size() {
			return 0;
		}

		public boolean isEmpty() {
			return false;
		}

		public boolean contains(Object o) {
			return false;
		}

		public Iterator<XEvent> iterator() {
			return null;
		}

		public Object[] toArray() {
			return null;
		}

		public <T> T[] toArray(T[] a) {
			return null;
		}

		public boolean add(XEvent e) {
			if (eventCallback != null) {
				try {
					eventCallback.call(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			return false;
		}

		public boolean remove(Object o) {
			return false;
		}

		public boolean containsAll(Collection<?> c) {
			return false;
		}

		public boolean addAll(Collection<? extends XEvent> c) {
			for (XEvent e : c) {
				add(e);
			}
			return false;
		}

		public boolean addAll(int index, Collection<? extends XEvent> c) {
			return addAll(c);
		}

		public boolean removeAll(Collection<?> c) {
			return false;
		}

		public boolean retainAll(Collection<?> c) {
			return false;
		}

		public void clear() {

		}

		public XEvent get(int index) {
			return null;
		}

		public XEvent set(int index, XEvent element) {
			return null;
		}

		public void add(int index, XEvent element) {
			add(element);
		}

		public XEvent remove(int index) {
			return null;
		}

		public int indexOf(Object o) {
			return 0;
		}

		public int lastIndexOf(Object o) {
			return 0;
		}

		public ListIterator<XEvent> listIterator() {
			return null;
		}

		public ListIterator<XEvent> listIterator(int index) {
			return null;
		}

		public List<XEvent> subList(int fromIndex, int toIndex) {
			return null;
		}

		public int insertOrdered(XEvent event) {
			return 0;
		}

		public void accept(XVisitor visitor, XLog log) {

		}

		public Object clone() {
			return null;
		}

	}
}
