package ch.bedag.dap.hellodata.sidecars.sftpgo.service.user;

import ch.bedag.dap.hellodata.commons.nats.annotation.JetStreamSubscribe;
import ch.bedag.dap.hellodata.commons.sidecars.resources.v1.user.data.SubsystemUserUpdate;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.User;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.SftpGoService;
import ch.bedag.dap.hellodata.sidecars.sftpgo.service.resource.SftpGoUserResourceProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static ch.bedag.dap.hellodata.commons.sidecars.events.HDEvent.CREATE_USER;

@Log4j2
@Service
@SuppressWarnings("java:S3516")
@RequiredArgsConstructor
public class SftpGoCreateUserConsumer {

    private final SftpGoService sftpgoService;
    private final SftpGoUserResourceProviderService sftpGoUserResourceProviderService;


    @SuppressWarnings("unused")
    @JetStreamSubscribe(event = CREATE_USER, asyncRun = false)
    public void createUser(SubsystemUserUpdate subsystemUserUpdate) {
        try {
            log.info("------- Received SFTPGo user creation request {}", subsystemUserUpdate);
            User user = sftpgoService.getUser(subsystemUserUpdate.getUsername());
            if (user == null) {
                sftpgoService.createUser(subsystemUserUpdate.getPassword(), subsystemUserUpdate.getUsername(), subsystemUserUpdate.getPassword());
                if (subsystemUserUpdate.isSendBackUsersList()) {
                    sftpGoUserResourceProviderService.publishUsers();
                }
            }
        } catch (Exception e) {
            log.error("Could not create user {}", subsystemUserUpdate.getEmail(), e);
        }
    }
}
