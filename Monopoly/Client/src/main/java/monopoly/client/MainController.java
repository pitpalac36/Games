package monopoly.client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import monopoly.services.IObserver;
import monopoly.services.IServices;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import static java.lang.System.exit;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    @FXML
    public ListView<String> playersListView;
    @FXML
    public Label positionsLabel;
    @FXML
    public Button rollDiceBtn;
    @FXML
    public Label generatedNoLabel;
    @FXML
    public Label currentPositionLabel;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Label currencyLabel;
    @FXML
    public Label explanationLabel;
    @FXML
    public Button logoutBtn;
    @FXML
    Button startBtn;
    private IServices server;
    private String username;
    private Stage stage;
    private int[] numbers;
    private int currency;
    private int currentPosition;
    private int rounds = 0;

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
    public void newGame(int[] numbers, int sum) {
        StringBuilder text = new StringBuilder();
        for (int each : numbers) {
            text.append(each).append("  ");
        }
        this.numbers = numbers;
        this.currency = sum;
        this.currentPosition = 1;
        Platform.runLater(() -> {
            playersListView.getItems().addAll(server.getAllPlayers());
            startBtn.setDisable(true);
            anchorPane.setVisible(true);
            positionsLabel.setText(String.valueOf(text));
            currencyLabel.setText(String.valueOf(sum));
            currentPositionLabel.setText(String.valueOf(currentPosition));
        });
    }

    @Override
    public void pay(int sum, int position, String username) {
        currency -= sum;
        Platform.runLater(() -> {
            currentPosition = position;
            currentPositionLabel.setText(String.valueOf(currentPosition));
            currencyLabel.setText(String.valueOf(currency));
            if (username.isEmpty()) {
                explanationLabel.setText("You pay " + sum + "!");
            } else {
                explanationLabel.setText("Place was occupied by " + username + ". You pay " + sum + " to " + username + "!");
            }
        });
    }

    @Override
    public void receive(int sum, String username) {
        currency += sum;
        Platform.runLater(() -> {
            currencyLabel.setText(String.valueOf(currency));
            if (username != null) {
                explanationLabel.setText(username + " landed on your place. You receive " + sum + " from " + username + "!");
            } else {
                explanationLabel.setText("You reached the start again, so you receive " + sum + "!!");
            }
        });
    }

    @Override
    public void endGame() {
        Platform.runLater(() -> {
            logoutBtn.setVisible(true);
            startBtn.setDisable(false);
            rollDiceBtn.setDisable(true);
            server.sendScore(currency, username, this);
        });
    }

    @Override
    public void wonOrNot(String currentWinner, int biggestScore) {
        Platform.runLater(() -> {
            if (currency == biggestScore) { // winner
                Utils.showAlert("You won!!!", Alert.AlertType.INFORMATION);
            } else {
                Utils.showAlert("You lost! " + currentWinner + " won with a score of " + biggestScore, Alert.AlertType.INFORMATION);
            }
        });
    }

    public void rollDice(MouseEvent mouseEvent) throws Exception {
        Random r = new Random();
        int generated = r.nextInt(3) + 1;
        Platform.runLater(() -> {
            generatedNoLabel.setText(String.valueOf(generated));
        });
        server.movePawn(username, currentPosition, generated, currency, this);
        rounds++;
        if (rounds == 3)
            server.announceFinish(this);
    }
}
