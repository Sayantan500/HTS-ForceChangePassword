package helpdesk_ticketing_system.authentication.password_system;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import helpdesk_ticketing_system.authentication.password_system.entities.ForceChangePasswordRequestEvent;
import helpdesk_ticketing_system.authentication.password_system.entities.Response;
import helpdesk_ticketing_system.authentication.password_system.service.PasswordManagement;
import helpdesk_ticketing_system.authentication.password_system.service.PasswordManagementImpl;
import helpdesk_ticketing_system.authentication.password_system.utility.CognitoClient;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PasswordEventsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
    private final Gson gson;
    private final PasswordManagement passwordManagement;
    private final String USERNAME;
    private final Map<String,String> headers;

    public PasswordEventsHandler(){
        gson = new GsonBuilder().setPrettyPrinting().create();
        CognitoClient cognitoClient = new CognitoClient();
        passwordManagement = new PasswordManagementImpl(cognitoClient);
        this.USERNAME = System.getenv("username_path_param_name");
        headers = new HashMap<>();
        headers.put("Content-Type","application/json; charset=utf-8");
    }
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        if(requestEvent==null)
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .withHeaders(headers)
                    .withBody("\"message\":\"Received Null Response from Gateway\"");

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent;

        ForceChangePasswordRequestEvent changePasswordOnFirstLoginEvent =
                gson.fromJson(requestEvent.getBody(), ForceChangePasswordRequestEvent.class);

        String username = requestEvent.getPathParameters().get(USERNAME);

        // the session_token & password must not be blank
        if(StringUtils.isNotBlank(changePasswordOnFirstLoginEvent.getSession_token())
                && StringUtils.isNotBlank(changePasswordOnFirstLoginEvent.getPassword())
        )
        {
            Response response = passwordManagement.forceChangePassword(
                    changePasswordOnFirstLoginEvent.getSession_token(),
                    username,
                    changePasswordOnFirstLoginEvent.getPassword(),
                    context
            );
            apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(response.getStatus())
                    .withHeaders(headers)
                    .withBody(gson.toJson(response));
        }
        // either of the required parameter is missing
        else
            apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.BAD_REQUEST)
                    .withHeaders(headers)
                    .withBody("\"message\":\"Session Token or Password is not present.\"");

        return apiGatewayProxyResponseEvent;
    }
}
