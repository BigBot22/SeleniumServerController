import java.util.Map;
import java.util.logging.Logger;

public class HttpController {

    private final static Logger LOGGER = Logger.getLogger(HttpController.class.getName());

    public static void main(String args[]) throws Exception {

        SeleniumServerController controller = new SeleniumServerController();
        Map<String, String> state;

        state = controller.getState();
        LOGGER.info("HttpController getStateResult:" + " state:" + state.get("state") + " commandLine:" + state.get("commandLine") +
                " exitValue:" + state.get("exitValue") + " exception:" + state.get("exception"));


        LOGGER.info("startReturn:" + controller.start("java -jar target/classes/selenium-server-standalone-2.42.2.jar -port 4444"));


//        LOGGER.info("stopReturn:" + controller.stop());


        state = controller.getState();
        LOGGER.info("getStateResult:" + " state:" + state.get("state") + " commandLine:" + state.get("commandLine") +
                " exitValue:" + state.get("exitValue") + " exception:" + state.get("exception"));
    }
}
