package com.kaoyaya.jenkins.dingtalk;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import org.jenkinsci.plugins.gwt.GenericCause;

import java.util.ArrayList;
import java.util.Map;

public class Utils {

    public static ArrayList<String> getUserDescription(AbstractBuild r) {
        ArrayList<String> desc = new ArrayList<>();

        // 从 changelog 获取用户
        ChangeLogSet<?> changes = r.getChangeSet();
        for (ChangeLogSet.Entry e : changes) {
            if (!desc.contains(e.getAuthor().getDescription())) {
                desc.add(e.getAuthor().getDescription());
            }
        }

        // 从手动触发构建获取用户
        Cause.UserIdCause userIdCause = (Cause.UserIdCause) r.getCause(Cause.UserIdCause.class);
        User user;
        if (userIdCause != null) {
            if (userIdCause.getUserId() != null) {
                user = User.getById(userIdCause.getUserId(), false);
                if (user != null && !desc.contains(user.getDescription())) {
                    desc.add(user.getDescription());
                }
            }
        }

        // 从 git 仓库 webhook 获取用户
        GenericCause genericCause = (GenericCause) r.getCause(GenericCause.class);
        Map<String, String> var = null;
        if (genericCause != null) {
            var = genericCause.getResolvedVariables();
        }
        String userEmail = null;
        if (var != null) {
            for (Map.Entry<String, String> entry : var.entrySet()) {
                if (entry.getKey().equals("email")) {
                    userEmail = entry.getValue();
                }
            }
        }
        if (userEmail != null) {
            user = User.getById(getEmailPrefix(userEmail), false);
            if (user != null && !desc.contains(user.getDescription())) {
                desc.add(user.getDescription());
            }
        }
        return desc;
    }

    public static String getEmailPrefix(String email) {
        String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        if (!email.matches(check)) {
            return null;
        }
        return email.substring(0, email.indexOf("@"));
    }
}
