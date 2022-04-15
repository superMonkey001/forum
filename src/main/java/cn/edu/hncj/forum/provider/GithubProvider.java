package cn.edu.hncj.forum.provider;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.dto.GithubUser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public static final MediaType mediaType
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
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
    public GithubUser getGithubUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
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
        //通过参数accessToken找不到用户信息；
        return null;
    }
}
