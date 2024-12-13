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

import java.util.ArrayList;
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

    private void addGroup(GroupMapping.TypeEnum type, String adminGroupName, User user) {
        GroupMapping groupMapping = new GroupMapping();
        groupMapping.type(type);
        groupMapping.name(adminGroupName);
        user.addGroupsItem(groupMapping);
    }

    private void removeGroup(User user, String adminGroupName) {
        user.setGroups(new ArrayList<>(user.getGroups().stream().filter(groupMapping -> !groupMapping.getName().equalsIgnoreCase(adminGroupName)).toList()));
    }

    private void checkDataDomainRoles(UserContextRoleUpdate userContextRoleUpdate, User user) {
        userContextRoleUpdate.getContextRoles().stream()
                .filter(contextRole -> contextRole.getParentContextKey() != null).forEach(userContextRole -> {
                    String groupName = SlugifyUtil.slugify(userContextRole.getContextKey(), "");
                    switch (userContextRole.getRoleName()) {
                        case NONE -> removeGroup(user, groupName);
                        case DATA_DOMAIN_ADMIN, DATA_DOMAIN_EDITOR, DATA_DOMAIN_VIEWER -> { //TODO ACL based on permission level
                            removeGroup(user, ADMIN_GROUP_NAME);
                            addGroup(GroupMapping.TypeEnum.NUMBER_2, groupName, user);
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
                removeGroup(user, ADMIN_GROUP_NAME);
                addGroup(GroupMapping.TypeEnum.NUMBER_1, ADMIN_GROUP_NAME, user);
            } else {
                removeGroup(user, ADMIN_GROUP_NAME);
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
