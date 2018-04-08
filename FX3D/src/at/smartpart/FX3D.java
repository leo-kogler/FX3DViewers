package at.smartpart;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FX3D extends Application {

	private View view;
	static double anchorY;
	static double anchorX;

	private static double anchorAngleX = 0;
	private static double anchorAngleY = 0;
	
	private final static DoubleProperty angleX = new SimpleDoubleProperty(25);
	private final static DoubleProperty angleY = new SimpleDoubleProperty(40);
	
	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {

		view = new View();
		stage.setTitle("FX3D");
		stage.setScene(view.scene);
		stage.show();
		view.animate();

	}

	private static class View {

		public Scene scene;
		public Box box;
		public PerspectiveCamera camera;

		private final Rotate rotX;
		private final Rotate rotY;
		private final Rotate rotZ;
		private final Translate translateZ;

		private View() {

			box = new Box(10, 10, 10);
			camera = new PerspectiveCamera(true);
			rotX = new Rotate(-20, Rotate.X_AXIS);
			rotY = new Rotate(-20, Rotate.Y_AXIS);
			rotZ = new Rotate(-20, Rotate.Z_AXIS);
			translateZ = new Translate(0, 0, -100);

			camera.getTransforms().addAll(rotX, rotY, rotZ, translateZ);

			Group group = new Group(box, camera);
			
			
			
			scene.setOnMousePressed((MouseEvent event)-> {
				anchorX = event.getSceneX();
				anchorY = event.getSceneY();
				
				anchorAngleX = angleX.get();
				anchorAngleY = angleY.get();
			});
			
			scene.setOnMouseDragged((MouseEvent event)-> {
				angleY.set(anchorAngleY + anchorX - event.getSceneX());
			});
			
			
			scene = new Scene(group, 640, 480);
			scene.setCamera(camera);
		}

		public void animate() {

			Timeline tl = new Timeline(

					new KeyFrame(Duration.seconds(0), new KeyValue(translateZ.zProperty(), -20),
							new KeyValue(rotX.angleProperty(), 90), new KeyValue(rotY.angleProperty(), 90),
							new KeyValue(rotZ.angleProperty(), 90)),
					new KeyFrame(Duration.seconds(5), new KeyValue(translateZ.zProperty(), -80),
							new KeyValue(rotX.angleProperty(), -90), new KeyValue(rotY.angleProperty(), -90),
							new KeyValue(rotZ.angleProperty(), -90)));

			tl.setCycleCount(Animation.INDEFINITE);
			tl.play();

		}

	}

}
