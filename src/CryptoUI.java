import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CryptoUI extends Application {

	TextArea output;
	private String type = "";

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(new Image("1.jpg"));
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));

		grid.setVgap(5);
		grid.setHgap(5);
		// Set the Size of the VBox
		grid.setMinSize(1000, 600);
		grid.setAlignment(Pos.TOP_LEFT);
		MenuBar menuBar = new MenuBar();

		// --- Menu File
		Menu menuFile = new Menu("Type");
		setType(menuFile);

		// --- Menu Edit
		Menu menuEdit = new Menu("Edit");

		// --- Menu View
		Menu menuView = new Menu("View");

		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

		grid.add(menuBar, 0, 0);

		GridPane inputGrid = new GridPane();
		inputGrid.setPadding(new Insets(0, 0, 0, 0));
		TextArea input = new TextArea();
		input.setPrefColumnCount(30);
		input.setPrefRowCount(30);
		ScrollBar s1 = new ScrollBar();
		s1.setOrientation(Orientation.VERTICAL);
		inputGrid.add(s1, 0, 0);
		inputGrid.add(input, 1, 0);
		grid.add(inputGrid, 0, 1);

		GridPane outputGrid = new GridPane();
		outputGrid.setPadding(new Insets(0, 0, 0, 0));
		output = new TextArea("Output");
		output.setPrefColumnCount(30);
		output.setPrefRowCount(30);
		output.setEditable(false);
		ScrollBar s2 = new ScrollBar();
		s2.setOrientation(Orientation.VERTICAL);
		outputGrid.add(s2, 0, 0);
		outputGrid.add(output, 1, 0);
		grid.add(outputGrid, 1, 1);
		

		GridPane submitGrid = new GridPane();
		submitGrid.setPadding(new Insets(0, 0, 0, 0));
		
		Label label1 = new Label("Key:");
		TextField textField = new TextField ();
		submitGrid.add(label1, 0, 0);
		submitGrid.add(textField, 1, 0);
		
		Button encrypt = new Button("Encrypt");
		submitGrid.setAlignment(Pos.CENTER);
		submitGrid.add(encrypt, 0, 1);
		encrypt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String inputStr = input.getText();
				Converter c = null;
				switch (type) {
				case "Affine":
					ArrayList<Integer> aff = getAffineKey(inputStr);
					c = new Affine(inputStr,aff.get(0),aff.get(1));
					break;
				case "Baby-DES":
					c = new Baby_DES(inputStr, textField.getText());
				default:
					c = new Converter(inputStr);
				}
				output = new TextArea(c.encrypt());
				output.setPrefColumnCount(30);
				output.setPrefRowCount(30);
				output.setEditable(false);
				outputGrid.add(output, 1, 0);
				stage.show();
			}

			private ArrayList<Integer> getAffineKey(String inputStr) {
				ArrayList<Integer> re = new ArrayList<Integer>();
				String[] arr = textField.getText().split(",");
				if(arr.length != 2) {
					re.add(0);
					re.add(0);
				} else {
					re.add(Integer.parseInt(arr[0].trim()));
					re.add(Integer.parseInt(arr[1].trim()));
				}
				return re;
			}

		});
		
		Button decrypt = new Button("Decrypt");
		submitGrid.setAlignment(Pos.CENTER);
		submitGrid.add(decrypt, 1, 1);
		decrypt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String inputStr = input.getText();
				Converter c = null;
				switch (type) {
				case "Affine":
					System.out.println("Affine");
					ArrayList<Integer> aff = getAffineKey(inputStr);
					c = new Affine(inputStr,aff.get(0),aff.get(1));
					break;
				default:
					c = new Converter(inputStr);
				}
				output = new TextArea(c.decrypt());
				output.setPrefColumnCount(30);
				output.setPrefRowCount(30);
				output.setEditable(false);
				outputGrid.add(output, 1, 0);
				stage.show();
			}

			private ArrayList<Integer> getAffineKey(String inputStr) {
				ArrayList<Integer> re = new ArrayList<Integer>();
				String[] arr = textField.getText().split(",");
				if(arr.length != 2) {
					re.add(0);
					re.add(0);
				} else {
					re.add(Integer.parseInt(arr[0].trim()));
					re.add(Integer.parseInt(arr[1].trim()));
				}
				return re;
			}

		});
		grid.add(submitGrid, 0, 2);
		// Create the Scene
		Scene scene = new Scene(grid);

		// Set the Properties of the Stage
		stage.setX(100);
		stage.setY(200);
		stage.setMinHeight(300);
		stage.setMinWidth(400);

		// Add the scene to the Stage
		stage.setScene(scene);
		// Set the title of the Stage
		stage.setTitle("Crypography");
		// Display the Stage
		stage.show();
	}

	private void setType(Menu menuFile) {
		MenuItem item1 = new MenuItem("Affine");
		item1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				type = "Affine";
			}

		});
		menuFile.getItems().add(item1);
		MenuItem item2 = new MenuItem("Baby-DES");
		item2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				type = "Baby-DES";
			}
			
		});
		menuFile.getItems().add(item2);
	}

}
