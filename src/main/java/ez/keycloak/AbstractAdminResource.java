package ez.keycloak;

import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AbstractAdminResource {

    private static final Logger LOG = Logger.getLogger(AbstractAdminResource.class);

    @Context
    protected ClientConnection clientConnection;

    @Context
    private HttpHeaders headers;

    @Context
    private KeycloakSession keycloakSession;

    protected AdminPermissionEvaluator realmAuth;

    protected RealmModel realm;
//    protected T auth;
    protected AdminAuth auth;
//    protected AdminEventBuilder adminEvent;

    public AbstractAdminResource(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
        this.realm = this.keycloakSession.getContext().getRealm();
    }

    public void setup() {
        auth = setupAuth();
        realmAuth = AdminPermissions.evaluator(keycloakSession, realm, this.auth);
    }

    private AdminAuth setupAuth() {
        AppAuthManager authManager = new AppAuthManager();
        String tokenString = authManager.extractAuthorizationHeaderToken(headers);

        if (tokenString == null) {
            throw new NotAuthorizedException("Bearer");
        }

        AccessToken token;

        try {
            JWSInput input = new JWSInput(tokenString);
            token = input.readJsonContent(AccessToken.class);
        } catch (JWSInputException e) {
            throw new NotAuthorizedException("Bearer token format error");
        }

        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
        RealmManager realmManager = new RealmManager(keycloakSession);
        RealmModel realm = realmManager.getRealmByName(realmName);

        if (realm == null) {
            throw new NotAuthorizedException("Unknown realm in token");
        }
        keycloakSession.getContext().setRealm(realm);
        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(keycloakSession, realm, keycloakSession.getContext().getUri(), clientConnection, headers);
        if (authResult == null) {
            throw new NotAuthorizedException("Bearer");
        }

        ClientModel client
                = realm.getName().equals(Config.getAdminRealm())
                ? this.realm.getMasterAdminClient()
                : this.realm.getClientByClientId(realmManager.getRealmAdminClientId(this.realm));

        if (client == null) {
            throw new NotFoundException("Could not find client for authorization");
        }
//
//        UserModel user = authResult.getUser();
//
//        Class clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//
//        try {
//            Constructor<? extends Type> constructor = clazz.getConstructor(RealmModel.class, AccessToken.class, UserModel.class, ClientModel.class);
//            auth = (T) constructor.newInstance(new Object[]{realm, token, user, client});
//        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            LOG.error("Failed to instantiate AdminAuth instance", ex);
//        }
        return new AdminAuth(realm, authResult.getToken(), authResult.getUser(), client);
    }
//
//    private void setupEvents() {
//        adminEvent = new AdminEventBuilder(session.getContext().getRealm(), auth, session, session.getContext().getConnection())
//                .realm(session.getContext().getRealm());
//    }

}