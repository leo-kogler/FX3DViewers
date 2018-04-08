package at.smartpart;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector3d;

/*
 * @author Friedrich Geyer & Leopold Kogler
 * 09.04.2016
 * mesh2STL function transforms indexed Triangle List from OCC to STL output file
 * 
 */

public class MeshList {
	

	public List<Vector3d> mesh2List(ArrayList<DefaultFaceMesh> myFaceMeshes) {

		List<Vector3d> list = new ArrayList<Vector3d>();

		for (int a = 0; a < myFaceMeshes.size(); a++) {
			float[] p = myFaceMeshes.get(a).getNodes();
			int[] tri = myFaceMeshes.get(a).getMesh();

			for (int i = 0; i < myFaceMeshes.get(a).getMesh().length / 3; i++) {

				Vector3d vA;
				Vector3d vB;
				Vector3d vC;
				
				if ( a > 0){
					
					
					vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1],
							p[3 * tri[3 * i + 0] + 2]);
					vB = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1],
							p[3 * tri[3 * i + 1] + 2]);
					vC = new Vector3d(p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1],
							p[3 * tri[3 * i + 2] + 2]);
					
					
				}else {
				
				vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1],
						p[3 * tri[3 * i + 0] + 2]);
				vC = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1],
						p[3 * tri[3 * i + 1] + 2]);
				vB = new Vector3d(p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1],
						p[3 * tri[3 * i + 2] + 2]);
				}

			/*	Vector3d vSubBA = new Vector3d();
				vSubBA.sub(vB, vA);
				Vector3d vSubCA = new Vector3d();
				vSubCA.sub(vC, vA);

				Vector3d vBAxCA = new Vector3d();
				vBAxCA.cross(vSubBA, vSubCA);

				Vector3d vN = new Vector3d();
				vN.normalize(vBAxCA);

		*/	
				
				list.add(vA);
				list.add(vB);
				list.add(vC);
				

			}
			
		}
		//Collections.reverse(list);
	return list;

}
}