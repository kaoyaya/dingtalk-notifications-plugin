package com.kaoyaya.jenkins.dingtalk;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class DingtalkPush implements DingdingService {

    private String jenkinsURL;

    private String token;

    private TaskListener listener;

    private AbstractBuild build;

    public DingtalkPush(String jenkinsURL, String token, TaskListener listener, AbstractBuild build) {
        this.jenkinsURL = jenkinsURL;
        this.token = token;
        this.listener = listener;
        this.build = build;
    }

    @Override
    public void start() {
        String text = String.format("\uD83D\uDD04 【%s%s】 开始构建\n%s",
                build.getProject().getDisplayName(),
                build.getDisplayName(),
                getBuildUrl()
        );
        sendTextMessage(Utils.getUserDescription(this.build), text);
    }

    @Override
    public void success() {
        String text = String.format("[对勾] 【%s%s】 构建成功\nsummary: %s,duration: %s\n%s",
                build.getProject().getDisplayName(),
                build.getDisplayName(),
                build.getBuildStatusSummary().message,
                build.getDurationString(),
                getBuildUrl()
        );
        sendTextMessage(Utils.getUserDescription(this.build), text);
    }

    @Override
    public void failed() {
        String text = String.format("❌ 【%s%s】 构建失败\nsummary: %s,duration: %s\n%s",
                build.getProject().getDisplayName(),
                build.getDisplayName(),
                build.getBuildStatusSummary().message,
                build.getDurationString(),
                getBuildUrl()
        );
        sendTextMessage(Utils.getUserDescription(this.build), text);
    }

    @Override
    public void abort() {
        String text = String.format("⛔ 【%s%s】 构建终止\nsummary: %s,duration: %s\n%s",
                build.getProject().getDisplayName(),
                build.getDisplayName(),
                build.getBuildStatusSummary().message,
                build.getDurationString(),
                getBuildUrl()
        );
        sendTextMessage(Utils.getUserDescription(this.build), text);
    }

    private String getBuildUrl() {
        if (jenkinsURL.endsWith("/")) {
            return jenkinsURL + build.getUrl();
        } else {
            return jenkinsURL + "/" + build.getUrl();
        }
    }

    public void sendTextMessage(List<String> atMobiles, String text) {
        HttpClient client = new HttpClient();
        String url = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", this.token);
        PostMethod post = new PostMethod(url);

        JSONObject body = new JSONObject();
        body.put("msgtype", "text");
        JSONObject textJson = new JSONObject();
        textJson.put("content", text);
        body.put("text", textJson);
        JSONObject atJson = new JSONObject();
        atJson.put("atMobiles", atMobiles);
        body.put("at", atJson);
        try {
            post.setRequestEntity(new StringRequestEntity(body.toString(), "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            client.executeMethod(post);
            System.out.println(post.getResponseBodyAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        post.releaseConnection();
    }
}
