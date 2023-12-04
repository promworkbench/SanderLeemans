package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.util.ArrayList;

class PredSuccUtil {
	private OrderRelation or;

	public PredSuccUtil(OrderRelation or) {
		this.or = or;
	}

	public int compare(PredSucc2 ps1, PredSucc2 ps2) {
		return ps1.predecessor.size() >= ps2.predecessor.size() && ps1.predecessor.containsAll(ps2.predecessor)
				? (ps1.successor.size() >= ps2.successor.size() && ps1.successor.containsAll(ps2.successor)
						? (ps1.predecessor.size() == ps2.predecessor.size()
								&& ps1.successor.size() == ps2.successor.size() ? -1 : 1)
						: (ps2.predecessor.size() == ps1.predecessor.size()
								&& ps2.successor.size() > ps1.successor.size()
								&& ps2.successor.containsAll(ps1.successor) ? -1
										: (!this.canMerge(ps1.successor, ps2.successor) ? 2
												: (this.canPredSucc(ps1.predecessor, ps2.successor) ? 0 : 2))))
				: (ps2.predecessor.size() >= ps1.predecessor.size() && ps2.predecessor.containsAll(ps1.predecessor)
						? (ps2.successor.size() >= ps1.successor.size() && ps2.successor.containsAll(ps1.successor) ? -1
								: (!this.canMerge(ps2.successor, ps1.successor) ? 2
										: (this.canPredSucc(ps2.predecessor, ps1.successor) ? 0 : 2)))
						: (!this.canMerge(ps1.predecessor, ps2.predecessor) ? 2
								: (!this.canMerge(ps1.successor, ps2.successor) ? 2
										: (!this.canPredSucc(ps1.predecessor, ps2.successor) ? 2
												: (this.canPredSucc(ps2.predecessor, ps1.successor) ? 0 : 2)))));
	}

	public boolean canPredSucc(ArrayList al1, ArrayList al2) {
		for (int i = 0; i < al1.size(); ++i) {
			String t1 = (String) al1.get(i);

			for (int j = 0; j < al2.size(); ++j) {
				String t2 = (String) al2.get(j);
				if (this.or.getRel(t1, t2) != 1) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean canMerge(ArrayList al1, ArrayList al2) {
		for (int i = 0; i < al1.size(); ++i) {
			String t1 = (String) al1.get(i);

			for (int j = 0; j < al2.size(); ++j) {
				String t2 = (String) al2.get(j);
				if (this.or.getRel(t1, t2) == 2) {
					return false;
				}
			}
		}

		return true;
	}

	public static PredSucc2 merge(PredSucc2 ps1, PredSucc2 ps2) {
		PredSucc2 ps = new PredSucc2();
		ps.predecessor.addAll(ps1.predecessor);

		int i;
		Object o;
		for (i = 0; i < ps2.predecessor.size(); ++i) {
			o = ps2.predecessor.get(i);
			if (!ps.predecessor.contains(o)) {
				ps.predecessor.add(o);
			}
		}

		ps.successor.addAll(ps1.successor);

		for (i = 0; i < ps2.successor.size(); ++i) {
			o = ps2.successor.get(i);
			if (!ps.successor.contains(o)) {
				ps.successor.add(o);
			}
		}

		return ps;
	}

	public boolean isContained(PredSucc2 ps, ArrayList al_ps) {
		boolean isContained = false;

		for (int m = 0; m < al_ps.size(); ++m) {
			if (this.compare(ps, (PredSucc2) al_ps.get(m)) <= 0) {
				isContained = true;
				break;
			}
		}

		return isContained;
	}
}