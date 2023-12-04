package crc;

public class State {
	public double coarse;
	public double middle;
	public double fine;
	
	public String toString() {
		return "sum " + (coarse + middle + fine);
	}
}
