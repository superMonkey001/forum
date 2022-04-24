package cn.edu.hncj.forum.provider;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.provider.dto.GiteeUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GiteeProvider {
    public static final MediaType MEDIA_TYPE
            = MediaType.get("application/json; charset=utf-8");

    @Value("${gitee.client.id}")
    private String clientId;

    @Value("${gitee.redirect.uri}")
    private String redirectUri;

    @Value("${gitee.client.secret}")
    private String clientSecret;


    /**
     * 登录功能的第二步
     * okHttp插件帮助后台模拟post请求，访问https://github.com/login/oauth/access_token
     *
     * @param accessTokenDTO
     * @return 返回access_token
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_secret(clientSecret);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_TYPE, JSON.toJSONString(accessTokenDTO));

        String url = "https://gitee.com/oauth/token?grant_type=authorization_code&code=%s&client_id=%s&redirect_uri=%s&client_secret=%s";
        url = String.format(url,accessTokenDTO.getCode(), clientId, redirectUri, clientSecret);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //responseStr:access_token=gho_16C7e42F292c6912E7710c838347Ae178B4a&scope=repo%2Cgist&token_type=bearer
            String responseStr = response.body().string();
            JSONObject jsonObject = JSON.parseObject(responseStr);
            return jsonObject.getString("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录功能的第三步
     * okHttp插件帮助后台模拟get请求,访问https://api.github.com/user（带头信息Authorization）
     *
     * @param accessToken
     * @return 返回Github用户的信息
     */
    public GiteeUser getGiteeUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        // github获取User信息的新方式
        Request request = new Request.Builder()
                .url("https://gitee.com/api/v5/user?access_token=" + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //返回用户信息的json串
            String jsonUser = response.body().string();
            GiteeUser giteeUser = JSON.parseObject(jsonUser, GiteeUser.class);
            return giteeUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过参数accessToken找不到用户信息；
        return null;
    }
}
