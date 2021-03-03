package gaurav.keycloak;

import com.fasterxml.jackson.databind.JsonSerializer;
import gaurav.keycloak.model.UserDetails;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.utils.MediaType;

import javax.validation.constraints.Null;
import javax.ws.rs.*;
import java.security.acl.Group;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DemoRestProvider implements RealmResourceProvider {
    private final KeycloakSession session;


    public DemoRestProvider(KeycloakSession session) {
        this.session = session;
    }


    public void close() {

    }

    public Object getResource() {
        return this;
    }


//    http://localhost:8080/auth/realms/test/gaurav-rest/user/ben/group/marina

    @GET
    @Path("user/{username}/group/{group_name}")
    @NoCache
    @Produces({MediaType.APPLICATION_JSON})
    @Encoded
    public List<UserDetails> getUsers(@PathParam("username") String username, @PathParam("group_name") String group_name) {
//        UserModel userM = session.users().getUserByEmail("asd@asd", session.getContext().getRealm());
        UserModel userM = session.users().getUserByUsername(username, session.getContext().getRealm());
//        GroupModel groupM = session.realms().searchForGroupByName(session.getContext().getRealm(), "gang", 1, 1)
        List<GroupModel> gm = session.realms().getGroups(session.getContext().getRealm());
        System.out.println("------------------------");
        for ( GroupModel g: gm
             ) {
            if (group_name.equals(g.getName())){
                System.out.println("(name) Found group: " + g.getName());
                System.out.println("(id) Found group: " + g.getId());
                userM.joinGroup(g);

                break;
            }

            System.out.println(g.getName());
        }
        System.out.println("------------------------");

//        List a = session.realms().searchForGroupByName(session.getContext().getRealm(), "gang", 0, 1);
//        System.out.println("size: " + a.size());
//        for ( Object b : a) {
//            GroupModel c = (GroupModel) b;
//            System.out.println(c.getName());
//        }
//        System.out.println(session.realms().searchForGroupByName(session.getContext().getRealm(), "gang", 1, 1));
//        GroupModel groupM = session.realms().getGroupById("ebce78e2-cf50-44ad-878f-5700522a86ed", session.getContext().getRealm());
//        userM.joinGroup(groupM);
        List<UserModel> userModel = session.users().getUsers(session.getContext().getRealm());
        return userModel.stream().map(this::toUserDetail).collect(Collectors.toList());
    }

    private UserDetails toUserDetail(UserModel um) {
        return new UserDetails(um.getUsername(), um.getFirstName(), um.getLastName());

    }

}