import org.apache.commons.exec.*;
import org.eclipse.jetty.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class Controller extends HttpServlet {

    boolean isRunning;
    String runningCommandLine;
    int exitValue;
    String exception;

    SeleniumResultHandler resultHandler;
    ExecuteWatchdog watchdog;

    private final static Logger LOGGER = Logger.getLogger(Controller.class.getName());

    public Controller() {

        isRunning = false;
        runningCommandLine = "-";
        exitValue = Integer.MAX_VALUE;
        exception = null;

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String jsonStr = req.getParameter("json");
        if (jsonStr != null && jsonStr.length() > 0) {

            final Map<String, String> jsonParsed = (Map<String, String>) JSON.parse(jsonStr);

            resp.getWriter().println(handelStat(jsonParsed.get("state"), jsonParsed.get("commandLine")));
        }

        String query = req.getParameter("query");
        if (query != null && query.length() > 0 && query.equals("getState")) {

            final Map<String, String> map = new HashMap<>();

            map.put("state", "" + isRunning);

            map.put("commandLine", runningCommandLine);

            map.put("exitValue", "" + exitValue);

            map.put("exception", exception);

            resp.getWriter().println(new JSON().toJSON(map));
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }

    String handelStat(String potentialState, String commandLine) {
        if (potentialState.equals("start")) {
            if (!isRunning) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {

                    startServer(commandLine);

                } catch (IOException e) {
                    LOGGER.info(e.getMessage());
                    return "Starting selenium server was failed. Command line:" + commandLine +
                            "\n IOException was thrown:\n" + e.getMessage();
                }

                isRunning = true;
                runningCommandLine = commandLine;
                exitValue = Integer.MAX_VALUE;
                exception = "-";
                return "selenium server started with command line:" + commandLine;

            } else {
                return "server has been already started with commandLine:" + runningCommandLine;
            }
        }

        if (potentialState.equals("stop")) {
            if (isRunning) {

                if (stoppingProcess() == 0) {
                    return "Selenium server has been killed successfully.\n" +
                            "running:" + isRunning + " runningCommandLine:" + runningCommandLine +
                            " exitValue:" + exitValue + " exception:" + exception;


                } else {
                    return "selenium server killing failed:\n" +
                            "running:" + isRunning + " runningCommandLine:" + runningCommandLine +
                            " exitValue:" + exitValue + " exception:" + exception; // TODO send exception to
                }

            } else {
                return "selenium server runningState is already:" + isRunning;
            }
        }

        if (potentialState.equals("restart")) {
            if (isRunning) {
                if (stoppingProcess() != 0) {
                    return "selenium server killing failed:\n" +
                            "running:" + isRunning + " runningCommandLine:" + runningCommandLine +
                            " exitValue:" + exitValue + " exception:" + exception; // TODO send exception to
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                startServer(commandLine);
            } catch (final Exception e) {
                LOGGER.info(e.getMessage());
                return "Restarting selenium server was failed:\n" +
                        "running:" + isRunning + " runningCommandLine:" + runningCommandLine +
                        " exitValue:" + exitValue + " exception:" + exception + " Exception" + e.getMessage();
            }

            isRunning = true;
            runningCommandLine = commandLine;
            exitValue = Integer.MAX_VALUE;
            exception = "-";

            return "selenium server has been restarted with command line:" + commandLine;
        }

        return potentialState + " has not implemented";
    }

    private void startServer(String commandLine) throws IOException {

        LOGGER.info("starting server");
        final Executor executor = new DefaultExecutor();
        watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
        executor.setWatchdog(watchdog);
        executor.setExitValue(1);

        LOGGER.info("executing non-blocking selenium server job  ...");
        resultHandler = new SeleniumResultHandler(watchdog);

        //TODO normal parsing of selenium server file in cmdLine
        StringTokenizer stringTokenizer = new StringTokenizer(commandLine, " ");
        stringTokenizer.nextToken(); //java
        stringTokenizer.nextToken(); //-jar

        String seleniumServerName = stringTokenizer.nextToken(); //selenium-server-standalone-2.42.2.jar
        File f = new File(seleniumServerName);
        if (!f.exists() || f.isDirectory()) {
            throw new IOException("file doesn't exist:" + seleniumServerName);
        }

        executor.execute(CommandLine.parse(commandLine), resultHandler);

    }

    private int stoppingProcess() {
        LOGGER.info("try to kill selenium server");

        watchdog.destroyProcess();

        if (watchdog.killedProcess()) {
            LOGGER.info("selenium server has been killed successfully");
            isRunning = false;
            runningCommandLine = "-";
            exitValue = resultHandler.getExitValue();

            if (resultHandler.getException() != null) {
                exception = resultHandler.getException().getMessage();
            } else {
                exception = "null";
            }

            resultHandler = null;
            watchdog = null;
            return 0;

        } else {

            LOGGER.info("selenium server killing failed");
            try {
                watchdog.checkException();

            } catch (Exception e) {
                exception = "Selenium server killing failed. Exception from selenium server watchdog:\n" + e.getMessage();
                LOGGER.info(exception);
                return -2;
            }

            exception = "Selenium server killing failed with no exception.";

            return -1;
        }

    }

    private class SeleniumResultHandler extends DefaultExecuteResultHandler {

        ExecuteWatchdog watchdog;

        public SeleniumResultHandler(ExecuteWatchdog watchdog) {
            super.onProcessComplete(exitValue);
            this.watchdog = watchdog;
        }

        @Override
        public void onProcessComplete(int exitVal) {
            super.onProcessComplete(exitVal);
            LOGGER.info("onProcessComplete exitValue:" + exitVal);

            isRunning = false;
            runningCommandLine = "-";
            exitValue = exitVal;
            exception = "-";
//            resultHandler = null;
//            watchdog = null;


        }

        @Override
        public void onProcessFailed(final ExecuteException e) {
            super.onProcessFailed(e);
            LOGGER.info("onProcessFailed: selenium server Failed");

            isRunning = false;
            runningCommandLine = "-";
            exitValue = -5;

//            resultHandler = null;

            if (watchdog != null && watchdog.killedProcess()) {
                exception = "[resultHandler] The print process timed out";
            } else {
                exception = "[resultHandler] The print process failed to do : " + e.getMessage();
            }
//            watchdog = null;

        }
    }

}
