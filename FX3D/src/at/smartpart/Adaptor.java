package at.smartpart;
/* Project Info:  http://jcae.sourceforge.net
	 * 
	 * This program is free software; you can redistribute it and/or modify it under
	 * the terms of the GNU Lesser General Public License as published by the Free
	 * Software Foundation; either version 2.1 of the License, or (at your option)
	 * any later version.
	 *
	 * This program is distributed in the hope that it will be useful, but WITHOUT
	 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
	 * details.
	 *
	 * You should have received a copy of the GNU Lesser General Public License
	 * along with this program; if not, write to the Free Software Foundation, Inc.,
	 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
	 *
	 * (C) Copyright 2007, by EADS France
	 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.vecmath.Vector3d;
import org.jcae.opencascade.Utilities;
import org.jcae.opencascade.jni.*;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
/**
 * Meshing parameters were taken from Opencascade sources. See
 * <ul>
 * <li>src/AIS/AIS_TexturedShape.cxx</li>
 * <li>src/AIS/AIS_Shape.cxx</li>
 * <li>src/Prs3d/Prs3d_Drawer.cxx</li>
 * <li>src/BRepMesh/BRepMesh.cxx</li>
 * <li>inc/BRepMesh_IncrementalMesh.hxx</li>
 * </ul>
 * 
 * @author Jerome Robert
 *
 */
public class Adaptor {
	private static int meshIter = 3;

	public static ArrayList<DefaultFaceMesh> myFaceMeshes = new ArrayList<DefaultFaceMesh>();

	private static boolean debug = false;

	ArrayList<Fnodes> verticesBuffer = new ArrayList<Fnodes>();
	ArrayList<float[]> coloursBuffer = new ArrayList<float[]>();;
	ArrayList<int[]> indicesBuffer = new ArrayList<int[]>();


	ArrayList<Vector3d> pointList = new ArrayList<Vector3d>();
	ArrayList<Integer> faceList = new ArrayList<Integer>();

	

	private int counter = 0;
	private int x = 0;
	private int [][] faces;
	
	public Adaptor() {

		TopoDS_Shape shape = Utilities.readFile("data/01-51309.stp");
		TopExp_Explorer explorer = new TopExp_Explorer();
		TopLoc_Location loc = new TopLoc_Location();


		
		

		for (explorer.init(shape, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next()) {
			TopoDS_Shape s = explorer.current();
			if (!(s instanceof TopoDS_Face))
				continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face) s;
			Poly_Triangulation pt = BRep_Tool.triangulation(face, loc);

			float error = 0.001f * getMaxBound(s) * 4;
			// float error=0.0001f;
			int iter = 0;
			while ((pt == null) & (iter < meshIter)) {
				new BRepMesh_IncrementalMesh(face, error, false);
				//new BRepMesh_IncrementalMesh(face,error, true);
				pt = BRep_Tool.triangulation(face, loc);
				error /= 10;
				iter++;
			}

			counter++;


		}
		
	faces= new int[counter][6];
		
		for (explorer.init(shape, TopAbs_ShapeEnum.FACE); explorer.more(); explorer.next()) {
			TopoDS_Shape s = explorer.current();
			if (!(s instanceof TopoDS_Face))
				continue; // should not happen!
			TopoDS_Face face = (TopoDS_Face) s;
			
			if (face.orientation() == TopAbs_Orientation.REVERSED) {
				System.out.println("wrong shape");
				face.reverse();

			}
			
			Poly_Triangulation pt = BRep_Tool.triangulation(face, loc);

			float error = 0.001f * getMaxBound(s) * 4;
			// float error=0.0001f;
			int iter = 0;
			while ((pt == null) & (iter < meshIter)) {
			new BRepMesh_IncrementalMesh(face, error, false);
				// new BRepMesh_IncrementalMesh(face,error, true);
				pt = BRep_Tool.triangulation(face, loc);
				error /= 10;
				iter++;
			}
			


			if (pt == null) {
				System.err.println("Triangulation failed for face " + face + ". Trying other mesh parameters.");
				// faceMeshes.add(new DefaultFaceMesh(new float[0], new int[0]));
				continue;
	
			} else if (debug) {
				System.out.println("Triangulation succed for face " + face);
			}

			double[] dnodes = pt.nodes();
			float[] fnodes = pt.fnodes();

			final int[] itriangles = pt.triangles();
			double[] point = new double[3];
			double[] meshNormal = computeMeshNormal(dnodes, itriangles, point);
			double[] cadNormal = getNormal(point, face);

			if (scalarProduct(meshNormal, cadNormal) < 0) {
				System.out.println("reverseMesh");
				reverseMesh(itriangles);
			}
			if (face.orientation() == TopAbs_Orientation.REVERSED) {
				System.out.println("reverseMesh2");
				reverseMesh(itriangles);
			}

			double[] d2nodes = pt.nodes();
			float[] f2nodes = new float[d2nodes.length];
			for (int i = 0; i < d2nodes.length; i++) {

				f2nodes[i] = (float) d2nodes[i];// * scaleFactor;
			}
					
			
			if(loc.isIdentity())
			{
				for(int i=0; i<dnodes.length; i++)
				{
					fnodes[i]=(float) dnodes[i];
				}				
			}
			else {
				transformMesh(loc, dnodes, fnodes);
			}

			
			faces[x++]=itriangles;
			
			myFaceMeshes.add(new DefaultFaceMesh(fnodes, itriangles));

		}

	}

