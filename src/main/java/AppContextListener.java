
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

// This is handling events for backend
// This class acts as a Listener which calls everytime we toggle ON or OFF our server
@WebListener
public class AppContextListener implements ServletContextListener {

    // whenever server starts, contextInitialized is called at first
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("BloodBank Server started...");
    }

    // whenever server closed , contextDestroyed is called
    // WHY WE DO THIS ??
    // Because we will free-up every driver and memory instance before closing the server
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("BloodBank Server shutting down... Cleaning up MySQL drivers.");
        // Driver (MySQL) is used to connect this JAVA Backend to MySQL Database
        // Unregister JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                // We are freeing memory with all available registered ( created ) drivers ( ex : Mysql driver which we use in this project )
                DriverManager.deregisterDriver(driver);
                AbandonedConnectionCleanupThread.checkedShutdown();
                System.out.println("Deregistered JDBC driver: " + driver);
            } catch (Exception e) {
                System.err.println("Error deregistering driver: " + driver + " - " + e.getMessage());
            }
        }
    }
}
