package at.smartpart;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.vecmath.Vector3d;
import at.smartpart.DefaultFaceMesh;

/*
 * @author Friedrich Geyer & Leopold Kogler
 * 09.04.2016
 * mesh2STL function transforms indexed Triangle List from OCC to STL output file
 * 
 */

public class MeshUtils {

	public static ArrayList<float[]> mesh2STL(ArrayList<DefaultFaceMesh> myFaceMeshes) throws IOException {

		ArrayList<float[]> vectorList = new ArrayList<float[]>();

		for (int a = 0; a < myFaceMeshes.size(); a++) {
			float[] p = myFaceMeshes.get(a).getNodes();
			int[] tri = myFaceMeshes.get(a).getMesh();

			for (int i = 0; i < myFaceMeshes.get(a).getMesh().length / 3; i++) {

				float[] A;
				float[] B;
				float[] C;

				
				// if ( a > 0){
				 
				/* 
				 vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 *
				 tri[3 * i + 0] + 2]);
				 
				
				 
				 vB = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1], p[3 *
				 tri[3 * i + 1] + 2]);
				 

				 
				 
				 vC = new Vector3d(p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1], p[3 *
				 tri[3 * i + 2] + 2]);
				
				*/
				
				 A = new float[] {p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 *tri[3 * i + 0] + 2]};
				 
				 B = new float[] {p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1], p[3 *tri[3 * i + 1] + 2]};
				 
				 C = new float [] {p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1],p[3 *tri[3 * i + 2] + 2]};
				 
				/* 
				 * 
				 * }else {
				 * 
				 * vA = new Vector3d(p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 *
				 * tri[3 * i + 0] + 2]); vC = new Vector3d(p[3 * tri[3 * i + 1] + 0], p[3 *
				 * tri[3 * i + 1] + 1], p[3 * tri[3 * i + 1] + 2]); vB = new Vector3d(p[3 *
				 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1], p[3 * tri[3 * i + 2] + 2]);
				 * 
				 */
				//A = new float[] { p[3 * tri[3 * i + 0] + 0], p[3 * tri[3 * i + 0] + 1], p[3 * tri[3 * i + 0] + 2] };
				//C = new float[] { p[3 * tri[3 * i + 1] + 0], p[3 * tri[3 * i + 1] + 1], p[3 * tri[3 * i + 1] + 2] };
				//B = new float[] { p[3 * tri[3 * i + 2] + 0], p[3 * tri[3 * i + 2] + 1], p[3 * tri[3 * i + 2] + 2] };

				// }
				/*
				 * Vector3d vSubBA = new Vector3d(); vSubBA.sub(vB, vA); Vector3d vSubCA = new
				 * Vector3d(); vSubCA.sub(vC, vA);
				 * 
				 * Vector3d vBAxCA = new Vector3d(); vBAxCA.cross(vSubBA, vSubCA);
				 * 
				 * Vector3d vN = new Vector3d(); vN.normalize(vBAxCA);
				 * 
				 * /*
				 * sb.append("  facet normal ").append(vN.getX()).append(" ").append(vN.getY()).
				 * append(" ") .append(vN.getZ()).append("\r\n");
				 * sb.append("    outer loop\r\n");
				 * sb.append("      vertex ").append(vA.getX()).append(" ").append(vA.getY()).
				 * append(" ").append(vA.getZ()) .append("\r\n");
				 * sb.append("      vertex ").append(vB.getX()).append(" ").append(vB.getY()).
				 * append(" ").append(vB.getZ()) .append("\r\n");
				 * sb.append("      vertex ").append(vC.getX()).append(" ").append(vC.getY()).
				 * append(" ").append(vC.getZ()) .append("\r\n"); sb.append("    endloop\r\n");
				 * sb.append("  endfacet\r\n");
				 */

				vectorList.add(A);
				vectorList.add(B);
				vectorList.add(C);

			}

			// }
			/*
			 * sb.append("endsolid meshJCAE\r\n"); // write file try (BufferedWriter writer
			 * = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"),
			 * StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			 * writer.write(sb.toString()); }
			 */

		}
		return vectorList;

	}
}
