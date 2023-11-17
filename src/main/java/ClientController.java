//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Parent;
//import javafx.scene.control.Button;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class ClientController implements Initializable {
//    @FXML
//    Button b1;
//
//    @FXML
//    Button b2;
//
//    @FXML
//    Button b3;
//
//
//    void handleSelection(Button b) throws IOException {
//
//        String data= String.format("Category: %s", b.getText());
//        HangmanClientGUI.clientConnection.send(data);
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlfiles/clientGamePage.fxml"));
//        Parent root2 = loader.load();
//
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//    }
//}
