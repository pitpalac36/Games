package razboi.client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import razboi.services.IServices;

public class StartClient extends Application {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Initiating client");
        try {
            ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
            IServices server = (IServices) factory.getBean("service");
            System.out.println("Obtained a reference to remote games server");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setService(server);
            Stage stage = new Stage();
            stage.setTitle("log in");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Games Initialization exception:" + e);
            e.printStackTrace();
        }
    }
}