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

public class CallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the authorization code from the request
        String code = request.getParameter("code");
        String error = request.getParameter("error");

        if (error != null) {
            // Handle errors such as user denial of consent
            response.getWriter().write("Error received from authorization server: " + error);
            return;
        }

        if (code != null) {
            // Exchange the authorization code for an access token
            String accessToken = exchangeAuthorizationCodeForAccessToken(code);

            if (accessToken != null) {
                // Here you would typically redirect to another page or perform some action with the access token
                response.getWriter().write("Access Token: " + accessToken);
            } else {
                response.getWriter().write("Failed to retrieve access token");
            }
        }
    }

    private String exchangeAuthorizationCodeForAccessToken(String code) {
        String tokenEndpoint = "https://localhost:9443/oauth2/token";
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tokenEndpoint);

        try {
            // Basic authentication header may be required depending on your OAuth provider
            String clientId = "A22V4a9sdM3KoF3ZnIip97u0RRIa";
            String clientSecret = "Ka0XKfql85MXfOoHLCUyYNTcKEAa";
            String redirectUri = "http://localhost:8080/callback"; // This should match the redirect URI registered with the OAuth provider

            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
            httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // Set the request body
            StringEntity entity = new StringEntity(
                    "grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri
            );
            httpPost.setEntity(entity);

            // Execute the HTTP request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}