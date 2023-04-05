package helpdesk_ticketing_system.authentication.password_system.entities;

public class ForceChangePasswordRequestEvent
{
    private String session_token;
    private String username;
    private String password;

    public ForceChangePasswordRequestEvent() {
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ForceChangePasswordPasswordRequestInputEvent{" +
                ", session_token='" + session_token + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
