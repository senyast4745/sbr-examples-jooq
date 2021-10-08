package sbr.examples.jooq.model;

import java.util.Optional;

/**
 * @author senyasdr
 */
public class UserUpsertDto {

    public final Optional<Long> id;
    public final String login;
    public final String password;

    public UserUpsertDto(Long id, String login, String password) {
        this.id = Optional.of(id);
        this.login = login;
        this.password = password;
    }

    public UserUpsertDto(String login, String password) {
        this.id = Optional.empty();
        this.login = login;
        this.password = password;
    }
}
