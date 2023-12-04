package svn41statistics;

public enum StepType {
	linear {
		int step(int current, int step) {
			return current + step;
		}
	},
	exponential {
		int step(int current, int step) {
			return current * step;
		}
	};

	abstract int step(int current, int step);
}