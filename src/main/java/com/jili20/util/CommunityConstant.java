package com.jili20.util;

/**
 * @author bing  @create 2020/11/4-4:32 下午
 */
public interface CommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 重复激活
    int ACTIVATION_REPEAT = 1;

    // 激活失败
    int ACCTIVATION_FAILURE = 2;

    // 默认状态的登录凭证的超时时间
//    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;// 12个小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 24 * 100;
//    int DEFAULT_EXPIRED_SECONDS = 3600;

    // 记住我状态的登录凭证的超时时间
//    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100; // 100 天
    int REMEMBER_EXPIRED_SECONDS = 3600; // 100 天

}
