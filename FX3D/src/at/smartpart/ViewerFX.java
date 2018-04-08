package at.smartpart;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewerFX extends Application {

  private static final String MESH_FILENAME =
    "/Users/lilyshard/Downloads/Perfect Diamond/Perfect Diamond.STL";

  private static final double MODEL_SCALE_FACTOR = 2;
  private static final double MODEL_X_OFFSET = 0; // standard
  private static final double MODEL_Y_OFFSET = 0; // standard

  private static final int VIEWPORT_SIZE = 800;

  private static final Color lightColor = Color.rgb(244, 255, 250);
  private static final Color jewelColor = Color.rgb(0, 190, 222);

  private Group root;
  private PointLight pointLight;
  
	static double anchorY;
	static double anchorX;

	private double anchorAngleX = 0;
	private double anchorAngleY = 0;

	private final DoubleProperty angleX = new SimpleDoubleProperty(25);
	private final DoubleProperty angleY = new SimpleDoubleProperty(40);

  static MeshView[] loadMeshViews() {

	Adaptor adaptor = new Adaptor();
	ArrayList<DefaultFaceMesh> list = adaptor.getMeshList();

	Tesselation tes = new Tesselation();
	STEPModel model = tes.tesselate(list);
    TriangleMesh mesh = new TriangleMesh();
    mesh.getFaces().addAll(model.getFaces());
    mesh.getPoints().addAll(model.getNodes());
    mesh.getTexCoords().addAll(model.getTexCoor());
    
    MeshView mView = new MeshView(mesh);
    mView.setCullFace(CullFace.NONE);
    return new MeshView[] {  mView };
  }

  private Group buildScene() {
    MeshView[] meshViews = loadMeshViews();
    for (int i = 0; i < meshViews.length; i++) {
      meshViews[i].setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
      meshViews[i].setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
      meshViews[i].setTranslateZ(VIEWPORT_SIZE / 2);
      meshViews[i].setScaleX(MODEL_SCALE_FACTOR);
      meshViews[i].setScaleY(MODEL_SCALE_FACTOR);
      meshViews[i].setScaleZ(MODEL_SCALE_FACTOR);

      PhongMaterial sample = new PhongMaterial(jewelColor);
      sample.setSpecularColor(lightColor);
      sample.setSpecularPower(16);
      meshViews[i].setMaterial(sample);

      meshViews[i].getTransforms().setAll(new Rotate(38, Rotate.Z_AXIS), new Rotate(20, Rotate.X_AXIS));
    }

    pointLight = new PointLight(lightColor);
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

    root = new Group(meshViews);
    root.getChildren().add(pointLight);
    root.getChildren().add(pointLight2);
    root.getChildren().add(pointLight3);
    root.getChildren().add(ambient);

    return root;
  }

  private PerspectiveCamera addCamera(Scene scene) {
    PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
    System.out.println("Near Clip: " + perspectiveCamera.getNearClip());
    System.out.println("Far Clip:  " + perspectiveCamera.getFarClip());
    System.out.println("FOV:       " + perspectiveCamera.getFieldOfView());

    scene.setCamera(perspectiveCamera);
    return perspectiveCamera;
  }

  @Override
  public void start(Stage primaryStage) {
    Group group = buildScene();
    
    
	StackPane zoomPane = new StackPane();
	zoomPane.getChildren().addAll(group);
	String fileName = "background.png";
	int fontSize = 30;

	Image image = new Image(getClass().getResourceAsStream(fileName));

	Image bgImage = renderText2Background(image, fileName, fontSize);

	ImageView imgView = new ImageView(bgImage);


	AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();

	zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
		public void handle(ScrollEvent event) {
			double zoomFactor = 1.5;
			if (event.getDeltaY() <= 0) {
				// zoom out
				zoomFactor = 1 / zoomFactor;
			}
			zoomOperator.zoom(group, zoomFactor, event.getSceneX(), event.getSceneY(), event.getZ());
		}
	});

	

	Group parent = new Group(zoomPane);
	
	

	Rotate xRot;
	Rotate yRot;

	parent.getTransforms().setAll(xRot = new Rotate(0, Rotate.X_AXIS), yRot = new Rotate(0, Rotate.Y_AXIS));

	xRot.angleProperty().bind(angleX);
	yRot.angleProperty().bind(angleY);


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
    
    
    group.setScaleX(2);
    group.setScaleY(2);
    group.setScaleZ(2);
    group.setTranslateX(50);
    group.setTranslateY(50);

    Scene scene = new Scene(parent, VIEWPORT_SIZE, VIEWPORT_SIZE, true);
    scene.setFill(Color.rgb(10, 10, 40));
    addCamera(scene);
    primaryStage.setTitle("Jewel Viewer");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    System.setProperty("prism.dirtyopts", "false");
    launch(args);
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
}