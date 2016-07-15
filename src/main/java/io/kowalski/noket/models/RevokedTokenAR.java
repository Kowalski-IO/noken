package io.kowalski.noket.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;
import org.javalite.activejdbc.annotations.Table;

@Table("revoked")
@CompositePK({ "jti", "iss"})
public class RevokedTokenAR extends Model {

}
