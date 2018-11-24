package com.github.javydreamercsw.database.storage.db.server;

import org.openide.util.NbBundle.Messages;

/**
 * Enumeration to describe the database state
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Messages({
    "message.db.valid=Database is valid and current",
    "message.update.db=Database needs to be updated",
    "message.updated.db=Database was updated to the latest version (%v)",
    "message.db.error=Error detected, please check server logs and/or contact your system administrator.",
    "message.db.startup=Database is starting up.",
    "message.init.db=Database needs to be initialized. Please wait.",
    "message.db.locked=Database is locked."
})
public enum DBState {

    /*
     * Database is up to date
     */
    VALID("message.db.valid"),
    /*
     * Database needs a manual update
     */
    NEED_MANUAL_UPDATE("error.old.db"),
    /*
     * Database just updated
     */
    UPDATED("message.updated.db"),
    /*
     * Database needs to be updated
     */
    NEED_UPDATE("message.update.db"),
    /*
     * Database needs initialization
     */
    NEED_INIT("message.init.db"),
    /*
     * Error detected
     */
    ERROR("message.db.error"),
    /*
     * Start up
     */
    START_UP("message.db.startup"),
    /*
     * Updating
     */
    UPDATING("message.update.db");
    private final String mess;

    DBState(String mess) {
        this.mess = mess;
    }

    /**
     * @return the mess
     */
    public String getMessage() {
        return org.openide.util.NbBundle.getMessage(DBState.class, mess);
    }
}
