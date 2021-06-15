package anagrame.client;
import anagrame.domain.Game;
import anagrame.domain.User;
import anagrame.services.IObserver;
import anagrame.services.IServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class StartController extends UnicastRemoteObject implements IObserver, Serializable {
    @FXML
    public Button startButton;
    @FXML
    public AnchorPane gamePane;
    @FXML
    public ListView<String> playersList;
    @FXML
    public Label label;
    @FXML
    public Label wordLabel;
    @FXML
    public TextField guessField;
    @FXML
    public Button sendResponseButton;
    @FXML
    public Label redLabel;
    @FXML
    public Label correctWordLabel;
    @FXML
    public Label pointsLabel;
    @FXML
    public Label pLabel;
    @FXML
    public Button logoutButton;
    private User user;
    private Stage stage;
    private IServices server;
    private int round;
    private Game game;

    public StartController() throws RemoteException {
        round = 0;
    }

    @Override
    public void enableStart() throws RemoteException {
        Platform.runLater(() -> startButton.setDisable(false));
    }

    @Override
    public void disableStart() throws RemoteException {
        Platform.runLater(() -> startButton.setDisable(true));
    }

    @Override
    public void newRound(Integer id, String word, Integer points) throws RemoteException {
        Platform.runLater(() -> {
            round++;
            if (round == 1) {
                sendResponseButton.setDisable(false);
                gamePane.setVisible(true);
                List<String> players = server.getAllPlayers();
                playersList.getItems().addAll(players);
            } else {
                if (round == 2) {
                    sendResponseButton.setDisable(false);
                    label.setText("SECOND WORD : ");
                } else if (round == 3) {
                    sendResponseButton.setDisable(false);
                    label.setText("THIRD WORD : ");
                }
            }
            wordLabel.setText(word);
        });
    }

    @Override
    public void endGame(String correctWord, int points) throws RemoteException {
        Platform.runLater(() -> {
            if (round == 1) {
                redLabel.setVisible(true);
                correctWordLabel.setVisible(true);
                pLabel.setVisible(true);
                pointsLabel.setVisible(true);
            }
            correctWordLabel.setText(correctWord);
            pointsLabel.setText(String.valueOf(points));
            logoutButton.setVisible(true);
            round = 0;
        });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            try {
                this.logout();
            } catch (Exception e) {
                Utils.showAlert(e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    public void startNewGame(ActionEvent actionEvent) throws Exception {
        server.startGame();
        logoutButton.setVisible(false);
    }

    public void setServer(IServices server) {
        this.server = server;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void resultReceived(String correctWord, int points) throws RemoteException {
        Platform.runLater(() -> {
            if (round == 1) {
                redLabel.setVisible(true);
                correctWordLabel.setVisible(true);
                pLabel.setVisible(true);
                pointsLabel.setVisible(true);
            }
            correctWordLabel.setText(correctWord);
            pointsLabel.setText(String.valueOf(points));
            try {
                server.requestRound();
                if (round == 3) {
                    server.announceEnded(user, this);
                    round = 0;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void wonOrNot(int finalScore, int biggestScore) {
        Platform.runLater(() -> {
            if (finalScore == biggestScore) { // winner
                Utils.showAlert("Good news, " + user.getUsername() + "! You won with a score of " + finalScore + "!!!", Alert.AlertType.INFORMATION);
            } else {
                Utils.showAlert("Bad news, " + user.getUsername() + "! You lost with a score of " + finalScore, Alert.AlertType.INFORMATION);
            }
        });
    }

    public void sendResponse(MouseEvent mouseEvent) throws Exception {
        Platform.runLater(() -> {
            try {
                String guess = guessField.getText();
                sendResponseButton.setDisable(true);
                guessField.clear();
                server.sendResponse(user.getUsername(), game.getId(), guess);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void logout() {
        try {
            server.logout(user.getUsername(), this);
            System.exit(0);
        } catch (Exception e) {
            Utils.showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
