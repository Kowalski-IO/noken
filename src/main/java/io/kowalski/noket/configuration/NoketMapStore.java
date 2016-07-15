package io.kowalski.noket.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;

import com.hazelcast.core.MapStore;

import io.kowalski.noket.models.RevokedToken;
import io.kowalski.noket.models.RevokedTokenAR;

public class NoketMapStore implements MapStore<String, RevokedToken> {

    @Override
    public RevokedToken load(final String key) {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://127.0.0.1/noket", "postgres", "postgres");
        final RevokedTokenAR rt = RevokedTokenAR.findFirst("jti = ?", key);

        Base.close();

        return rt == null ? null : new RevokedToken(rt.getString("iss"), rt.getString("jti"), rt.getDate("exp"));
    }

    @Override
    public Map<String, RevokedToken> loadAll(final Collection<String> keys) {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://127.0.0.1/noket", "postgres", "postgres");
        final Map<String, RevokedToken> map = new HashMap<String, RevokedToken>();
        final List<RevokedTokenAR> revokedTokens = RevokedTokenAR.findAll();

        for (final RevokedTokenAR revokedToken : revokedTokens) {
            map.put(revokedToken.getString("jti").concat("$").concat(revokedToken.getString("iss")), new RevokedToken(
                    revokedToken.getString("iss"), revokedToken.getString("jti"), revokedToken.getDate("exp")));
        }

        Base.close();

        return map;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        final List<String> forceLoad = new ArrayList<String>();
        forceLoad.add("fakeKeyToForceLoading");
        return forceLoad;
    }

    @Override
    public void store(final String key, final RevokedToken token) {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://127.0.0.1/noket", "postgres", "postgres");

        RevokedTokenAR.createIt("jti", token.getJti(), "iss", token.getIss(), "exp", token.getExp());

        Base.close();
    }

    @Override
    public void storeAll(final Map<String, RevokedToken> map) {
        for (final Map.Entry<String, RevokedToken> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void delete(final String key) {

    }

    @Override
    public void deleteAll(final Collection<String> keys) {

    }

}
