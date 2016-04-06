package de.qabel.desktop.repository.sqlite.migration;

import de.qabel.desktop.repository.sqlite.MigrationException;

public class MigrationFailedException extends MigrationException {
    private AbstractMigration failedMigration;

    public MigrationFailedException(AbstractMigration failedMigration, String message) {
        super(message);
        this.failedMigration = failedMigration;
    }

    public MigrationFailedException(AbstractMigration failedMigration, String message, Throwable cause) {
        super(message, cause);
        this.failedMigration = failedMigration;
    }

    public AbstractMigration getFailedMigration() {
        return failedMigration;
    }
}
