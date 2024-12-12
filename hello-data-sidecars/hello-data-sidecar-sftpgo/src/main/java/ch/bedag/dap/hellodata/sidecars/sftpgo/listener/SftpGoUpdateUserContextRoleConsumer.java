package ch.bedag.dap.hellodata.sidecars.sftpgo.listener;

import ch.bedag.dap.hellodata.commons.nats.annotation.JetStreamSubscribe;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.user.data.UserContextRoleUpdate;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.SftpGoService;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.resource.SftpGoUserResourceProviderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<UserContextRoleUpdate.ContextRole> contextRoles = userContextRoleUpdate.getContextRoles().stream()
                .filter(contextRole -> contextRole.getParentContextKey() != null).toList();
    }

}
