package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.util.HashMap;

class OrderRelation {
	private HashMap ht_or = new HashMap();

	public int getRel(String t1, String t2) {
		HashMap ht_t1 = (HashMap) this.ht_or.get(t1);
		if (ht_t1 == null) {
			return 0;
		} else {
			Integer count = (Integer) ht_t1.get(t2);
			return count == null ? 0 : count.intValue();
		}
	}

	public void setRel(String t1, String t2, int n) {
		HashMap ht_t1 = (HashMap) this.ht_or.get(t1);
		if (ht_t1 == null) {
			ht_t1 = new HashMap();
			this.ht_or.put(t1, ht_t1);
		}

		Integer count = new Integer(n);
		ht_t1.put(t2, count);
	}

	public void clear() {
		this.ht_or.clear();
	}
}