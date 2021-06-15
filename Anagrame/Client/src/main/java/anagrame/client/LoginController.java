package anagrame.client;
import anagrame.domain.User;
import anagrame.services.IServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    public TextField usernameField;
    public PasswordField passwordField;
    public Button loginButton;
    private IServices server;

    public void setService(IServices server) {
        this.server = server;
    }

    public void login(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("views\\main.fxml"));
        Parent root=loader.load();
        StartController startController = loader.getController();

        try {
            boolean valid = server.login(usernameField.getText(), passwordField.getText(), startController);
            if (valid) {
                User user = new User(usernameField.getText(), passwordField.getText());
                stage.setTitle(usernameField.getText() + "'s window");
                stage.setScene(new Scene(root));

                stage.show();
                startController.setUser(user);
                startController.setStage(stage);
                startController.setServer(server);
                ((Node) (mouseEvent.getSource())).getScene().getWindow().hide();
            } else {
                Utils.showAlert("Access denied!", Alert.AlertType.ERROR);
                usernameField.clear();
                passwordField.clear();
            }
            } catch (Error error) {
                Utils.showAlert("Error occured: " + error.getMessage(), Alert.AlertType.ERROR);
                usernameField.clear();
                passwordField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
