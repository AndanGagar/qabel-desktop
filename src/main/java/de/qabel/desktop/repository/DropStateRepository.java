package de.qabel.desktop.repository;

import de.qabel.desktop.repository.exception.EntityNotFoundException;
import de.qabel.desktop.repository.exception.PersistenceException;

/**
 * Loads and saves "states" of "drops".
 * A drop is identified by the url on the drop server (including the latter).
 * The state can be anything the server says (and understands) like a timestamp, an E-Tag, etc.
 */
public interface DropStateRepository {
    String getDropState(String drop) throws EntityNotFoundException, PersistenceException;
    void setDropState(String drop, String state) throws PersistenceException;
}
