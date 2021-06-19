package game.client;
import game.model.Card;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import game.services.IObserver;
import game.services.IServices;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

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
    public FlowPane wonCardsPane;
    @FXML
    Button startBtn;
    private IServices server;
    private String username;
    private Stage stage;
    private Card c1;
    private Card c2;
    private Card c3;

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
    public void startGame(List<String> entrySet, Card c1, Card c2, Card c3) {
        Platform.runLater(() -> {
            anchorPane.setVisible(true);
            playersListView.getItems().addAll(entrySet);
            logoutBtn.setDisable(true);
            startBtn.setDisable(true);
            card1Btn.setText(c1.getValue());
            card2Btn.setText(c2.getValue());
            card3Btn.setText(c3.getValue());
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        });
    }

    @Override
    public void disable() throws RemoteException {
        Platform.runLater(() -> {
            card1Btn.setDisable(true);
            card2Btn.setDisable(true);
            card3Btn.setDisable(true);
        });
    }

    @Override
    public void enable() throws RemoteException {
        Platform.runLater(() -> {
            card1Btn.setDisable(false);
            card2Btn.setDisable(false);
            card3Btn.setDisable(false);
        });
    }

    @Override
    public void wonCards(List<Card> thisRound) {
        Platform.runLater(() -> {
            Utils.showAlert("Congrats, " + username + ", you won all the cards!", Alert.AlertType.INFORMATION);
            for (Card each : thisRound) {
                Button b = new Button(each.getValue());
                b.setPrefWidth(78.0);
                b.setPrefHeight(78.0);
                b.setDisable(true);
                wonCardsPane.getChildren().add(b);
            }
        });
    }

    @Override
    public void endGame() throws RemoteException {
        Platform.runLater(() -> {
            try {
                server.sendNumber(username, this, wonCardsPane.getChildren().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            logoutBtn.setDisable(false);
        });
    }

    public void pickCard(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            String card = ((Button)actionEvent.getSource()).getText();
            try {
                if (card.equals(c1.getValue())) {
                    server.sendCard(c1, username, this);
                } else {
                    if (card.equals(c2.getValue())) {
                        server.sendCard(c2, username, this);
                    } else {
                        server.sendCard(c3, username, this);
                    }
                }
                anchorPane.getChildren().remove(actionEvent.getSource());
            } catch (Exception e) {
                Utils.showAlert(e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

}
