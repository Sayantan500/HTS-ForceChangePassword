package helpdesk_ticketing_system.authentication.password_system;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PasswordEventsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
    private final Gson gson;
    public PasswordEventsHandler(){
        gson = new GsonBuilder().setPrettyPrinting().create();
    }
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log(">> Request Event : " +  gson.toJsonTree(requestEvent) + "\n");
        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
