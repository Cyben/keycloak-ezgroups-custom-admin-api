package ez.keycloak;

import org.keycloak.common.ClientConnection;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.ForbiddenException;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotAuthorizedException;

import org.jboss.logging.Logger;

public class EzGroupsAdminResource extends AbstractAdminResource {
    private static final Logger logger = Logger.getLogger(EzGroupsAdminResource.class);
//    private AdminPermissionEvaluator realmAuth;
//    private final KeycloakSession keycloakSession;
//
//    private AppAuthManager authManager;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private ClientConnection clientConnection;

//    private AdminAuth auth;

    public EzGroupsAdminResource(KeycloakSession keycloakSession) {
        super(keycloakSession);
//        this.realmAuth = AdminPermissions.evaluator(keycloakSession, realm, this.auth);

//        this.keycloakSession = keycloakSession;
//        this.authManager = new AppAuthManager();
    }
//
//    public void init() {
//        RealmModel realm = this.keycloakSession.getContext().getRealm();
//        this.auth = authenticateRealmAdminRequest();
//
//        RealmManager realmManager = new RealmManager(this.keycloakSession);
//        if (realm == null) throw new NotFoundException("Realm not found.");
//
//        if (!this.auth.getRealm().equals(realmManager.getKeycloakAdminstrationRealm())
//                && !this.auth.getRealm().equals(realm)) {
//            throw new org.keycloak.services.ForbiddenException();
//        }
//        this.realmAuth = AdminPermissions.evaluator(this.keycloakSession, realm, this.auth);
//    }


    @GET
    @Path("/true") // TODO: fix api call
    public Response test() {
        try {
            if (this.auth == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            canManageGroups();

            // TODO: implement code

            return Response.ok().build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("Error using this endpoint", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

//    protected AdminAuth authenticateRealmAdminRequest() {
//        String tokenString = authManager.extractAuthorizationHeaderToken(this.httpHeaders);
//        if (tokenString == null) throw new NotAuthorizedException("Bearer");
//        AccessToken token;
//        try {
//            JWSInput input = new JWSInput(tokenString);
//            token = input.readJsonContent(AccessToken.class);
//        } catch (JWSInputException e) {
//            throw new NotAuthorizedException("Bearer token format error");
//        }
//        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
//        RealmManager realmManager = new RealmManager(this.keycloakSession);
//        RealmModel realm = realmManager.getRealmByName(realmName);
//        if (realm == null) {
//            throw new NotAuthorizedException("Unknown realm in token");
//        }
//        this.keycloakSession.getContext().setRealm(realm);
//        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(tokenString, this.keycloakSession, realm, this.keycloakSession.getContext().getUri(), clientConnection, httpHeaders);
//        if (authResult == null) {
//            logger.info("Token not valid");
//            throw new NotAuthorizedException("Bearer");
//        }
//
//        ClientModel client = realm.getClientByClientId(token.getIssuedFor());
//        if (client == null) {
//            throw new NotFoundException("Could not find client for authorization");
//
//        }
//        return new AdminAuth(realm, authResult.getToken(), authResult.getUser(), client);
//    }

    private void canManageGroups() {
        if (!this.realmAuth.groups().canManage()) {
            logger.info("User does not have permission to manage groups");
            throw new ForbiddenException("User does not have permission to manage groups");
        }
    }
}
