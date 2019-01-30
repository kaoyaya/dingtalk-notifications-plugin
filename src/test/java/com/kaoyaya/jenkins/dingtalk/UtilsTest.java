package com.kaoyaya.jenkins.dingtalk;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void getEmailPrefix() {
        Assert.assertEquals("123", Utils.getEmailPrefix("123@qq.com"));
        assertNull(Utils.getEmailPrefix("123"));
        Assert.assertNotEquals("123", Utils.getEmailPrefix("1233@qq.com"));
        Assert.assertEquals("adevjoe", Utils.getEmailPrefix("adevjoe@gmail.com"));
    }
}