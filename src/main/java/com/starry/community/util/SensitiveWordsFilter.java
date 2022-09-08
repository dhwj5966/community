package com.starry.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-07-10:52 AM
 * @Describe 敏感词过滤器，利用Trie实现
 */
@Component
public class SensitiveWordsFilter {
    private Trie trie;

    public SensitiveWordsFilter() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            trie = new Trie();
            ClassPathResource classPathResource = new ClassPathResource("sensitive-words.txt");
            File file = classPathResource.getFile();
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                trie.insertSensitiveWord(temp);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据敏感词库里的词，将文本中的敏感词变为*
     * 时间复杂度 O(n),n为text长度
     * @param text 需要进行过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        return trie.filter(text);
    }

    /**
     * 字典树，为过滤器提供敏感词匹配功能
     */
    private class Trie {
        private TreeNode root;

        public Trie() {
            root = new TreeNode();
        }

        private String filter(String text) {
            if (StringUtils.isBlank(text)) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            char[] chars = text.toCharArray();
            TreeNode temp = root;
            int begin = 0 , position = 0;
            while (position < chars.length) {
                char c = chars[position];
                //先判断特殊字符,如果当前位是一个特殊字符
                if (isSymbol(c)) {
                    if (temp == root) {
                        result.append(c);
                        begin++;
                    }
                    //无论temp是不是根节点，position都无视该字符
                    position++;
                    continue;
                }
                //如果不是特殊字符，需要进行判断
                TreeNode next = temp.getChildren().get(c);
                //如果不存在下一个node了
                if (next == null) {
                    result.append(chars[begin]);
                    position = ++begin;
                    temp = root;
                } else if (next.isEnd()) {//存在下一个node，且下一个node为end
                    result.append("***");
                    begin = ++position;
                    temp = root;
                } else {//存在下一个node，且下一个node不是end
                    position++;
                    temp = next;
                }
            }
            result.append(text.substring(begin));
            return result.toString();
        }

        /**
         * 判断字符c是否是符号
         * @param c
         * @return true:符号   false:不是符号
         */
        private boolean isSymbol(Character c) {
            //该字符不是a-z,也不是A-Z,也不是0-9,也不是东亚字符
            return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
        }



        /**
         * 插入敏感词
         * @param word
         */
        public void insertSensitiveWord(String word) {
            char[] chars = word.toCharArray();
            TreeNode temp = root;
            for (int i = 0; i < chars.length; i++) {
                TreeNode nextNode = temp.getChildren().get(chars[i]);
                if (nextNode == null) {
                    nextNode = new TreeNode(chars[i]);
                    temp.getChildren().put(chars[i], nextNode);
                }
                temp = nextNode;
            }
            temp.setEnd(true);
        }

        /**
         * 判断word从[begin,end]是否是敏感词
         * 如果begin > end 则不是敏感词
         * @param word
         * @return true:如果是敏感词  false:如果不是敏感词
         */
        public boolean isSensitiveWord(String word, int begin, int end) {
            char[] chars = word.toCharArray();
            if (begin > end) {
                return false;
            }
            TreeNode temp = root;
            while (begin < end) {
                TreeNode nextNode = temp.getChildren().get(chars[begin]);
                if (nextNode == null) {
                    return false;
                }
                temp = nextNode;
            }
            return temp.isEnd();
        }
    }

    /**
     * 字典树Trie的节点类
     */
    private class TreeNode {
        private char key;
        private boolean isEnd;
        private Map<Character, TreeNode> children;

        public TreeNode() {
            children = new HashMap<>();
        }

        public TreeNode(char key) {
            this();
            this.key = key;
        }

        public void setKey(char key) {
            this.key = key;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public char getKey() {
            return key;
        }

        public boolean isEnd() {
            return isEnd;
        }

        public Map<Character, TreeNode> getChildren() {
            return children;
        }
    }
}
