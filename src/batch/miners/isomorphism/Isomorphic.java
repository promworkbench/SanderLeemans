package batch.miners.isomorphism;

import batch.stability.batchStability.PetrinetWithInitialMarking;

public interface Isomorphic {
	public abstract boolean isIsomorphic(PetrinetWithInitialMarking a, PetrinetWithInitialMarking b);
}
