package helpdesk_ticketing_system.authentication.password_system.utility;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

public class CognitoClient
{
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private final String clientID;
    private final String clientSecret;
    private final String userPoolId;

    public CognitoClient(){
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        System.getenv("aws_access_key_id"),
                        System.getenv("aws_secret_access_key")
                )))
                .region(Region.of(System.getenv("aws_region")))
                .build();
        clientID = System.getenv("cognito_app_client_id");
        clientSecret = System.getenv("cognito_app_client_secret");
        userPoolId = System.getenv("aws_cognito_userPoolId");
    }

    public CognitoIdentityProviderClient getCognitoIdentityProviderClient() {
        return cognitoIdentityProviderClient;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUserPoolId() {
        return userPoolId;
    }
}
