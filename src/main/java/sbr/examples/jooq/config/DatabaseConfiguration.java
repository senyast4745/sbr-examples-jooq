package sbr.examples.jooq.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * @author senyasdr
 */
public class DatabaseConfiguration {

    private DatabaseConfiguration() {
    }

    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "password";
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final SQLDialect DIALECT = SQLDialect.POSTGRES;


    public static DSLContext getContext() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        return DSL.using(cn, DIALECT);
    }

}
