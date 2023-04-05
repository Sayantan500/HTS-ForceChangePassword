package helpdesk_ticketing_system.authentication.password_system.service;

import com.amazonaws.services.lambda.runtime.Context;
import helpdesk_ticketing_system.authentication.password_system.entities.Response;

public interface PasswordManagement {
    Response forceChangePassword(String session, String username, String password, Context context);
}
