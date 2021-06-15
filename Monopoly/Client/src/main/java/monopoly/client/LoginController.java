package monopoly.client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import monopoly.services.IServices;

public class LoginController {
    public Button loginBtn;
    private IServices server;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;

    public void setService(IServices server) {
        this.server = server;
    }

    @FXML
    public void login(ActionEvent ae) throws Exception {
        Stage primaryStage=new Stage();

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/main.fxml"));
        Parent root=loader.load();

        monopoly.client.MainController mainController = loader.getController();
        try{
            boolean valid = server.login(usernameField.getText(), passwordField.getText(), mainController);
            if(valid){
                primaryStage.setScene(new Scene(root));
                primaryStage.setTitle("Player " + usernameField.getText());
                primaryStage.show();
                mainController.setService(server);
                mainController.setUsername(usernameField.getText());
                mainController.setStage(primaryStage);
                ((Node) (ae.getSource())).getScene().getWindow().hide();
            }
            else throw new Exception("Access denied");
        }catch (Exception e){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setTitle("Error message");
            message.setContentText(e.getMessage());
            message.showAndWait();
        }
    }

}
