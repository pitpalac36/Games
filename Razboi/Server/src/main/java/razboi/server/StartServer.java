package razboi.server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import razboi.persistence.JdbcUtils;

public class StartServer {
    public static void main(String[] args) {
        JdbcUtils.initialize();
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");
    }
}

