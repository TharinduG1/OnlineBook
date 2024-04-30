package servlets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2ResourceServerServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = request.getParameter("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Access Token is required");
            return;
        }

        if (validateAccessToken(accessToken)) {
            response.getWriter().println("Token is valid.");
            response.sendRedirect("ViewBooks.html");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("Invalid or expired token");
        }
    }

    private boolean validateAccessToken(String accessToken) throws IOException {
        String introspectionUrl = "https://localhost:9443/oauth2/introspect";
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(introspectionUrl);

        // Authorization header may be required depending on your OAuth provider
        httpPost.setHeader("Authorization", "Basic " + encodeClientCredentials("A22V4a9sdM3KoF3ZnIip97u0RRIa",
                "Ka0XKfql85MXfOoHLCUyYNTcKEAa"));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setEntity(new StringEntity("token=" + accessToken));

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
        JSONObject jsonObject = new JSONObject(jsonResponse);

        return jsonObject.getBoolean("active");
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        // Base64 encode the client ID and client secret
        String credentials = clientId + ":" + clientSecret;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}