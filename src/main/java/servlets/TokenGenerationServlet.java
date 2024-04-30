package servlets;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenGenerationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Client credentials - ideally, these should be stored securely and not hardcoded
        String clientId = "your_client_id";
        String clientSecret = "your_client_secret";

        // URL of the WSO2 token endpoint
        String tokenEndpoint = "https://localhost:9443/oauth2/token";

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(tokenEndpoint);

            // Basic authentication header
            String credentials = "A22V4a9sdM3KoF3ZnIip97u0RRIa" + ":" + "Ka0XKfql85MXfOoHLCUyYNTcKEAa";
            String encodedCredentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
            httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // Set the request entity
            StringEntity entity = new StringEntity("grant_type=client_credentials");
            httpPost.setEntity(entity);

            // Execute the HTTP request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

            // Parse and retrieve the access token
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String accessToken = jsonObject.getString("access_token");

            // Set response type to JSON and output the access token
            response.setContentType("application/json");
            response.getWriter().println("{\"access_token\": \"" + accessToken + "\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error fetching token: " + e.getMessage());
        }
    }
}