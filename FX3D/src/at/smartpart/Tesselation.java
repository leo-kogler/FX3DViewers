package at.smartpart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.vecmath.Vector3d;
import at.smartpart.DefaultFaceMesh;
import javafx.scene.shape.Polygon;

/*
 * @author Friedrich Geyer & Leopold Kogler
 * 09.04.2016
 * mesh2STL function transforms indexed Triangle List from OCC to STL output file
 * 
 */

public class Tesselation {


	public STEPModel tesselate(ArrayList<DefaultFaceMesh> myFaceMeshes) {

		ArrayList<Vector3d> vectorList = new ArrayList<Vector3d>();

		ArrayList<Vector3d> pointsList = new ArrayList<Vector3d>();
		ArrayList<Integer> facesList = new ArrayList<Integer>();

        ArrayList<Polygon> polygons = new ArrayList<>();
        ArrayList<Vector3d> vertices = new ArrayList<>();
		
		for (int a = 0; a < myFaceMeshes.size(); a++) {
			float[] p = myFaceMeshes.get(a).getNodes();
			int[] tri = myFaceMeshes.get(a).getMesh();

			for (int i = 0; i < myFaceMeshes.get(a).getMesh().length / 3; i++) {

				Vector3d vA;
				Vector3d vB;
				Vector3d vC;

				if (a < 3) {

					vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 * tri[3 * i + 0] + 2]);
					vB = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1], p[3 * tri[3 * i + 1] + 2]);
					vC = new Vector3d(p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1], p[3 * tri[3 * i + 2] + 2]);

					
					vectorList.add(vA);

					vectorList.add(vB);

					vectorList.add(vC);
	
					
				} else {

					vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 * tri[3 * i + 0] + 2]);
					vC = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1], p[3 * tri[3 * i + 1] + 2]);
					vB = new Vector3d(p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1], p[3 * tri[3 * i + 2] + 2]);

					vectorList.add(vA);
	
					vectorList.add(vB);
		
					vectorList.add(vC);
				
					
				}

	
				
				//Collections.reverse(vectorList);

			}

		}
		for (Vector3d vec : vectorList) {
			//if (!pointsList.contains(vec)) {
				pointsList.add(vec);
			//}

			facesList.add(pointsList.indexOf(vec));

		}
		
		
		double tempX = 0.0, tempXmin = 0.0;
		double tempY = 0.0, tempYmin = 0.0;
		double tempZ = 0.0, tempZmin = 0.0;
		
	for (Vector3d vec : pointsList) {
			System.out.println("x " + vec.x + " y " + vec.y + " z " + vec.z);
			
			if(vec.x > tempX) tempX = vec.x;
			if(vec.y > tempY) tempY = vec.y;
			if(vec.z > tempZ) tempZ = vec.z;
			
			if(vec.x < tempXmin) tempXmin = vec.x;
			if(vec.y < tempYmin) tempYmin = vec.y;
			if(vec.z < tempZmin) tempZmin = vec.z;
				
		}

	System.out.println("############");
	System.out.println("xmin " + tempXmin + " ymin " + tempYmin + " zmin " + tempZmin);
	System.out.println("xmax " + tempX + " ymax " + tempY + " zmax " + tempZ);
	System.out.println("############");
	
	
	/*	for (Integer idx : facesList) {
			System.out.println(idx);
		}
*/
		
		float[] verticesArray = new float[vectorList.size() * 3];

		int i = 0;
		for (Vector3d vec : vectorList) { //pointsList
 			verticesArray[i] = (float) vec.x;
			i++;
			verticesArray[i] = (float) vec.y;
			i++;
			verticesArray[i] = (float) vec.z;
			i++;
		}

		Integer[] triangleArr = new Integer[facesList.size()];
		triangleArr = facesList.toArray(triangleArr);

		int[] intArray = Arrays.stream(triangleArr).mapToInt(Integer::intValue).toArray();
		
		
		int[] int2Arr = new int[intArray.length * 2];
		int k = 0;
		for (int p : intArray) {  //intAray

			int2Arr[k] = p;
	///		System.out.println(p);
			k++;
			int2Arr[k] = 0;
	///		System.out.println(p);
			k++;

		}

		
		
		float[] texCoor = new float[intArray.length *2];

		for (float tc : texCoor) {
			tc = 0.0f;
		}

		return new STEPModel(verticesArray, int2Arr, texCoor);

	}

	


	
	
}
