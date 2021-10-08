package sbr.examples.jooq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStep2;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import sbr.examples.jooq.entity.tables.Users;
import sbr.examples.jooq.model.UserDto;
import sbr.examples.jooq.model.UserUpsertDto;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * @author senyasdr
 */
public class UsersRepository {

    private static final Table<Record> TABLE = table("users");

    private static class Columns {
        public static final Field<Long> ID = field("id", Long.class);
        public static final Field<String> LOGIN = field("login", String.class);
        public static final Field<String> PASSWORD = field("password", String.class);
    }

    private final DSLContext context;

    public UsersRepository(DSLContext context) {
        this.context = context;
    }


    public Long save(UserUpsertDto user) {
        if (user.id.isPresent()) {
            return context.update(TABLE)
                    .set(Columns.LOGIN, user.login)
                    .set(Columns.PASSWORD, user.password)
                    .where(Columns.ID.eq(user.id.get())).returning(Columns.ID)
                    .fetchOne().get(Columns.ID);
        }
        return context.insertInto(TABLE, Columns.LOGIN, Columns.PASSWORD)
                .values(user.login, user.password)
                .onConflict(Columns.LOGIN)
                .doUpdate()
                .set(Columns.PASSWORD, user.password)
                .returning(Columns.ID)
                .fetchOne().get(Columns.ID);
    }

    public void saveAll(List<UserUpsertDto> users) {
        InsertValuesStep2<Record, String, String> query = context.insertInto(TABLE, Columns.LOGIN, Columns.PASSWORD);
        users.forEach(user -> query.values(user.login, user.password).onConflict(Columns.LOGIN).doUpdate()
                .set(Columns.PASSWORD, user.password));
        query.execute();
    }

    public UserDto saveGen(UserUpsertDto user) {
        return context.insertInto(Users.USERS).set(context.newRecord(Users.USERS, user))
                .onConflict(Columns.LOGIN)
                .doUpdate()
                .set(Columns.PASSWORD, UUID.randomUUID().toString())
                .returning()
                .fetchOptional()
                .map(r ->
                        r.into(UserDto.class))
                .orElseThrow(IllegalArgumentException::new);
    }

    public Optional<UserDto> findById(Long id) {
        Optional<Record> res = context.select().from(TABLE).where(Columns.ID.eq(id)).fetchOptional();
        return res.map(r -> new UserDto(r.get(Columns.ID), r.get(Columns.LOGIN), r.get(Columns.PASSWORD)));
    }

    private SelectConditionStep<Record> byIdSelect(Long id) {
        return context.select().from(TABLE).where(Columns.ID.eq(id));
    }

    public List<Record> byIdOrTestLogin(Long id) {
        return byIdSelect(id).or(Columns.LOGIN.like("test%")).fetch();
    }

    public List<Record> getIfQueryPresents(Long id, Optional<Condition> conditionO) {
        SelectConditionStep<Record> q = byIdSelect(id);
        conditionO.ifPresent(q::or);
        return q.fetch();
    }

    public void clean() {
        context.delete(TABLE).execute();
    }
}
