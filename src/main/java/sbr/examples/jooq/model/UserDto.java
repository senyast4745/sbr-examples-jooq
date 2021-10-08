package sbr.examples.jooq.model;

/**
 * @author senyasdr
 */
public class UserDto {

    public final Long id;
    public final String login;
    public final String password;

    public UserDto(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto(super=" + super.toString() + ", id=" + this.id + ", login=" + this.login + ", password=" +
                this.password + ")";
    }
}
