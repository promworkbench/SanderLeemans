package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.util.HashMap;

class SuccSeqs {
	private HashMap ht_ss = new HashMap();

	public int getCount(String t1, String t2) {
		HashMap ht_t1 = (HashMap) this.ht_ss.get(t1);
		if (ht_t1 == null) {
			return 0;
		} else {
			Integer count = (Integer) ht_t1.get(t2);
			return count == null ? 0 : count.intValue();
		}
	}

	public void addCount(String t1, String t2) {
		HashMap ht_t1 = (HashMap) this.ht_ss.get(t1);
		if (ht_t1 == null) {
			ht_t1 = new HashMap();
			this.ht_ss.put(t1, ht_t1);
		}

		Integer count = (Integer) ht_t1.get(t2);
		if (count == null) {
			count = new Integer(0);
		}

		count = new Integer(count.intValue() + 1);
		ht_t1.put(t2, count);
	}

	public void clear() {
		this.ht_ss.clear();
	}
}