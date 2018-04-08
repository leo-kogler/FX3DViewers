package at.smartpart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class FX3D2 extends Application {

	static double anchorY;
	static double anchorX;

	private double anchorAngleX = 0;
	private double anchorAngleY = 0;

	private final DoubleProperty angleX = new SimpleDoubleProperty(25);
	private final DoubleProperty angleY = new SimpleDoubleProperty(40);
	private PerspectiveCamera activeNode;

	private int VIEWPORT_SIZE = 800;
	
	Color lightColor = Color.rgb(244, 255, 250);
	Color solidColor = Color.rgb(0, 190, 222);
	
	public static void main(String[] args) {

		launch(args);

	}

	public TriangleMesh createMesh() {
		TriangleMesh mesh = new TriangleMesh();

		Adaptor adaptor = new Adaptor();
		ArrayList<DefaultFaceMesh> list = adaptor.getMeshList();

		Tesselation tes = new Tesselation();
		STEPModel model = tes.tesselate(list);
		float[] points = model.getNodes();
		int[] faces = model.getFaces();
		float[] texCoords = model.getTexCoor();
		
		
		
		
		
		
		
	//	int[][] facesD = adaptor.getIntArr();

/*

		
		

		int pointElementSize = mesh.getPointElementSize();

		boolean DEBUG = true;
		int counter = 0;
		int[] facesArray = new int[faces.length];
		int facesInd = 0;
		int pointsInd = points.length;
		for (int[] face : facesD) {
			
				System.out.println("faces.length" + faces.length);
				
			if (DEBUG)
				System.out.println("face.length = " + (face.length / 2) + "  -- " + Arrays.toString(face));
			int lastPointIndex = face[face.length - 2];
			if (DEBUG)
				System.out.println("    lastPointIndex = " + lastPointIndex);
			for (int p = 0; p < face.length; p += 2) {
				
				if (counter < faces.length) {
				int pointIndex = face[p];
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
				counter+=6;
				System.out.println("counter" + counter);
			}

		}
			
	}

		
		int [] faces2 = {0, 0, 1, 0, 2, 0, 0, 0, 2, 0, 5, 0, 6, 0, 0, 0, 1, 0, 6, 0, 10, 0, 0, 0, 10, 0, 5, 0, 0, 0, 10, 0, 16, 0, 5, 0, 16, 0, 5, 0, 2, 0, 16, 0, 2, 0, 23, 0, 23, 0, 2, 0, 1, 0, 23, 0, 1, 0, 6, 0, 10, 0, 16, 0, 23, 0, 10, 0, 23, 0, 6, 0};
		
		int[] faces3 = {0,10,2,5,1,9,2,5,3,4,1,9,4,7,5,8,6,2,6,2,5,8,7,3,0,13,1,9,4,12,4,12,1,9,5,8,2,1,6,0,3,4,3,4,6,0,7,3,0,10,4,11,2,5,2,5,4,11,6,6,1,9,3,4,5,8,5,8,3,4,7,3};
		
		
	*/	
		
	/*
	    float[] points = {
	            -5, 5, 0,
	            -5, -5, 0,
	            5, 5, 0,
	            5, -5, 0
	        };
	        float[] texCoords = {
	            1, 1,
	            1, 0,
	            0, 1,
	            0, 0
	        };
	        int[] faces = {
	            2, 2, 1, 1, 0, 0,
	            2, 2, 3, 3, 1, 1
	    };
		

*/
	        
		
		mesh.getPoints().addAll(points);
		mesh.getFaces().addAll(faces);
		mesh.getTexCoords().addAll(texCoords);

		System.out.println(Arrays.toString(faces));
		
		System.out.println("Points: " + points.length);
		System.out.println("Faces: " + faces.length);
		System.out.println("Tex: " + texCoords.length);
		
		
		return mesh;
	}

	@Override
	public void start(Stage stage) throws Exception {

		PerspectiveCamera camera = new PerspectiveCamera(false);

		PhongMaterial modelMaterial = new PhongMaterial();
		modelMaterial.setSpecularColor(solidColor);
		modelMaterial.setSpecularPower(16);

		MeshView mView = new MeshView(createMesh());
		mView.setMaterial(modelMaterial);
		mView.setDrawMode(DrawMode.FILL);
		mView.setCullFace(CullFace.NONE);

		PhongMaterial modelMaterial2 = new PhongMaterial();
		modelMaterial.setSpecularColor(Color.BLACK);
		
		MeshView mView2 = new MeshView(createMesh());
		mView2.setMaterial(modelMaterial2);
		mView2.setDrawMode(DrawMode.LINE);
		mView2.setCullFace(CullFace.NONE);
		
		
		
		Bounds bounds = mView.getBoundsInLocal();
		double maxX = bounds.getMaxX();
		double maxY = bounds.getMaxX();
		double maxZ = bounds.getMaxX();

		System.out.println("X: " + maxX + " Y: " + maxY + " Z: " + maxZ);

		
		
		Box cyl1 = new Box(500,10,10);

		cyl1.setMaterial(new PhongMaterial(Color.YELLOW)); //X
		
		
		
		Box cyl2 = new Box(10,10,500);
	
		cyl2.setMaterial(new PhongMaterial(Color.RED));   //Z
		
		
		Box cyl3 = new Box(10,500,10);
	
		cyl3.setMaterial(new PhongMaterial(Color.BLUE));  //Y
		
		
		Group axes = new Group();
		axes.getChildren().add(cyl1);
		axes.getChildren().add(cyl2);
		axes.getChildren().add(cyl3);
		
		
		
		
		
		StackPane zoomPane = new StackPane();
		zoomPane.getChildren().addAll(mView);
		zoomPane.getChildren().add(axes);

		// StackPane backGround = new StackPane();

		String fileName = "background.png";
		int fontSize = 30;

		Image image = new Image(getClass().getResourceAsStream(fileName));

		Image bgImage = renderText2Background(image, fileName, fontSize);

		ImageView imgView = new ImageView(bgImage);

		/*
		 * BackgroundImage myBI= new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
		 * BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		 * BackgroundSize.DEFAULT);
		 * 
		 * backGround.setBackground(new Background(myBI));
		 * 
		 * backGround.getChildren().add(zoomPane);
		 */

		// Create operator
		AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

		// Listen to scroll events (similarly you could listen to a button click,
		// slider, ...)
		zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent event) {
				double zoomFactor = 1.5;
				if (event.getDeltaY() <= 0) {
					// zoom out
					zoomFactor = 1 / zoomFactor;
				}
				zoomOperator.zoom(mView, zoomFactor, event.getSceneX(), event.getSceneY(), event.getZ());
			}
		});

		Group parent = new Group(zoomPane);

		Group world = new Group(imgView, parent);

		imgView.setTranslateX(0);
		imgView.setTranslateY(0);
		imgView.setTranslateZ(500);

		Bounds pBounds = parent.getBoundsInLocal();
		double X = pBounds.getMaxX();
		double Y = pBounds.getMaxY();
		double Z = pBounds.getMaxZ();

		System.out.println("X: " + X + " Y: " + Y + " Z: " + Z);

		parent.setTranslateX(960);
		parent.setTranslateY(540);
		parent.setTranslateZ(0);

		double screen = camera.getScaleZ();
		System.out.println("Screen" + screen);

		camera.setScaleZ(10.0);

		System.out.println("Screen" + camera.getScaleZ());

		Rotate xRot;
		Rotate yRot;

		parent.getTransforms().setAll(xRot = new Rotate(0, Rotate.X_AXIS), yRot = new Rotate(0, Rotate.Y_AXIS));

		xRot.angleProperty().bind(angleX);
		yRot.angleProperty().bind(angleY);

		
		createLights(world);
		Group root = new Group(world);
		// world parent

		final Scene scene = new Scene(root, 1920, 1080, true);
		scene.setFill(Color.WHITE);

		zoomPane.setOnMousePressed((MouseEvent event) -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();

			anchorAngleX = angleX.get();
			anchorAngleY = angleY.get();

			PickResult pickResult = event.getPickResult();

			System.out.println(pickResult.getIntersectedFace());

		});

		zoomPane.setOnMouseDragged((MouseEvent event) -> {
			angleX.set(anchorAngleX - anchorY - event.getSceneY());
			angleY.set(anchorAngleY + anchorX - event.getSceneX());
		});

		PointLight pointLight = new PointLight(Color.BLUE);
		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
		pointLight.setTranslateX(1920);
		pointLight.setTranslateY(1080);
		pointLight.setTranslateZ(-3000);
		pointLight.setRotate(45);


		
		//Group lGroup = new Group(pointLight);//, ambientLight);

		
		//root.getChildren().add(lGroup);


		scene.setCamera(camera);
		stage.setScene(scene);

		stage.setTitle("STEP Viewer Light");
		stage.show();
		

		// capture(root);

	}
	
	
	void createLights (Group root) {
		
		

		
		PointLight pointLight = new PointLight(lightColor);
	    pointLight.setTranslateX(VIEWPORT_SIZE*3/4);
	    pointLight.setTranslateY(VIEWPORT_SIZE/2);
	    pointLight.setTranslateZ(VIEWPORT_SIZE/2);
	    PointLight pointLight2 = new PointLight(lightColor);
	    pointLight2.setTranslateX(VIEWPORT_SIZE*1/4);
	    pointLight2.setTranslateY(VIEWPORT_SIZE*3/4);
	    pointLight2.setTranslateZ(VIEWPORT_SIZE*3/4);
	    PointLight pointLight3 = new PointLight(lightColor);
	    pointLight3.setTranslateX(VIEWPORT_SIZE*5/8);
	    pointLight3.setTranslateY(VIEWPORT_SIZE/2);
	    pointLight3.setTranslateZ(0);

	    Color ambientColor = Color.rgb(80, 80, 80, 0);
	    AmbientLight ambient = new AmbientLight(ambientColor);


	    root.getChildren().add(pointLight);
	    root.getChildren().add(pointLight2);
	    root.getChildren().add(pointLight3);
	    root.getChildren().add(ambient);

	}

	public void capture(Node node) {
		WritableImage image = node.snapshot(new SnapshotParameters(), null);

		// TODO: probably use a file chooser here
		File file = new File("data/screenshot.png");

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException e) {
			// TODO: handle exception here
		}
	}

	private WritableImage textToImage(String text) {

		Text t = new Text(text);
		Scene scene = new Scene(new StackPane(t));
		return t.snapshot(null, null);
	}

	protected Image renderText2Background(Image fxImage, String text, int fontSize) {

		BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);

		Graphics2D gfx = image.createGraphics();
		gfx.setColor(java.awt.Color.black);
		gfx.setFont(new Font("Tahoma", Font.ROMAN_BASELINE, fontSize));
		gfx.drawString(text, image.getWidth() - fontSize * text.length(), image.getHeight() - fontSize);
		System.err.println(text);

		Image fxReturnImage = SwingFXUtils.toFXImage(image, null);

		return fxReturnImage;
	}
	
	public TriangleMesh buildTriangleMesh(int subDivX, int subDivY, float scale, boolean planeXY) {

	       final int pointSize = 3;
	       final int texCoordSize = 2;
	       // 3 point indices and 3 texCoord indices per triangle
	       final int faceSize = 6;
	       int numDivX = subDivX + 1;
	       int numVerts = (subDivY + 1) * numDivX;
	       float points[] = new float[numVerts * pointSize];
	       float texCoords[] = new float[numVerts * texCoordSize];
	       int faceCount = subDivX * subDivY * 2;
	       int faces[] = new int[faceCount * faceSize];

	       // Create points and texCoords
	       for (int y = 0; y <= subDivY; y++) {
	           float dy = (float) y / subDivY;
	           double fy = (1 - dy) * minY + dy * maxY;

	           for (int x = 0; x <= subDivX; x++) {
	               float dx = (float) x / subDivX;
	               double fx = (1 - dx) * minX + dx * maxX;

	               int index = y * numDivX * pointSize + (x * pointSize);
	               points[index] = (float) fx * scale;
	               if(planeXY){
	                   points[index + 1] = (float) fy * scale;
	                   points[index + 2] = 0.0f;
	               } else {
	                   points[index + 2] = (float) fy * scale;
	                   points[index + 1] = 0.0f;
	               }

	               index = y * numDivX * texCoordSize + (x * texCoordSize);
	               texCoords[index] = dx;
	               texCoords[index + 1] = dy;
	           }
	       }

	       // Create faces
	       for (int y = 0; y < subDivY; y++) {
	           for (int x = 0; x < subDivX; x++) {
	               int p00 = y * numDivX + x;
	               int p01 = p00 + 1;
	               int p10 = p00 + numDivX;
	               int p11 = p10 + 1;
	               int tc00 = y * numDivX + x;
	               int tc01 = tc00 + 1;
	               int tc10 = tc00 + numDivX;
	               int tc11 = tc10 + 1;

	               int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
	               faces[index + 0] = p00;
	               faces[index + 1] = tc00;
	               if(planeXY){
	                   faces[index + 2] = p10;
	                   faces[index + 3] = tc10;
	                   faces[index + 4] = p11;
	                   faces[index + 5] = tc11;
	               } else {
	                   faces[index + 2] = p11;
	                   faces[index + 3] = tc11;
	                   faces[index + 4] = p10;
	                   faces[index + 5] = tc10;
	               }

	               index += faceSize;
	               faces[index + 0] = p11;
	               faces[index + 1] = tc11;
	               if(planeXY){
	                   faces[index + 2] = p01;
	                   faces[index + 3] = tc01;
	                   faces[index + 4] = p00;
	                   faces[index + 5] = tc00;
	               } else {
	                   faces[index + 2] = p00;
	                   faces[index + 3] = tc00;
	                   faces[index + 4] = p01;
	                   faces[index + 5] = tc01;
	               }
	           }
	       }

	       TriangleMesh triangleMesh = new TriangleMesh();
	       triangleMesh.getPoints().addAll(points);
	       triangleMesh.getTexCoords().addAll(texCoords);
	       triangleMesh.getFaces().addAll(faces);

	       return triangleMesh;
	   }

}