	void transformMesh(TopLoc_Location loc, double[] src, float[] dst)
	{
		double[] matrix=new double[16];
		loc.transformation().getValues(matrix);
		Matrix4d m4d=new Matrix4d(matrix);
		Point3d p3d=new Point3d();
		for(int i=0; i<src.length; i+=3)
		{
			p3d.x=src[i+0];
			p3d.y=src[i+1];
			p3d.z=src[i+2];
			m4d.transform(p3d);
			dst[i+0]=(float) p3d.x;
			dst[i+1]=(float) p3d.y;
			dst[i+2]=(float) p3d.z;
		}		
	}
	
	


	public ArrayList<DefaultFaceMesh> correctMesh() {

		ArrayList<DefaultFaceMesh> mySecondFaceMesh = new ArrayList<DefaultFaceMesh>();

		boolean DEBUG = false;

		int facesInd = 0;
		int pointsInd = myFaceMeshes.size() * 3;

		int[] facesArray = new int[myFaceMeshes.size() * myFaceMeshes.get(0).getMesh().length];

		for (DefaultFaceMesh fm : myFaceMeshes) {

			float[] f2nodes = fm.getNodes();
			int[] itriangles = fm.getMesh();
			int pointElementSize = f2nodes.length * myFaceMeshes.size();

			if (DEBUG)
				System.out.println(
						"face.length = " + (fm.getMesh().length / 2) + "  -- " + Arrays.toString(fm.getMesh()));
			int lastPointIndex = fm.getMesh()[fm.getMesh().length - 2];
			if (DEBUG)
				System.out.println("    lastPointIndex = " + lastPointIndex);
			for (int p = 0; p < fm.getMesh().length; p += 2) {
				int pointIndex = fm.getMesh()[p];
				if (DEBUG)
					System.out.println("        connecting point[" + lastPointIndex + "] to point[" + pointIndex + "]");
				facesArray[facesInd++] = lastPointIndex;
				facesArray[facesInd++] = 0;
				facesArray[facesInd++] = pointIndex;
				facesArray[facesInd++] = 0;
				facesArray[facesInd++] = pointsInd / pointElementSize;
				facesArray[facesInd++] = 0;
				if (DEBUG)
					System.out.println("            facesInd = " + facesInd);
				pointsInd += pointElementSize;
				lastPointIndex = pointIndex;
			}

			/*
			 * for (int j = 0 ; j < facesArray.length; j+=6) {
			 * 
			 * int [] sub = new int [6];
			 * 
			 * sub[j]=facesArray[j]; sub[j+1]=facesArray[j+1]; sub[j+2]=facesArray[j+2];
			 * sub[j+3]=facesArray[j+3]; sub[j+4]=facesArray[j+4]; sub[j+5]=facesArray[j+5];
			 * 
			 * mySecondFaceMesh.add(new DefaultFaceMesh (f2nodes, sub));
			 * 
			 * }
			 */

		}

		System.out.println("corr. Array Size : " + facesArray.length);
		return mySecondFaceMesh;

	}

