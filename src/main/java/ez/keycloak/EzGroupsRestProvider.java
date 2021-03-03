package ez.keycloak;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class EzGroupsRestProvider implements RealmResourceProvider {

    private final KeycloakSession keycloakSession;

    public EzGroupsRestProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public Object getResource() {
        return new EzGroupsResource(this.keycloakSession);
    }

    @Override
    public void close() {
    }

}
