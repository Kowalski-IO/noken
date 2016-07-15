package io.kowalski.noket.models;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RevokedToken implements Serializable {

    private static final long serialVersionUID = 2239866272950250530L;

    private final String iss;
    private final String jti;
    private final Date exp;

}
