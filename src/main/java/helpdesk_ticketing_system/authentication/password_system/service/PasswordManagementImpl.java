package helpdesk_ticketing_system.authentication.password_system.service;

import com.amazonaws.services.lambda.runtime.Context;
import helpdesk_ticketing_system.authentication.password_system.entities.Response;
import helpdesk_ticketing_system.authentication.password_system.utility.CognitoClient;
import helpdesk_ticketing_system.authentication.password_system.utility.HashingUtils;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordManagementImpl implements PasswordManagement
{
    private final CognitoClient cognitoClient;

    public PasswordManagementImpl(CognitoClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Override
    public Response forceChangePassword(String session, String username, String password, Context context) {
        // if username is not found
        if(!isUsernameFound(username))
            return new Response(HttpStatusCode.NOT_FOUND,"Username Not Found.");

        //when username is found go forward and change password
        Map<String,String> challengeResponse = new LinkedHashMap<>();
        challengeResponse.put("NEW_PASSWORD",password);
        challengeResponse.put("USERNAME",username);
        challengeResponse.put("SECRET_HASH",findPasswordSecretHash(username));

        AdminRespondToAuthChallengeRequest adminRespondToAuthChallengeRequest =
                AdminRespondToAuthChallengeRequest.builder()
                        .clientId(cognitoClient.getClientID())
                        .userPoolId(cognitoClient.getUserPoolId())
                        .session(session)
                        .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                        .challengeResponses(challengeResponse)
                        .build();

        try{
            AdminRespondToAuthChallengeResponse adminRespondToAuthChallengeResponse =
                    cognitoClient.getCognitoIdentityProviderClient()
                            .adminRespondToAuthChallenge(adminRespondToAuthChallengeRequest);
            if(adminRespondToAuthChallengeResponse.authenticationResult()!=null)
            {
                SdkHttpResponse sdkHttpResponse = adminRespondToAuthChallengeResponse.sdkHttpResponse();
                String message = sdkHttpResponse.statusText().isPresent()?sdkHttpResponse.statusText().get() : null;
                return new Response(sdkHttpResponse.statusCode(),message);
            }
        }catch (CognitoIdentityProviderException exception){
            context.getLogger().log("Request ID : " + exception.requestId() + "\nMessage : " + exception.getMessage());
            return new Response(exception.statusCode(),exception.getMessage());
        }
        return new Response(HttpStatusCode.INTERNAL_SERVER_ERROR,null);
    }

    private String findPasswordSecretHash(String username)
    {
        if (cognitoClient.getClientID() != null || cognitoClient.getClientID().length() > 0) {
            return HashingUtils.computeSecretHash(
                    cognitoClient.getClientID(),
                    cognitoClient.getClientSecret(),
                    username
            );
        }
        return null;
    }

    private boolean isUsernameFound(String username) {
        try{
            cognitoClient.getCognitoIdentityProviderClient()
                    .adminGetUser(AdminGetUserRequest.builder()
                            .username(username)
                            .userPoolId(cognitoClient.getUserPoolId())
                            .build());
            return true;
        }catch (UserNotFoundException userNotFoundException){
            return false;
        }

    }
}
