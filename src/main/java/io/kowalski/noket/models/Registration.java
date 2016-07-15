package io.kowalski.noket.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Registration implements Serializable {

    private static final long serialVersionUID = 3809211671732452130L;

    private String applicationName;

}
