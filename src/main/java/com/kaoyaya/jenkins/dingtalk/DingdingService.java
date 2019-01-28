package com.kaoyaya.jenkins.dingtalk;

/**
 * Created by Marvin on 16/10/8.
 */
public interface DingdingService {


    void start();

    void success();

    void failed();
    
    void abort();
}
