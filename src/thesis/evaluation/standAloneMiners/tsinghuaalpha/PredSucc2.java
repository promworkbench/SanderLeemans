package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.util.ArrayList;

class PredSucc2 {
	public ArrayList predecessor;
	public ArrayList successor;

	public PredSucc2() {
		this.predecessor = new ArrayList();
		this.successor = new ArrayList();
	}

	public PredSucc2(ArrayList pred) {
		this.predecessor = new ArrayList(pred);
		this.successor = new ArrayList();
	}

	public boolean equals(PredSucc2 ps) {
		return this.predecessor.size() == ps.predecessor.size() && this.predecessor.containsAll(ps.predecessor)
				&& this.successor.size() == ps.successor.size() && this.successor.containsAll(ps.successor);
	}

	public boolean equals(Object o) {
		PredSucc2 ps = (PredSucc2) o;
		return this.predecessor.size() == ps.predecessor.size() && this.predecessor.containsAll(ps.predecessor)
				&& this.successor.size() == ps.successor.size() && this.successor.containsAll(ps.successor);
	}
}