package com.jili20.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 *
 * @author bing  @create 2020/11/9-9:30 上午
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符，把敏感词替换成 ***
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 过滤敏感词时机：这是一个初始化方法，当服务器初始化容器实例化 SensitiveFilter 这个bean和构造器实例化以后，init 方法被调用
    @PostConstruct
    public void init() {
        // 读取敏感词 sensitive-words.txt
        // this.getClass() 任意对象；getClassLoader() 获取类加载器 target -> classes 要重新编译，保证sensitive-words.txt 在此目录下；
        // 得到一个字节流,以下写法不用关闭，用完 自动关闭
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                // 将 is 字节流 转 BufferedReader缓冲流,InputStreamReader 字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord; //每次读到的敏感词存到这个变量里
            while ((keyWord = reader.readLine()) != null) { // 每次读一行，非空为读到了
                // 调用方法，把读到的敏感词添加到前缀树
                this.addKeyWord(keyWord);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }


    // 将一个敏感词添加到前缀树
    private void addKeyWord(String keyWord) {
        // 临时节点，默认指向根节点
        TrieNode tempNode = rootNode;
        // 遍历 keyWord，看里面有什么
        for (int i = 0; i < keyWord.length(); i++) {
            // 每次遍历得到一个字符
            char c = keyWord.charAt(i);
            // 试图去找它有没有子节点，有就不用新建，没有就要新建
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);//把新建的子节点挂到临时节点下
            }
            // 指向子节点，进入下一轮循环
            tempNode = subNode;
            // 设置结束标识
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }


    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针 1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果 用变长字符串
        StringBuilder sb = new StringBuilder();
        // 遍历，当指针3小于待过滤的文本时，
        while (position < text.length()) {
            // 得到一个字符
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点，将此符号计入结果，让指针3向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论箱号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin 开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 把树形指针归位。重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd) {
                // 发现敏感词，将begin~position字符串替换成 ***
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 把树形指针归位。重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        }
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 非特殊符号!CharUtils.isAsciiAlphanumeric； 0x2E80 ~ 0x9FFF 东亚文字范围，之外
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树，定义前缀树的某一个节点
    private class TrieNode {

        // 关键词结束标识（是不是敏感词，是不是一个单词的结尾）
        private boolean isKeyWordEnd = false;

        // 子节点（通过当前节点找到此子节点）(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }

}
