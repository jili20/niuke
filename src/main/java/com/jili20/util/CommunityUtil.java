package com.jili20.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author bing  @create 2020/11/4-12:54 下午
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5 加密
      public static String md5(String key){
          if (StringUtils.isBlank(key)) {
              return null;
          }
          return DigestUtils.md5DigestAsHex(key.getBytes());
      }
}
