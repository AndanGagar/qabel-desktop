package de.qabel.desktop.repository.sqlite;

import de.qabel.core.config.Identity;
import de.qabel.desktop.repository.exception.PersistenceException;
import de.qabel.desktop.repository.sqlite.hydrator.PrefixHydrator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class SqlitePrefixRepository extends AbstractSqliteRepository<String> {
    private static final String TABLE_NAME = "prefix";

    public SqlitePrefixRepository(ClientDatabase database) {
        super(database, new PrefixHydrator(), TABLE_NAME);
    }

    public Collection<String> findAll(Identity identity) throws PersistenceException {
        return findAll("identity_id=?", identity.getId());
    }

    public void delete(Identity identity) throws SQLException {
        PreparedStatement dropPrefixes = database.prepare("DELETE FROM " + TABLE_NAME + " WHERE identity_id = ?");
        dropPrefixes.setInt(1, identity.getId());
        dropPrefixes.execute();
    }

    public void store(Identity identity) throws SQLException {
        for (String prefix : identity.getPrefixes()) {
            PreparedStatement prefixStatment = database.prepare(
                "INSERT INTO " + TABLE_NAME + " (identity_id, prefix) VALUES (?, ?)"
            );
            prefixStatment.setInt(1, identity.getId());
            prefixStatment.setString(2, prefix);
            prefixStatment.execute();
        }
    }
}
