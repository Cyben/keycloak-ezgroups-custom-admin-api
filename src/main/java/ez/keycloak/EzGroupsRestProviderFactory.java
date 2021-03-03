package ez.keycloak;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class EzGroupsRestProviderFactory implements RealmResourceProviderFactory {
    private static final String ID = "ez-groups";

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new EzGroupsRestProvider(keycloakSession);
    }

    @Override
    public void init(Scope scope) {}

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return ID;
    }
}
