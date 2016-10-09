
// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

// Extend HttpServlet class
public class ErrorHandler extends HttpServlet {

	// Method to handle GET method request.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Analyze the servlet exception
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}

		// Set response content type
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		String title = "Error/Exception Information";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
		out.println(
				docType + "<html>\n" + "<head><title>" + title + "</title></head>\n" + "<body bgcolor=\"#f0f0f0\">\n");
		out.println("<h2 aligin='center'>" + title + "</h2>");

		if (throwable == null && statusCode == null) {
			out.println("<h2>Error information is missing</h2>");
			out.println("Please return to the <a href=\"" + response.encodeURL("http://localhost:8080/")
					+ "\">Home Page</a>.");
		} else if (statusCode != null) {
			out.println("<table width='100%' border='1' align='center' cellpadding='6'>");
			out.println("<tr><td>CharacterEncoding</td><td>" + request.getCharacterEncoding() + "</td></tr>");
			out.println("<tr><td>ContentType</td><td>" + request.getContentType() + "</td></tr>");
			out.println("<tr><td>ContextPath</td><td>" + request.getContextPath() + "</td></tr>");
			out.println("<tr><td>LocalAddr</td><td>" + request.getLocalAddr() + "</td></tr>");
			out.println("<tr><td>LocalName</td><td>" + request.getLocalName() + "</td></tr>");
			out.println("<tr><td>LocalPort</td><td>" + request.getLocalPort() + "</td></tr>");
			out.println("<tr><td>PathInfo</td><td>" + request.getPathInfo() + "</td></tr>");
			out.println("<tr><td>PathInfoTranslated</td><td>" + request.getPathTranslated() + "</td></tr>");
			out.println("<tr><td>RemoteAddr</td><td>" + request.getRemoteAddr() + "</td></tr>");
			out.println("<tr><td>RemoteHost</td><td>" + request.getRemoteHost() + "</td></tr>");
			out.println("<tr><td>RemoteUser</td><td>" + request.getRemoteUser() + "</td></tr>");
			out.println("<tr><td>RequestURI</td><td>" + request.getRequestURI() + "</td></tr>");
			out.println("<tr><td>RequestURL</td><td>" + request.getRequestURL() + "</td></tr>");
			out.println("<tr><td>EncodeURL</td><td>" + response.encodeURL(request.getContextPath()) + "</td></tr>");
			out.println("<tr><td>StatusCode</td><td>" + statusCode + "</td></tr>");
			out.println("</table>");
		} else {
			out.println("<h2>Error information</h2>");
			out.println("Servlet Name : " + servletName + "</br></br>");
			out.println("Exception Type : " + throwable.getClass().getName() + "</br></br>");
			out.println("The request URI: " + requestUri + "<br><br>");
			out.println("The exception message: " + throwable.getMessage());
		}
		out.println("</body>");
		out.println("</html>");
	}

	// Method to handle POST method request.
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}