import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class HangmanClientGUI extends Application {
	BorderPane pane, pane2;
	Button b1, b2, b3;
	Client clientConnection= null;
	String command, category;
	int len=0;
	Text t1;
	HashMap<String, Scene > Scenes;

	ArrayList<TextField> boxes;//makes len number of boxes for each word to guess
	String css;
	Scene scene;
	Stage primaryStage;
	HBox hbox;
	int badGuess=0;
	Text numGuess;

	ArrayList<Button> alphabet;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Client");
		this.primaryStage= primaryStage;
		css = Objects.requireNonNull(this.getClass().getResource("/assets/style.css")).toExternalForm();
		clientConnection= new Client(data->Platform.runLater(()->{
			String substring= data.toString().substring(data.toString().indexOf(" ") + 1);
			if(data.toString().contains("LEN:")) {

				command= substring;
				len= Integer.parseInt(command);
				generateGame();
			} else if (data.toString().contains("LOC:") &&!data.toString().contains("NIL") ) {

				String let= substring.substring(0,1);
				command= substring.substring(2);
				while(command.contains(";")){
					int pos = command.indexOf(";");
					boxes.get(Integer.valueOf(command.substring(0, pos))).setText(let);
					command= command.substring(pos+1);
				}
				boxes.get(Integer.valueOf(command.substring(0))).setText(let);
//				for(int i=0; i< command.length();i++){
//					 boxes.get(Integer.valueOf(command.substring(i, i+1))).setText(let);
//				}
//



			}
			else{  //probably a nill response from server
				badGuess++;
				numGuess.setText("Bad Guesses: "+String.valueOf(badGuess)+"/6"); ;
			}
		}));
		clientConnection.start();
		createScenes();
		primaryStage.setScene(Scenes.get("HomePage"));
		primaryStage.show();
		b1.setOnAction(e->
				handleSelection(b1));
		b2.setOnAction(e->handleSelection(b2));
		b3.setOnAction(e->handleSelection(b3));

		
				
		
	}
	public void changeScene(String str){
		scene= Scenes.get(str);
		scene.getStylesheets().add(css);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public void createScenes(){
		Scenes= new HashMap<>();
		Scenes.put("HomePage", createClientPage());
		Scenes.put("GamePage", createGamePage());
	}
	public Scene createClientPage(){
		pane= new BorderPane();
		Text t1= new Text("Pick a Category to Start");
		b1= new Button("Animals");

		b2 = new Button("Places");
		b3= new Button("Brands");
		VBox vBox= new VBox(t1, b1, b2, b3);
		vBox.setSpacing(10);
		pane.setCenter(vBox);
		vBox.setAlignment(Pos.CENTER);


		return new Scene(pane, 500, 400);
	}
	public Scene createGamePage(){
		numGuess= new Text("Bad Guesses: "+String.valueOf(badGuess)+"/6") ;
		pane2= new BorderPane();
		BorderPane miniPane= new BorderPane();
		miniPane.setRight(numGuess);
		pane2.setTop(miniPane);
		return new Scene(pane2, 700, 700);
	}
	void handleSelection(Button b){

		String data= String.format("CATEGORY: %s", b.getText());
		clientConnection.send(data);
		category= b.getText().toUpperCase();
		changeScene("GamePage");
		
	}
	void generateGame(){
		initializeButton();
		boxes= new ArrayList<>();
		hbox = new HBox(); // Initialize hbox outside the loop
		for (int i = 0; i < len; i++) {
			boxes.add(new TextField());
//			boxes.get(i).setFill(Color.web("#ffd700"));
			boxes.get(i).setId("guessBoxes");
			boxes.get(i).setEditable(false);

			hbox.getChildren().add(boxes.get(i));

		}
		hbox.setSpacing(3);
		Text t2= new Text(category);

		ImageView iv1= new ImageView();
		iv1.setId("image");
		iv1.setFitWidth(100);
		iv1.setFitHeight(350);
		VBox vBox= new VBox(t2,iv1,hbox);
		pane2.setCenter(vBox);
		vBox.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER);

	}
	void initializeButton(){
		alphabet= new ArrayList<>();
		HBox sectionA_J= new HBox();
		HBox sectionL_R= new HBox();
		HBox sectionR_Z= new HBox();

		for(int i=0; i< 26; i++){
			char mychar= (char) ('A'+i);
			alphabet.add(new Button(String.valueOf(mychar)));
			int finalI = i;
			alphabet.get(i).setOnAction(e->{
				handleAlphabet(alphabet.get(finalI));});
		}
		for(int i=0; i<11; i++){
			sectionA_J.getChildren().add(alphabet.get(i));
		}
		for(int i=11; i<19; i++){
			sectionL_R.getChildren().add(alphabet.get(i));
		}
		for(int i=19; i<26; i++){
			sectionR_Z.getChildren().add(alphabet.get(i));
		}
		VBox alphabetButtons=new VBox(sectionA_J, sectionL_R,sectionR_Z);
		pane2.setBottom(alphabetButtons);
		alphabetButtons.setAlignment(Pos.CENTER);
		sectionR_Z.setAlignment(Pos.CENTER);
		sectionL_R.setAlignment(Pos.CENTER);
		sectionA_J.setAlignment(Pos.CENTER);


	}
	void handleAlphabet(Button b1){
		clientConnection.send("CHECK: "+b1.getText());
		b1.setDisable(true);
	}

}
