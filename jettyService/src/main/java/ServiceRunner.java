import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServiceRunner {

    static int defaultPort = 8080;
    static String defaultCommaondLine = "";

    public static void main(String[] args) throws Exception {

        if (args.length == 1) {
            defaultPort = Integer.parseInt(args[0]);
        }

        if (args.length == 2) {
            defaultPort = Integer.parseInt(args[0]);
            defaultCommaondLine = args[1];
        }

        Server server = new Server(defaultPort);
        final ServletContextHandler context = new ServletContextHandler();

        context.addServlet(new ServletHolder(new MainServlet()), "/");
        context.addServlet(new ServletHolder(new Controller(defaultCommaondLine)), "/control");

        server.setHandler(context);
        server.start();
    }
}
