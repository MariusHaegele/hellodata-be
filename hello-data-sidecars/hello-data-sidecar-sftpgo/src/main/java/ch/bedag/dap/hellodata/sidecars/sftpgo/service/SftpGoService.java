package ch.bedag.dap.hellodata.sidecars.sftpgo.service;

import ch.bedag.dap.hellodata.commons.SlugifyUtil;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.FoldersApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.GroupsApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.TokenApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.UsersApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.ApiClient;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.auth.HttpBasicAuth;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.auth.HttpBearerAuth;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.*;
import ch.bedag.dap.hellodata.sidecars.sftpgo.config.S3ConnectionsConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.web.reactive.function.client.WebClientResponseException.Conflict;
import static org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;

@Log4j2
@Service
@RequiredArgsConstructor
public class SftpGoService {
    private static final String ADMIN_GROUP_NAME = "Admin";
    private final ApiClient apiClient;
    private final S3ConnectionsConfig s3ConnectionsConfig;

    @Value("${hello-data.sftpgo.admin-username}")
    private String sftpGoAdminUsername;
    @Value("${hello-data.sftpgo.admin-password}")
    private String sftpGoAdminPassword;
    @Value("${hello-data.admin-virtual-folder}")
    private String adminVirtualFolder;

    @PostConstruct
    public void initAdminGroup() {
        refreshToken();
        GroupsApi groupsApi = new GroupsApi(apiClient);
        try {
            Group existingGroup = groupsApi.getGroupByName(ADMIN_GROUP_NAME, 0).block();
            log.info("Admin group '{}' already exists", existingGroup);
        } catch (NotFound notFound) {
            log.debug("", notFound);
            log.info("Admin group not found, creating...");
            createAdminGroup(groupsApi);
        }
    }

    public List<User> getAllUsers() {
        refreshToken();
        UsersApi usersApi = new UsersApi(apiClient);
        return usersApi.getUsers(0, Integer.MAX_VALUE, "ASC").collectList().block();
    }

    public User getUser(String username) {
        refreshToken();
        UsersApi usersApi = new UsersApi(apiClient);
        return usersApi.getUserByUsername(username, 0).block();
    }

    public void disableUser(String username) {
        User user = getUser(username);
        user.setStatus(User.StatusEnum.NUMBER_0);
        UsersApi usersApi = new UsersApi(apiClient);
        usersApi.updateUser(username, user, 1).block();
        log.info("User {} disabled", username);
    }

    public void enableUser(String username) {
        User user = getUser(username);
        user.setStatus(User.StatusEnum.NUMBER_1);
        UsersApi usersApi = new UsersApi(apiClient);
        usersApi.updateUser(username, user, 1).block();
        log.info("User {} enabled", username);
    }

    public void createUser(String email, String username, String password) {
        refreshToken();
        UsersApi usersApi = new UsersApi(apiClient);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setStatus(User.StatusEnum.NUMBER_1);
        usersApi.addUser(user, 0).block();
        usersApi.disableUser2fa(username).block();
        log.info("User {} created", username);
    }

    public void createGroup(String dataDomainKey, String dataDomainName) {
        refreshToken();
        GroupsApi groupsApi = new GroupsApi(apiClient);
        String groupName = SlugifyUtil.slugify(dataDomainKey, "");
        try {
            Group existingGroup = groupsApi.getGroupByName(groupName, 0).block();
            log.info("Group {} already exists", existingGroup);
        } catch (NotFound notFound) {
            log.debug("", notFound);
            log.info("Group {} not found, creating...", groupName);
            Group group = new Group();
            group.setName(groupName);
            group.setDescription(dataDomainName);
            VirtualFolder vf = createVirtualFolder(dataDomainKey, dataDomainName, groupName);
            group.setVirtualFolders(List.of(vf));
            groupsApi.addGroup(group, 0).block();
            log.info("Group {} created", groupName);
        }
    }

    private void createAdminGroup(GroupsApi groupsApi) {
        BaseVirtualFolder baseVirtualFolder = new BaseVirtualFolder();
        baseVirtualFolder.setName(ADMIN_GROUP_NAME);
        baseVirtualFolder.setMappedPath(adminVirtualFolder);
        FoldersApi foldersApi = new FoldersApi(apiClient);
        BaseVirtualFolder createdFolder = foldersApi.addFolder(baseVirtualFolder, 0).block();

        Group group = new Group();
        group.setName(ADMIN_GROUP_NAME);
        group.setDescription("Admin group");
        VirtualFolder virtualFolder = new VirtualFolder();
        virtualFolder.setName(createdFolder.getName());
        virtualFolder.setMappedPath(createdFolder.getMappedPath());
        virtualFolder.setVirtualPath(adminVirtualFolder);
        virtualFolder.setId(createdFolder.getId());
        group.setVirtualFolders(List.of(virtualFolder));
        groupsApi.addGroup(group, 0).block();
        log.info("Admin group created");
    }

    private VirtualFolder createVirtualFolder(String dataDomainKey, String dataDomainName, String groupName) {
        S3ConnectionsConfig.S3Connection s3Connection = s3ConnectionsConfig.getS3Connection(dataDomainKey);
        S3Config s3Config = new S3Config();
        s3Config.setAccessKey(s3Connection.getAccessKey());
        Secret accessSecret = new Secret();
        accessSecret.setKey(s3Connection.getAccessKey());
        accessSecret.setStatus(Secret.StatusEnum.PLAIN);
        accessSecret.setPayload(s3Connection.getAccessSecret());
        s3Config.setAccessSecret(accessSecret);
        s3Config.setEndpoint(s3Connection.getEndpoint());
        s3Config.forcePathStyle(s3Connection.isForcePathStyle());
        s3Config.setBucket(s3Connection.getBucket());
        FilesystemConfig filesystemConfig = new FilesystemConfig();
        filesystemConfig.s3config(s3Config);
        filesystemConfig.setProvider(FsProviders.NUMBER_1);

        BaseVirtualFolder baseVirtualFolder = new BaseVirtualFolder();
        baseVirtualFolder.setName(groupName);
        baseVirtualFolder.setDescription(dataDomainName);
        baseVirtualFolder.setMappedPath("/" + groupName);
        baseVirtualFolder.setFilesystem(filesystemConfig);
        FoldersApi foldersApi = new FoldersApi(apiClient);
        log.info("Creating folder {}", baseVirtualFolder);

        BaseVirtualFolder createdFolder;
        try {
            createdFolder = foldersApi.addFolder(baseVirtualFolder, 0).block();
        } catch (Conflict conflict) {
            log.info("Folder {} already exists, fetching", baseVirtualFolder);
            createdFolder = foldersApi.getFolderByName(groupName, 0).block();
        }

        VirtualFolder vf = new VirtualFolder();
        vf.setName(createdFolder.getName());
        vf.setDescription(createdFolder.getDescription());
        vf.setMappedPath(createdFolder.getMappedPath());
        vf.setVirtualPath("/" + groupName);
        vf.setId(createdFolder.getId());
        return vf;
    }

    private void refreshToken() {
        HttpBasicAuth basicAuth = (HttpBasicAuth) apiClient.getAuthentication("BasicAuth");
        basicAuth.setUsername(sftpGoAdminUsername);
        basicAuth.setPassword(sftpGoAdminPassword);

        TokenApi tokenApi = new TokenApi(apiClient);
        Token token = tokenApi.getToken(null).block();
        HttpBearerAuth BearerAuth = (HttpBearerAuth) apiClient.getAuthentication("BearerAuth");
        BearerAuth.setBearerToken(token.getAccessToken());
    }
}
