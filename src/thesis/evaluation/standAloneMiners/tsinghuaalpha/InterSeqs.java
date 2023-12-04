package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.util.HashMap;

class InterSeqs {
	private HashMap ht_is = new HashMap();

	public int getCount(String t1, String t2) {
		String small = t1.compareTo(t2) > 0 ? t2 : t1;
		String big = t1.compareTo(t2) > 0 ? t1 : t2;
		HashMap ht_small = (HashMap) this.ht_is.get(small);
		if (ht_small == null) {
			return 0;
		} else {
			Integer count = (Integer) ht_small.get(big);
			return count == null ? 0 : count.intValue();
		}
	}

	public void addCount(String t1, String t2) {
		String small = t1.compareTo(t2) > 0 ? t2 : t1;
		String big = t1.compareTo(t2) > 0 ? t1 : t2;
		HashMap ht_small = (HashMap) this.ht_is.get(small);
		if (ht_small == null) {
			ht_small = new HashMap();
			this.ht_is.put(small, ht_small);
		}

		Integer count = (Integer) ht_small.get(big);
		if (count == null) {
			count = new Integer(0);
		}

		count = new Integer(count.intValue() + 1);
		ht_small.put(big, count);
	}

	public void clear() {
		this.ht_is.clear();
	}
}