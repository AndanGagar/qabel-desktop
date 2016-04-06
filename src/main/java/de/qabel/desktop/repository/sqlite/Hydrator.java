package de.qabel.desktop.repository.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface Hydrator<T> {
    String[] getFields();
    T hydrateOne(ResultSet resultSet) throws SQLException;
    Collection<T> hydrateAll(ResultSet resultSet) throws SQLException;

    /**
     * force the hydrator to know the instance (and to return it on future hydrates or add it to an EntityManager etc)
     */
    void recognize(T instance);
}
