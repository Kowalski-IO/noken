package io.kowalski.nekot.models;

import java.io.Serializable;

public class Registration implements Serializable {

    private static final long serialVersionUID = 3809211671732452130L;

    private String applicationName;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

}
