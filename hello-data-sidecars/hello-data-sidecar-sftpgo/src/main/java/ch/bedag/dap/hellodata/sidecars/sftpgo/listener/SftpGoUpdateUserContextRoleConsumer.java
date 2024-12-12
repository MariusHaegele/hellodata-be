package ch.bedag.dap.hellodata.sidecars.sftpgo.listener;

import ch.bedag.dap.hellodata.commons.nats.annotation.JetStreamSubscribe;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.user.data.UserContextRoleUpdate;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.User;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.SftpGoService;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.resource.SftpGoUserResourceProviderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

import static ch.bedag.dap.hellodata.commons.sidecars.events.HDEvent.UPDATE_USER_CONTEXT_ROLE;

@Log4j2
@Service
@AllArgsConstructor
public class SftpGoUpdateUserContextRoleConsumer {

    private final SftpGoService sftpgoService;
    private final SftpGoUserResourceProviderService sftpGoUserResourceProviderService;

    @SuppressWarnings("unused")
    @JetStreamSubscribe(event = UPDATE_USER_CONTEXT_ROLE)
    public void processContextRoleUpdate(UserContextRoleUpdate userContextRoleUpdate) {
        log.info("-=-=-=-= RECEIVED USER CONTEXT ROLES UPDATE: payload: {}", userContextRoleUpdate);
        User user = fetchUser(userContextRoleUpdate);


//        Optional<UserContextRoleUpdate.ContextRole> businessDomainRole = userContextRoleUpdate.getContextRoles().stream()
//                .filter(contextRole -> contextRole.getParentContextKey() == null).findFirst();
//        businessDomainRole.ifPresent(businessDomainRoleContext -> {
//            HdRoleName roleName = businessDomainRoleContext.getRoleName();
//            if (roleName != HdRoleName.NONE) {
//
//            }
//        })

    }

    private User fetchUser(UserContextRoleUpdate userContextRoleUpdate) {
        User user = null;
        try {
            user = sftpgoService.getUser(userContextRoleUpdate.getUsername());
            log.info("User {} already created", user);
        } catch (WebClientResponseException.NotFound notFound) {
            log.debug("", notFound);
            user = sftpgoService.createUser(userContextRoleUpdate.getEmail(), userContextRoleUpdate.getUsername(), UUID.randomUUID().toString());
        } catch (Exception e) {
            log.error("Could not create user {}", userContextRoleUpdate.getEmail(), e);
        }
        return user;
    }

}