	private static float getMaxBound(TopoDS_Shape shape) {
		Bnd_Box box = new Bnd_Box();
		BRepBndLib.add(shape, box);
		double[] bbox = box.get();
		double minBoundingBox = Math.max(Math.max(bbox[3] - bbox[0], bbox[4] - bbox[1]), bbox[5] - bbox[2]);
		return (float) minBoundingBox;

	}

	/**
	 * Create a Colored Shape
	 * 
	 * @param shape
	 * @param facesColors
	 * 
	 *            public OCCFaceDomain(TopoDS_Shape shape,Color[] facesColors){
	 *            this(shape); this.facesColors=facesColors; }
	 * 
	 *            /**
	 * @param itriangles
	 */
	static private void reverseMesh(int[] itriangles) {
		int tmp;
		for (int i = 0; i < itriangles.length; i += 3) {
			tmp = itriangles[i];
			itriangles[i] = itriangles[i + 1];
			itriangles[i + 1] = tmp;
		}
	}

	static private double scalarProduct(double[] v1, double[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}

	/**
	 * 
	 * @param nodes
	 *            the nodes of the mesh
	 * @param trias
	 *            the triangles of the mesh
	 * @param point
	 *            double[3] array which will receive the point were the normal is
	 *            computed
	 * @return The normal a the point <code>point</code>
	 */
	static private double[] computeMeshNormal(double[] nodes, int[] trias, double[] point) {
		double[] toReturn = new double[3];
		int index = (trias.length / 6) * 3;
		double[] v1 = new double[3];
		double[] v2 = new double[3];

		v1[0] = nodes[trias[index] * 3] - nodes[trias[index + 1] * 3];
		v1[1] = nodes[trias[index] * 3 + 1] - nodes[trias[index + 1] * 3 + 1];
		v1[2] = nodes[trias[index] * 3 + 2] - nodes[trias[index + 1] * 3 + 2];

		v2[0] = nodes[trias[index + 2] * 3] - nodes[trias[index + 1] * 3];
		v2[1] = nodes[trias[index + 2] * 3 + 1] - nodes[trias[index + 1] * 3 + 1];
		v2[2] = nodes[trias[index + 2] * 3 + 2] - nodes[trias[index + 1] * 3 + 2];

		// compute center of the triangle
		point[0] = nodes[trias[index] * 3] + nodes[trias[index] * 3 + 1] + nodes[trias[index] * 3 + 2];
		point[1] = nodes[trias[index + 1] * 3] + nodes[trias[index + 1] * 3 + 1] + nodes[trias[index + 1] * 3 + 2];
		point[2] = nodes[trias[index + 2] * 3] + nodes[trias[index + 2] * 3 + 1] + nodes[trias[index + 2] * 3 + 2];

		/*
		 * System.out.println("v1 "+v1[0]+" "+v1[1]+" "+v1[2]);
		 * System.out.println("v2 "+v2[0]+" "+v2[1]+" "+v2[2]);
		 * System.out.println("point "+point[0]+" "+point[1]+" "+point[2]);
		 */
		// compute the normal
		toReturn[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
		toReturn[1] = (v2[0] * v1[2]) - (v2[2] * v1[0]);
		toReturn[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);

		return toReturn;
	}

	static private double[] getNormal(double[] point, TopoDS_Face face) {
		Geom_Surface gs = BRep_Tool.surface(face);
		GeomLProp_SLProps myLprop = new GeomLProp_SLProps(2, 0.0001);
		myLprop.setSurface(gs);
		GeomAPI_ProjectPointOnSurf pof = new GeomAPI_ProjectPointOnSurf(point, gs);
		double[] uv = new double[2];
		pof.lowerDistanceParameters(uv);
		myLprop.setParameters(uv[0], uv[1]);
		double[] toReturn = myLprop.normal();
		if (face.orientation() == TopAbs_Orientation.REVERSED) {
			toReturn[0] *= -1;
			toReturn[1] *= -1;
			toReturn[2] *= -1;
		}
		return toReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcae.viewer3d.cad.CADDomainAdaptor#getFaceIterator()
	 * 
	 * @Override public Iterator<FaceMesh> getFaceIterator() { return
	 * faceMeshes.iterator(); } /** Return the Color of the ith Face of the iterator
	 * 
	 * @param i
	 * 
	 * @return
	 */

	public STEPModel getSTEP() throws IOException {

		boolean DEBUG = false;
		
		ArrayList<float[]> verticesBuffer = MeshUtils.mesh2STL(myFaceMeshes);

		int size = 0;
		for (float[] vbo : verticesBuffer) {
			int vboSize = vbo.length;
			size = size + vboSize;
		}

		float[] verticesArrayBuffer = new float[size];

		int j = 0;
		for (float[] vbo : verticesBuffer) {

			for (int i = 0; i < vbo.length; i++) {
				verticesArrayBuffer[j] = vbo[i]; // zomm

				// System.out.println("index= " + j + " value " + vbo[i]);// + " y " + vbo[i+1]
				// +" z "+ vbo[i+2]);
				j++;
			}

		}

		for (int count = 0; count < verticesArrayBuffer.length; count = count + 3) {
			Vector3d vec = new Vector3d(verticesArrayBuffer[count], verticesArrayBuffer[count + 1],
					verticesArrayBuffer[count + 2]);
			if (!pointList.contains(vec)) {
				pointList.add(vec);
			} else {
				// faceList.add(pointList.indexOf(vec));
			}

			faceList.add(pointList.indexOf(vec));

		}

		for (Vector3d vec : pointList) {
			if(DEBUG)System.out.println("x " + vec.x + "y " + vec.y + "z " + vec.z);
		}

		for (Integer idx : faceList) {
			if(DEBUG)System.out.println(idx);
		}

		float[] verticesArray = new float[pointList.size() * 3];

		int i = 0;
		for (Vector3d vec : pointList) {
			verticesArray[i] = (float) vec.x;
			i++;
			verticesArray[i] = (float) vec.y;
			i++;
			verticesArray[i] = (float) vec.z;
			i++;
		}

		Integer[] triangleArr = new Integer[faceList.size()];
		triangleArr = faceList.toArray(triangleArr);

		int[] intArray = Arrays.stream(triangleArr).mapToInt(Integer::intValue).toArray();

		// reverse the triplet

		int[] orderedTriangles = new int[faceList.size()];

		for (int l = 0; l < intArray.length; l = l + 3) {

			orderedTriangles[l] = intArray[l + 2];
			orderedTriangles[l + 1] = intArray[l + 1];
			orderedTriangles[l + 2] = intArray[l];

			System.out.println("listA:" + orderedTriangles[l] + " listB: " + intArray[l + 2]);
			System.out.println("listA:" + orderedTriangles[l + 1] + " listB: " + intArray[l + 1]);
			System.out.println("listA:" + orderedTriangles[l + 2] + " listB: " + intArray[l]);

		}

		int[] int2Arr = new int[intArray.length * 2];
		int k = 0;
		for (int p : orderedTriangles) { // intArray

			int2Arr[k] = p;
			/* System.out.println(p); */ k++;
			int2Arr[k] = 0;
			/* System.out.println(p); */ k++;

		}

		float[] texCoor = new float[verticesArray.length / 3];

		for (float tc : texCoor) {
			tc = 0.0f;
		}

		return new STEPModel(verticesArray, int2Arr, texCoor);

	}

	public ArrayList<DefaultFaceMesh> getMeshList() {

		return myFaceMeshes;
	}






	public int[][] getIntArr() {

		return faces;
	}

}
