package barcute.client;
import barcute.model.Place;
import barcute.services.IObserver;
import barcute.services.IServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.exit;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    @FXML
    public Label descriptiveLabel;
    @FXML
    public Label opponentLabel;
    @FXML
    public GridPane boardPane = new GridPane();
    @FXML
    public Button startButton;
    @FXML
    public Button logoutButton;
    @FXML
    public Label infoLabel;
    private IServices server;
    private int id;
    Button[][] boardButtons = new Button[4][4];
    private Place first = new Place();
    private Place second = new Place();
    private boolean gameStarted = false;

    public MainController() throws RemoteException {
    }

    public void setService(IServices server) {
        this.server = server;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStage(Stage primaryStage) {
        Platform.runLater(() -> {
            for (int l = 0; l < 4; l++) {
                for (int k = 0; k < 4; k++) {
                    boardButtons[l][k] = new Button();
                    boardButtons[l][k].setPrefSize(50, 50);
                    int finalL = l;
                    int finalK = k;
                    boardButtons[l][k].setOnAction(event -> {
                        if (gameStarted) {
                            try {
                                server.sendGuess(finalL, finalK, id, this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!first.isSet()) {
                            first.setRow(finalL);
                            first.setColumn(finalK);
                            first.setSet(true);
                            ((Button)event.getSource()).setText("X");
                        } else if (!second.isSet()) {
                                if(!(first.getRow() == finalL && first.getColumn() == finalK)) {
                                    second.setRow(finalL);
                                    second.setColumn(finalK);
                                    second.setSet(true);
                                    ((Button)event.getSource()).setText("X");
                                }
                            }
                    });
                    boardPane.add(boardButtons[l][k], l, k);
                }
            }
        });
    }

    public void start(MouseEvent mouseEvent) throws Exception {
        if (!first.isSet() || !second.isSet()) {
            Utils.showAlert("Please choose 2 positions for your boat!", Alert.AlertType.ERROR);
        } else {
            server.addParticipant(id, this, first, second);
            startButton.setDisable(true);
        }
    }

    @Override
    public void startGame(int opponentId) {
        Platform.runLater(() -> {
            gameStarted = true;
            startButton.setDisable(true);
            descriptiveLabel.setVisible(true);
            opponentLabel.setText(String.valueOf(opponentId));
            opponentLabel.setVisible(true);
        });
    }

    @Override
    public void endGame(boolean won) throws RemoteException {
        Platform.runLater(() -> {
            disableBoard();
            if (won) {
                Utils.showAlert("Congrats, " + id + ", you won!", Alert.AlertType.INFORMATION);
            } else {
                Utils.showAlert("Bad news, " + id + ", you lost!", Alert.AlertType.INFORMATION);
            }
            logoutButton.setVisible(true);
            startButton.setDisable(false);
        });

    }

    public void disableBoard() {
        for (int l = 0; l < 4; l++) {
            for (int k = 0; k < 4; k++) {
                boardButtons[l][k].setDisable(true);
            }
        }
    }

    public void enableBoard() {
        for (int l = 0; l < 4; l++) {
            for (int k = 0; k < 4; k++) {
                boardButtons[l][k].setDisable(false);
            }
        }
    }

    @Override
    public void notifyGuess() {
        Platform.runLater(() -> {
            infoLabel.setText("You guessed one place!");
        });
    }

    @Override
    public void notifyDanger(int row, int column) throws RemoteException {
        Platform.runLater(() -> {
            boardButtons[row][column].setStyle("-fx-background-color: red;");
        });

    }

    public void logout(ActionEvent actionEvent) {
        try{
            server.logout(id, this);
            exit(0);
        }
        catch (Exception e){
            Utils.showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
