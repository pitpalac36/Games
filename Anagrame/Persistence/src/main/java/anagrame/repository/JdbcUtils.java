package anagrame.repository;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class JdbcUtils {
    private static SessionFactory sessionFactory;

    public static void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch(HibernateException exception){
            System.out.println("Problem creating session factory");
            exception.printStackTrace();
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    static void close(){
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }
}