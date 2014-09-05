import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServiceRunner {

    static final int defaultPort = 8080;

    public static void main(String[] args) throws Exception {

        Server server = new Server(defaultPort);
        final ServletContextHandler context = new ServletContextHandler();

        context.addServlet(new ServletHolder(new MainServlet()), "/");
        context.addServlet(new ServletHolder(new Controller()), "/control");

        server.setHandler(context);
        server.start();
    }
}
