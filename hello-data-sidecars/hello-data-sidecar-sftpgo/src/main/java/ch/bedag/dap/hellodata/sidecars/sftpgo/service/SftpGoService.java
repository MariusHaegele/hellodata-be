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
