package ch.bedag.dap.hellodata.sidecars.sftpgo.listener;

import ch.bedag.dap.hellodata.commons.SlugifyUtil;
import ch.bedag.dap.hellodata.commons.nats.annotation.JetStreamSubscribe;
import ch.bedag.dap.hellodata.commons.sidecars.context.role.HdRoleName;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.user.data.UserContextRoleUpdate;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.GroupMapping;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.User;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.SftpGoService;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.resource.SftpGoUserResourceProviderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;
import java.util.UUID;

import static ch.bedag.dap.hellodata.commons.sidecars.events.HDEvent.UPDATE_USER_CONTEXT_ROLE;
import static ch.bedag.dap.hellodata.sidecars.sftpgo.service.SftpGoService.ADMIN_GROUP_NAME;

@Log4j2
@Service
@AllArgsConstructor
public class SftpGoUpdateUserContextRoleConsumer {

    private final SftpGoService sftpGoService;
    private final SftpGoUserResourceProviderService sftpGoUserResourceProviderService;

    @SuppressWarnings("unused")
    @JetStreamSubscribe(event = UPDATE_USER_CONTEXT_ROLE)
    public void processContextRoleUpdate(UserContextRoleUpdate userContextRoleUpdate) {
        log.info("-=-=-=-= RECEIVED USER CONTEXT ROLES UPDATE: payload: {}", userContextRoleUpdate);
        User user = fetchUser(userContextRoleUpdate);
        checkBusinessContextRole(userContextRoleUpdate, user);
        checkDataDomainRoles(userContextRoleUpdate, user);
        sftpGoService.updateUser(user);
        log.info("Updated user {}", user);
        if (userContextRoleUpdate.isSendBackUsersList()) {
            sftpGoUserResourceProviderService.publishUsers();
        }
    }

    private void checkDataDomainRoles(UserContextRoleUpdate userContextRoleUpdate, User user) {
        userContextRoleUpdate.getContextRoles().stream()
                .filter(contextRole -> contextRole.getParentContextKey() != null).forEach(userContextRole -> {
                    String groupName = SlugifyUtil.slugify(userContextRole.getContextKey(), "");
                    switch (userContextRole.getRoleName()) {
                        case NONE ->
                                user.setGroups(user.getGroups().stream().filter(groupMapping -> groupMapping.getName().equalsIgnoreCase(groupName)).toList());
                        case DATA_DOMAIN_ADMIN, DATA_DOMAIN_EDITOR, DATA_DOMAIN_VIEWER -> { //TODO ACL based on permission level
                            GroupMapping groupMapping = new GroupMapping();
                            groupMapping.type(GroupMapping.TypeEnum.NUMBER_2);
                            groupMapping.name(groupName);
                            user.addGroupsItem(groupMapping);
                        }
                    }
                });
    }

    private void checkBusinessContextRole(UserContextRoleUpdate userContextRoleUpdate, User user) {
        Optional<UserContextRoleUpdate.ContextRole> businessDomainRole = userContextRoleUpdate.getContextRoles().stream()
                .filter(contextRole -> contextRole.getParentContextKey() == null).findFirst();
        businessDomainRole.ifPresent(businessDomainRoleContext -> {
            HdRoleName roleName = businessDomainRoleContext.getRoleName();
            if (roleName != HdRoleName.NONE) {
                GroupMapping groupMapping = new GroupMapping();
                groupMapping.type(GroupMapping.TypeEnum.NUMBER_1);
                groupMapping.name(ADMIN_GROUP_NAME);
                user.addGroupsItem(groupMapping);
            } else {
                user.setGroups(user.getGroups().stream().filter(groupMapping -> groupMapping.getName().equalsIgnoreCase(ADMIN_GROUP_NAME)).toList());
            }
        });
    }

    private User fetchUser(UserContextRoleUpdate userContextRoleUpdate) {
        User user = null;
        try {
            user = sftpGoService.getUser(userContextRoleUpdate.getUsername());
            log.info("User {} already created", user);
        } catch (WebClientResponseException.NotFound notFound) {
            log.debug("", notFound);
            user = sftpGoService.createUser(userContextRoleUpdate.getEmail(), userContextRoleUpdate.getUsername(), UUID.randomUUID().toString());
        } catch (Exception e) {
            log.error("Could not create user {}", userContextRoleUpdate.getEmail(), e);
        }
        return user;
    }

}
