package com.jili20.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * @author bing  @create 2020/11/4-12:54 下午
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        // 把加密的结果加密成16进制的字符串返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 加入 fastjson 依赖后使用
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key,map.get(key));
            }

        }
        return json.toJSONString();
    }
    // 给不同参数的构造器
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
    // 测试
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age","25");
        System.out.println(getJSONString(0, "ok", map));
        // {"msg":"ok","code":0,"name":"张三","age":"25"}
    }
}
