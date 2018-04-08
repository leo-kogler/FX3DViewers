package at.smartpart;

public class STEPModel {
	
	private float[] vertices;
	private int[] triangles;
	private float[] texCoor;
	private int[][] faces;

	public STEPModel(float[] vertices, int[] triangles, float[] texCoor) {
		this.vertices = vertices;
		this.triangles=triangles;
		this.texCoor=texCoor;
		//this.faces = faces;
		
	}

	public float[] getNodes() {

		return this.vertices;
	}

	public int[] getFaces() {

		return triangles;
	}
	
	
	public float[] getTexCoor() {

		return texCoor;
	}
	
	public int[][] getIntArr() {

		return faces;
	}

}
