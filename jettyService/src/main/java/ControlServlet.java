import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//  http://172.18.67.72:8080/control?state=restart
public class ControlServlet extends HttpServlet {

    CommandLine commandLine;
    String runningCommandLine;
    DefaultExecutor executor;
    DefaultExecuteResultHandler resultHandler;
    ExecuteWatchdog watchdog;
//    private java.util.logging.Logger logger;

    public ControlServlet() {
        commandLine = CommandLine.parse("java -jar selenium-server-standalone-2.42.2_.jar -port 4444");
        runningCommandLine = "-";
        executor = new DefaultExecutor();
        executor.setExitValue(1); // success end
        watchdog = new ExecuteWatchdog(10 * 60 * 1000); //kills a run-away process after 5 seconds.
        executor.setWatchdog(watchdog);
        resultHandler = null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String potentialCommandLine = req.getParameter("command");
        if (potentialCommandLine != null && potentialCommandLine.length() > 0) {
            commandLine = CommandLine.parse(potentialCommandLine);
        }

        String potentialState = req.getParameter("query");
        if (potentialState != null && potentialState.length() > 0) {
            handelStat(potentialState);
        }

        resp.getWriter().println("Control Servlet.\n");
        resp.getWriter().println("Server state:" + (resultHandler != null ? "running with commandLine:"
                + runningCommandLine : "stopped"));
        resp.getWriter().println("commandLine:" + commandLine);

        resp.getWriter().println("\n\n\n\n\nHow To:");
        resp.getWriter().println("172.18.67.72:8080/control?query=start");
        resp.getWriter().println("172.18.67.72:8080/control?query=stop");
        resp.getWriter().println("172.18.67.72:8080/control?query=restart");
        resp.getWriter().println("172.18.67.72:8080/control?command=java -jar selenium-server-standalone-2.42.2.jar -port 4444");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }

    void handelStat(String potentialState) {
        if (potentialState.equals("start") && resultHandler == null) {
            System.out.println("ControlServlet" + " handelStat" + " start");
            try {
                System.out.println("commandLine:" + commandLine.toString());
                resultHandler = new DefaultExecuteResultHandler();
                executor.execute(commandLine, resultHandler);
                runningCommandLine = commandLine.toString();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("process exit with exitValue ");
            }
        }

        if (potentialState.equals("stop") && resultHandler != null) {
            System.out.println("ControlServlet" + " handelStat" + " stop");
//            resultHandler.onProcessFailed(new ExecuteException("executeException", -1));
//            resultHandler.onProcessComplete(0);

//            watchdog.destroyProcess();
            while (!watchdog.killedProcess()) {
                watchdog.destroyProcess();
                System.out.println("killing");
            }

//            System.out.println("process exit with exitValue " + resultHandler.getExitValue());
            System.out.println("process exit with exitValue ");
            resultHandler = null;
            runningCommandLine = "-";
        }

        if (potentialState.equals("restart")) {
            System.out.println("ControlServlet" + " handelStat" + " restart");
            if (resultHandler != null) {
//                resultHandler.onProcessComplete(1);

//                watchdog.destroyProcess();
                while (!watchdog.killedProcess()) {
                    watchdog.destroyProcess();
                    System.out.println("killing");
                }

                System.out.println("process exit with exitValue ");
                runningCommandLine = "-";
            }

            try {
                executor.execute(commandLine, resultHandler);
                runningCommandLine = commandLine.toString();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("process exit with exitValue ");
            }
        }
    }
}
