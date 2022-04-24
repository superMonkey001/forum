package cn.edu.hncj.forum.provider;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.provider.dto.GithubUser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public static final MediaType MEDIA_TYPE
            = MediaType.get("application/json; charset=utf-8");


    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Value("${github.client.secret}")
    private String clientSecret;


    /**
     * 登录功能的第二步
     * okHttp插件帮助后台模拟post请求，访问https://github.com/login/oauth/access_token
     * @param accessTokenDTO
     * @return 返回access_token
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_secret(clientSecret);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_TYPE, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //responseStr:access_token=gho_16C7e42F292c6912E7710c838347Ae178B4a&scope=repo%2Cgist&token_type=bearer
            String responseStr = response.body().string();
            String accessToken = responseStr.split("&")[0].split("=")[1];
            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录功能的第三步
     * okHttp插件帮助后台模拟get请求,访问https://api.github.com/user（带头信息Authorization）
     * @param accessToken
     * @return 返回Github用户的信息
     */
    public GithubUser getGithubUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        // github获取User信息的新方式
        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization","token "+accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //返回用户信息的json串
            String jsonUser = response.body().string();
            GithubUser githubUser = JSON.parseObject(jsonUser, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通过参数accessToken找不到用户信息；
        return null;
    }
}
