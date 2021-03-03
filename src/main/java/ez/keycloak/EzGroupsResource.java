package ez.keycloak;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.Path;

public class EzGroupsResource {

    private final KeycloakSession keycloakSession;

    public EzGroupsResource(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Path("/admin")
    public EzGroupsAdminResource admin() {
        EzGroupsAdminResource service = new EzGroupsAdminResource(this.keycloakSession);
        ResteasyProviderFactory.getInstance().injectProperties(service); //TODO: check why do we need this
        service.setup();
        return service;
    }
}
