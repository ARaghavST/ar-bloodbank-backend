
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("BloodBank Server started...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("BloodBank Server shutting down... Cleaning up MySQL drivers.");

        // Unregister JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                AbandonedConnectionCleanupThread.checkedShutdown();
                System.out.println("Deregistered JDBC driver: " + driver);
            } catch (Exception e) {
                System.err.println("Error deregistering driver: " + driver + " - " + e.getMessage());
            }
        }
    }
}
