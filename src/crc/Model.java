package crc;

public class Model {

	public void updateProduct(State oldState, Product product, ModelParameters parameters) {
//		product.coarse = oldState.coarse * parameters.getProductCoarse();
//		product.middle = oldState.middle * (1 - parameters.getProductCoarse()) * parameters.getProductMiddleFine();
//		product.fine = oldState.fine * (1 - parameters.getProductCoarse()) * (1 - parameters.getProductMiddleFine());
		product.coarse = oldState.coarse;
		product.middle = oldState.middle;
		product.fine = oldState.fine;
	}

	public State newState(State oldState, ModelParameters parameters, Product newProduct, DataIn dataIn) {
		State newState = new State();

		//coarse
		newState.coarse =
				//content
				oldState.coarse
						//feed 
						+ dataIn.coarseIn
						//grinding down
						- oldState.coarse * parameters.getCoarseMiddleGrinding()
						//product
						- newProduct.coarse;
		//middle
		newState.middle =
				//content
				oldState.middle
						//feed
						+ dataIn.middleIn
						//grinding down
						+ oldState.coarse * parameters.getCoarseMiddleGrinding()
						//grinding down
						- oldState.middle * parameters.getMiddleFineGrinding()
						//product
						- newProduct.middle;

		//fine
		newState.fine =
				//content
				oldState.fine
						//feed
						+ dataIn.fineIn
						//grinding down
						+ oldState.middle * parameters.getMiddleFineGrinding()
						//product
						- newProduct.fine;

		return newState;
	}

}
