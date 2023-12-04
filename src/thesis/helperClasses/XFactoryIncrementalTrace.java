package thesis.helperClasses;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.processmining.plugins.InductiveMiner.Function;

public class XFactoryIncrementalTrace extends XFactoryNaiveImpl {

	private final Function<XTrace, Object> traceCallback;

	/**
	 * The callback is called whenever a trace is parsed. The XLogs that result
	 * from this factory are useless.
	 * 
	 * @param traceCallback
	 */
	public XFactoryIncrementalTrace(Function<XTrace, Object> traceCallback) {
		this.traceCallback = traceCallback;
	}

	@Override
	public XLog createLog() {
		return new XLogNonStoring();
	}

	private class XLogNonStoring implements XLog {

		public XAttributeMap getAttributes() {
			return new XAttributeMapImpl();
		}

		public void setAttributes(XAttributeMap attributes) {

		}

		public boolean hasAttributes() {
			return false;
		}

		public Set<XExtension> getExtensions() {
			return new THashSet<>();
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

		public Iterator<XTrace> iterator() {
			return null;
		}

		public Object[] toArray() {
			return null;
		}

		public <T> T[] toArray(T[] a) {
			return null;
		}

		public boolean add(XTrace e) {
			if (traceCallback != null) {
				try {
					traceCallback.call(e);
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

		public boolean addAll(Collection<? extends XTrace> c) {
			for (XTrace trace : c) {
				add(trace);
			}
			return true;
		}

		public boolean addAll(int index, Collection<? extends XTrace> c) {
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

		public XTrace get(int index) {
			return null;
		}

		public XTrace set(int index, XTrace element) {
			return null;
		}

		public void add(int index, XTrace element) {

		}

		public XTrace remove(int index) {
			return null;
		}

		public int indexOf(Object o) {
			return 0;
		}

		public int lastIndexOf(Object o) {
			return 0;
		}

		public ListIterator<XTrace> listIterator() {
			return null;
		}

		public ListIterator<XTrace> listIterator(int index) {
			return null;
		}

		public List<XTrace> subList(int fromIndex, int toIndex) {
			return null;
		}

		public List<XEventClassifier> getClassifiers() {
			return new ArrayList<>();
		}

		public List<XAttribute> getGlobalTraceAttributes() {
			return new ArrayList<>();
		}

		public List<XAttribute> getGlobalEventAttributes() {
			return new ArrayList<>();
		}

		public boolean accept(XVisitor visitor) {
			return false;
		}

		public XLogInfo getInfo(XEventClassifier classifier) {
			return null;
		}

		public void setInfo(XEventClassifier classifier, XLogInfo info) {

		}

		public Object clone() {
			return null;
		}
	}
}
