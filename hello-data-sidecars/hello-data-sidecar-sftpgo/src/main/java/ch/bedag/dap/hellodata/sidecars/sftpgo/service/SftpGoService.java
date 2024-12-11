package ch.bedag.dap.hellodata.sidecars.sftpgo.service;

import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.TokenApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.api.UsersApi;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.ApiClient;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.auth.HttpBasicAuth;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.invoker.auth.HttpBearerAuth;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.Token;
import ch.bedag.dap.hellodata.sidecars.sftpgo.client.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SftpGoService {
    private final ApiClient apiClient;

    @Value("${hello-data.sftpgo.admin-username}")
    private String sftpgoAdminUsername;
    @Value("${hello-data.sftpgo.admin-password}")
    private String sftpgoAdminPassword;

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
    }

    public void enableUser(String username) {
        User user = getUser(username);
        user.setStatus(User.StatusEnum.NUMBER_1);
        UsersApi usersApi = new UsersApi(apiClient);
        usersApi.updateUser(username, user, 1).block();
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
    }

    private void refreshToken() {
        HttpBasicAuth basicAuth = (HttpBasicAuth) apiClient.getAuthentication("BasicAuth");
        basicAuth.setUsername(sftpgoAdminUsername);
        basicAuth.setPassword(sftpgoAdminPassword);

        TokenApi tokenApi = new TokenApi(apiClient);
        Token token = tokenApi.getToken(null).block();
        HttpBearerAuth BearerAuth = (HttpBearerAuth) apiClient.getAuthentication("BearerAuth");
        BearerAuth.setBearerToken(token.getAccessToken());
    }
}
