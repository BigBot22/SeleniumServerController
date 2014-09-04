import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.jetty.util.ajax.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SeleniumServerController {

    static private String controllerServerURL = "http://172.18.67.72:8080/control";


    public String start(String commandLine) {

        Map<String, String> state = getState();

        if (!state.get("commandLine").equals(commandLine)) {
            if (state.get("state").equals("true")) {
                return setState("restart", commandLine);
            } else {
                return setState("start", commandLine);
            }
        }

        return "selenium server is already running with commandLine:" + commandLine;
    }

    public String stop() {

        return setState("stop", "-");
    }

    public Map<String, String> getState() {
        HttpClient client = new HttpClient();

        URI uri;
        GetMethod method = new GetMethod();

        try {
            uri = new URI(controllerServerURL, true);
            method.setURI(uri);
        } catch (URIException e) {
            e.printStackTrace();
        }

        method.setQueryString("query=getState");

        int returnCode = 0;
        String stateJson = null;
        try {
            returnCode = client.executeMethod(method);
            stateJson = method.getResponseBodyAsString();
            method.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> map;

        assert (returnCode == 200);

        if (returnCode == 200) {
            map = (Map) JSON.parse(stateJson);
        } else {
            map = new HashMap<>();
            map.put("state", "getState filed:" + returnCode);
            map.put("commandLine", stateJson);
        }

        return map;
    }

    private String setState(String state, String commandLine) {
        HttpClient client = new HttpClient();

        URI uri;
        GetMethod method = new GetMethod();
        try {
            uri = new URI("http://172.18.67.72:8080/control", true);
            method.setURI(uri);
        } catch (URIException e) {
            e.printStackTrace();
        }

        String jsonStr = "{\"state\":\"" + state + "\",\"commandLine\":\"" + commandLine + "\"}";

        String encodedQueryString = null;
        try {
            encodedQueryString = (URIUtil.encodeQuery("json=" + jsonStr));
        } catch (URIException e) {
            e.printStackTrace();
        }

        method.setQueryString(encodedQueryString);

        int returnCode = 0;
        String responseBodyString = null;
        try {
            returnCode = client.executeMethod(method);
            responseBodyString = method.getResponseBodyAsString();
            method.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBodyString;
    }

}
