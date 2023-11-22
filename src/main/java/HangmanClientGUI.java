import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class HangmanClientGUI extends Application {
	BorderPane pane, pane2;
	Button b1, b2, b3;
	Client clientConnection= null;
	String command, category;
	int len=0;
	int cat;
	HashMap<String, Scene > Scenes;
	HashMap<Integer, Integer> preCat;

	ArrayList<TextField> boxes;//makes len number of boxes for each word to guess
	String css;
	Scene scene;
	Stage primaryStage;
	HBox hbox;
	int badGuess=0;
	Text numGuess;
	int correctGuesses;
	ArrayList<Button> alphabet;
	String win_lose;
	Alert a = new Alert(Alert.AlertType.NONE);

	TextField portNo;
	Button submit, restart, exit;
	static int port;

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
		primaryStage.setScene(createLogInScene());
		primaryStage.show();
		preCat= new HashMap<>();
		submit.setOnAction(e->{
			port = Integer.valueOf(portNo.getText());
			clientConnection= new Client(data->Platform.runLater(()->{
				handleclientCon(data);

			}));
			clientConnection.start();
			primaryStage.setScene(Scenes.get("HomePage"));
			primaryStage.show();
		});
		css = Objects.requireNonNull(this.getClass().getResource("/assets/style.css")).toExternalForm();


		createScenes();

		b1.setOnAction(e->
				handleSelection(b1, 1));
		b2.setOnAction(e->handleSelection(b2, 2));
		b3.setOnAction(e->handleSelection(b3, 3));

		
				
		
	}
	public Scene createLogInScene(){
		Text t1= new Text("Enter Port Number");
		portNo= new TextField();
		submit= new Button("Submit");
		HBox row= new HBox(portNo, submit);
		VBox col = new VBox(t1, row);
		BorderPane pane = new BorderPane();
		pane.setCenter(col);
		col.setAlignment(Pos.CENTER);
		row.setAlignment(Pos.CENTER);
		return new Scene(pane, 500, 400);
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
	void handleSelection(Button b, int x){

		String data= String.format("CATEGORY: %s", b.getText());
		clientConnection.send(data);
		category= b.getText().toUpperCase();
		changeScene("GamePage");

		if(!preCat.containsKey(x)){
			preCat.put(x, 0);
		}
		cat= x;
		
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
	void handleclientCon(Serializable data){
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
				correctGuesses++;
				command= command.substring(pos+1);
			}
			boxes.get(Integer.valueOf(command.substring(0))).setText(let);
			correctGuesses++;
			if(correctGuesses== len){
				//do right guess animation??
				//and go back to the home screen


				a.setAlertType(Alert.AlertType.INFORMATION);
				a.setTitle("Category Completed!!!");
				a.setContentText("Category Completed!!!");
				a.show();
				changeScene("HomePage");
				correctGuesses=0;
				badGuess=0;
				switch(cat){
					case 1:
						b1.setDisable(true);
						break;
					case 2:
						b2.setDisable(true);
						break;
					case 3:
						b3.setDisable(true);
						break;
				}
				if(b1.isDisabled() && b2.isDisabled() && b3.isDisabled()){
					win_lose= "You Win!!!";
					primaryStage.setScene(optionScreen());
					primaryStage.show();
				}
			}



		}
		else{  //probably a nill response from server
			badGuess++;
			numGuess.setText("Bad Guesses: "+String.valueOf(badGuess)+"/6");


			if(badGuess==6){
				preCat.replace(cat,preCat.get(cat)+1);
				for(Map.Entry<Integer,Integer> entry : preCat.entrySet()){
					if(entry.getValue() >= 3){
						a.setAlertType(Alert.AlertType.INFORMATION);
						a.setTitle("You Lose :(");
						a.setContentText("You missed 3 words in a row");
						a.show();

						//game over frrr
						//exit or restart
						win_lose= "You Lose!!!";
						primaryStage.setScene(optionScreen());
						primaryStage.show();
						return;

					}
				};
				if(!a.isShowing()) {
					a.setAlertType(Alert.AlertType.INFORMATION);
					a.setTitle("You Lose :(");
					a.setContentText("You Lose :(");
					a.show();
				}



				correctGuesses=0;

				badGuess=0;
				changeScene("HomePage");

			}




		}
	}
	public Scene  optionScreen(){
		BorderPane pane= new BorderPane();
		Text t1= new Text(win_lose);
		restart= new Button("Play Again");

		exit= new Button("Exit");
		restart.setOnAction(e->{
			clientConnection.send("RESET");
			//reset everything
			resetAll();
			changeScene("HomePage");
		});
		exit.setOnAction(e->{
			Platform.exit();
		});
		VBox vBox= new VBox(t1, restart, exit);
		vBox.setSpacing(10);
		pane.setCenter(vBox);
		vBox.setAlignment(Pos.CENTER);
		pane.setCenter(vBox);
		return new Scene(pane,500, 400 );
	}
	void resetAll(){
		b1.setDisable(false);
		b2.setDisable(false);
		b3.setDisable(false);

//		pane= null;
//		pane2= null;
//		b1= null;
//		b2= null;
//		b3= null;
//
//		command= null;
//		category= null;
		len =0;
		cat= 0;
//		Scenes= null;
//		preCat= null;
		badGuess=0;
//		numGuess= null;
//		alphabet= null;
//		win_lose= null;
//		a=new Alert(Alert.AlertType.NONE);
//		portNo= null;
//		submit= restart= exit= null;
		port= 0;

	}

}
