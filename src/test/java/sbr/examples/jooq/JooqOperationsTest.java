package sbr.examples.jooq;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sbr.examples.jooq.config.DatabaseConfiguration;
import sbr.examples.jooq.entity.tables.Users;
import sbr.examples.jooq.model.UserDto;
import sbr.examples.jooq.model.UserUpsertDto;
import sbr.examples.jooq.repository.UsersRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author senyasdr
 */
class JooqOperationsTest {

    private static DSLContext context;
    private static UsersRepository repository;

    @BeforeAll
    static void setUp() throws SQLException {
        context = DatabaseConfiguration.getContext();
        repository = new UsersRepository(context);
    }

    @AfterEach
    void tearDown() {
        repository.clean();
    }

    @Test
    void assertSaved() {
        //when
        Long id = repository.save(new UserUpsertDto("login", "password"));

        //then
        Optional<UserDto> byIdO = repository.findById(id);
        assertTrue(byIdO.isPresent());
        assertEquals("login", byIdO.get().login);
        assertEquals("password", byIdO.get().password);

        System.out.println("Saved user: " + byIdO);
    }

    @Test
    void assertUpsert() {
        //given
        Long id = repository.save(new UserUpsertDto("login", "password"));

        //when
        Long newId = repository.save(new UserUpsertDto("login", "pwd"));

        //then
        assertEquals(id, newId);
        Optional<UserDto> byIdO = repository.findById(id);
        assertTrue(byIdO.isPresent());
        assertEquals("login", byIdO.get().login);
        assertEquals("pwd", byIdO.get().password);
        System.out.println("Saved user: " + byIdO);
    }

    @Test
    void assertUpdate() {
        //given
        Long id = repository.save(new UserUpsertDto("login", "password"));

        //when
        Long newId = repository.save(new UserUpsertDto(id, "login", "new_pwd"));

        //then
        assertEquals(id, newId);
        Optional<UserDto> byIdO = repository.findById(id);
        assertTrue(byIdO.isPresent());
        assertEquals("login", byIdO.get().login);
        assertEquals("new_pwd", byIdO.get().password);
        System.out.println("Saved user: " + byIdO);
    }

    @Test
    void saveAll() {
        // given
        repository.save(new UserUpsertDto("login2", "pwd"));


        List<UserUpsertDto> users =
                Stream.of(1, 2, 3, 4, 5)
                        .map(i -> new UserUpsertDto("login" + i, UUID.randomUUID().toString()))
                        .collect(Collectors.toList());

        //when
        repository.saveAll(users);

        //then
        List<UserDto> usersFetched = context.select().from("users").fetchInto(UserDto.class);
        assertEquals(5, usersFetched.size());
        Optional<UserDto> secondUser = usersFetched.stream().filter(u -> u.login.equals("login2")).findFirst();
        assertTrue(secondUser.isPresent());
        assertNotEquals("pwd", secondUser.get().password);
    }

    @Test
    void testIfQueryPresent() {

        //given
        Long id = repository.save(new UserUpsertDto("login", "password"));
        List<UserUpsertDto> upsertUsers =
                Stream.of(1, 2, 3, 4, 5)
                        .map(i -> new UserUpsertDto("login" + i, "pwd"))
                        .collect(Collectors.toList());

        repository.saveAll(upsertUsers);

        //when
        List<Record> users = repository.getIfQueryPresents(id, Optional.of(Users.USERS.LOGIN.like("%in2")));


        //then
        assertEquals(2, users.size());

        //when
        users = repository.getIfQueryPresents(id, Optional.empty());

        //then
        assertEquals(1, users.size());
    }

    @Test
    void saveGenerated() {

        //given
        Long id = repository.save(new UserUpsertDto("login", "password"));

        //when
        UserDto user = repository.saveGen(new UserUpsertDto("login", "pwd"));

        //then
        assertEquals(id, user.id);
        assertEquals("login", user.login);
        assertNotEquals("pwd", user.password);
        assertNotEquals("password", user.password);
    }

    @Test
    void testByIdOrTestLogin() {

        //given
        Long id = repository.save(new UserUpsertDto("login", "password"));
        repository.saveGen(new UserUpsertDto("testLogin", "pwd"));

        //when
        List<Record> userRecords = repository.byIdOrTestLogin(id);
        assertEquals(2, userRecords.size());
    }

}