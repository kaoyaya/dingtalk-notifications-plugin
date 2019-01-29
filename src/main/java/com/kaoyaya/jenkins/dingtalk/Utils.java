package com.kaoyaya.jenkins.dingtalk;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.User;
import hudson.scm.ChangeLogSet;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<String> getUserDescription(AbstractBuild r) {
        ArrayList<String> desc = new ArrayList<>();
        Cause.UserIdCause userIdCause = (Cause.UserIdCause) r.getCause(Cause.UserIdCause.class);
        ChangeLogSet<?> changes = r.getChangeSet();
        for (ChangeLogSet.Entry e : changes) {
            if (!desc.contains(e.getAuthor().getDescription())) {
                desc.add(e.getAuthor().getDescription());
            }
        }
        User user;
        if (userIdCause != null) {
            if (userIdCause.getUserId() != null) {
                user = User.getById(userIdCause.getUserId(), false);
                if (user != null && !desc.contains(user.getDescription())) {
                    desc.add(user.getDescription());
                }
            }
        }
        return desc;
    }
}
