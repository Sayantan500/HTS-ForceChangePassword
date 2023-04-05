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

public class PasswordEventsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
    private final Gson gson;
    private final PasswordManagement passwordManagement;

    public PasswordEventsHandler(){
        gson = new GsonBuilder().setPrettyPrinting().create();
        CognitoClient cognitoClient = new CognitoClient();
        passwordManagement = new PasswordManagementImpl(cognitoClient);
    }
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        if(requestEvent==null)
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .withBody("Received Null Response from Gateway");

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent;

        ForceChangePasswordRequestEvent changePasswordOnFirstLoginEvent =
                gson.fromJson(requestEvent.getBody(), ForceChangePasswordRequestEvent.class);

        context.getLogger().log("Event : " + changePasswordOnFirstLoginEvent);

        // the session_token, username & password must not be blank
        if(StringUtils.isNotBlank(changePasswordOnFirstLoginEvent.getSession_token())
                && StringUtils.isNotBlank(changePasswordOnFirstLoginEvent.getUsername())
                && StringUtils.isNotBlank(changePasswordOnFirstLoginEvent.getPassword())
        )
        {
            Response response = passwordManagement.forceChangePassword(
                    changePasswordOnFirstLoginEvent.getSession_token(),
                    changePasswordOnFirstLoginEvent.getUsername(),
                    changePasswordOnFirstLoginEvent.getPassword(),
                    context
            );
            apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(response.getStatus()).withBody(response.getMessage());
        }
        // either of the required parameter is missing
        else
            apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(HttpStatusCode.BAD_REQUEST)
                    .withBody("Session Token or Username or Password is not present.");

        return apiGatewayProxyResponseEvent;
    }
}
