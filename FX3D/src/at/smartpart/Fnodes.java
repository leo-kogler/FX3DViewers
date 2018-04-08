package at.smartpart;
public class Fnodes {
	
	private float[] fnodes;
	private int size;

	public Fnodes (float[] fnodes, int size) {
		this.fnodes=fnodes;
		this.size=size;
	}

	public int getSize() {

		return this.size;
		
	}

	public float[] getFnodes() {
	
		return fnodes;
	}
	

}
