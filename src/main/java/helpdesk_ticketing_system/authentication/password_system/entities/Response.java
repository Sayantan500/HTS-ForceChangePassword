package helpdesk_ticketing_system.authentication.password_system.entities;

public class Response
{
    private final Integer status;
    private final String message;

    public Response(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
