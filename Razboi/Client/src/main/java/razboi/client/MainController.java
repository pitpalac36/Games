package razboi.client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import razboi.services.IObserver;
import razboi.services.IServices;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.exit;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    @FXML
    public ListView<String> playersListView;
    @FXML
    public Button logoutBtn;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Button card1Btn;
    @FXML
    public Button card2Btn;
    @FXML
    public Button card3Btn;
    @FXML
    public Button card4Btn;
    public List<Button> won = new ArrayList<>();
    @FXML
    public FlowPane secondaryPane= new FlowPane(Orientation.VERTICAL);
    @FXML
    Button startBtn;
    private IServices server;
    private String username;
    private Stage stage;


    public MainController() throws RemoteException {
    }

    public void setService(IServices server) {
        this.server = server;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize(){

    }

    @FXML
    public void logout() throws Exception {
        try{
            server.logout(username, this);
            exit(0);
        }
        catch (Exception e){
            alert(e.getMessage());
        }
    }

    public void alert(String err){
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
    }

    public void start(MouseEvent mouseEvent) throws Exception {
        System.out.println(username);
        System.out.println(this);
        server.addParticipant(username, this);
    }

    @Override
    public void newGame(String[] currentRoundCards) {
        Platform.runLater(() -> {
            playersListView.getItems().addAll(server.getAllPlayers());
            startBtn.setDisable(true);
            anchorPane.setVisible(true);
            card1Btn.setText(currentRoundCards[0]);
            card2Btn.setText(currentRoundCards[1]);
            card3Btn.setText(currentRoundCards[2]);
            card4Btn.setText(currentRoundCards[3]);
        });
    }

    @Override
    public void winCards(List<String> keySet) {
        Platform.runLater(() -> {
            Utils.showAlert(username + ", you won cards", Alert.AlertType.INFORMATION);
            for (String each : keySet) {
                Button b = new Button(each);
                b.setDisable(true);
                b.setPrefHeight(99.0);
                b.setPrefWidth(68.0);
                secondaryPane.getChildren().add(b);
                b.setOnAction(this::pickCard);
                won.add(b);
            }
        });

    }

    public void pickCard(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            String card = ((Button)actionEvent.getSource()).getText();
            anchorPane.getChildren().remove(actionEvent.getSource());
            server.sendCard(card, username, this);
        });
    }

    @Override
    public void disableCards() {
        Platform.runLater(() -> {
            card1Btn.setDisable(true);
            card2Btn.setDisable(true);
            card3Btn.setDisable(true);
            card4Btn.setDisable(true);
        });
    }

    @Override
    public void enableCards() {
        Platform.runLater(() -> {
            if (card1Btn == null && card2Btn == null && card3Btn == null && card4Btn == null) {
                Utils.showAlert("cam gata", Alert.AlertType.INFORMATION);
            } else {
                card1Btn.setDisable(false);
                card2Btn.setDisable(false);
                card3Btn.setDisable(false);
                card4Btn.setDisable(false);
            }
        });
    }
}