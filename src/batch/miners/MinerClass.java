package batch.miners;

import batch.miners.isomorphism.Isomorphic;


public class MinerClass {
	public final Class<? extends Miner> classs;
	public final String identification;
	public final Class<? extends Isomorphic> isoCheck;
	
	public MinerClass(Class<? extends Miner> classs, String identification, Class<? extends Isomorphic> isoCheck) {
		this.classs = classs;
		this.identification = identification;
		this.isoCheck = isoCheck;
	}

	public Miner newInstantation() {
		try {
			return classs.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public Isomorphic newIsoCheckInstantation() {
		try {
			return isoCheck.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
