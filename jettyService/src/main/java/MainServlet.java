import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Страница MainServlet. Работает только с get-Запросами. http://127.0.0.1:8080?nameParam=name
 */
public class MainServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.getWriter().println("Main page of super service.\nTo run selenium server go to: /control");
        resp.getWriter().println("\n\n\n\n\n How To: http://172.18.67.72:8080/control");
        resp.setStatus(HttpStatus.OK_200);

    }
}
