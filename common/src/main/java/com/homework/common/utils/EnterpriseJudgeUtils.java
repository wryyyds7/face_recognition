package com.homework.common.utils;

import com.homework.common.domain.entity.BoChaResult.WebPageValue;
import com.homework.common.domain.entity.EnterpriseInfo;
import com.homework.common.feign.AIClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 这个的小路太低了，之后可以考虑搞一个类来返回值，不然要搞几次搜索
public class EnterpriseJudgeUtils {


    /**
     * 判断是否为目标企业官网 - 基于WebPageValue对象
     * @param webPageValue API返回的网页信息
     * @param targetEnterpriseName 目标企业名称
     * @return EnterpriseInfo 企业信息，如果不是目标企业则返回null
     */
    public static EnterpriseInfo isRightEnterprise(WebPageValue webPageValue, String targetEnterpriseName) {
        if (webPageValue == null) {
            return null;
        }

        // 获取网页标题和URL
        String title = webPageValue.getName();
        String url = webPageValue.getUrl();
//        String displayUrl = webPageValue.getDisplayUrl();
        String snippet = webPageValue.getSnippet();

        if (title == null || url == null) {
            return null;
        }

        // 统一转换为小写进行比较
        String lowerTitle = title.toLowerCase();
        String lowerTargetName = targetEnterpriseName.toLowerCase();

        // 判断条件：
        // 1. 标题或者snippet中包含企业名称
        // 2. URL是有效的企业官网地址

        boolean isMatch = (lowerTitle.contains(lowerTargetName) || snippet.toLowerCase().contains(lowerTargetName))
                && isValidEnterpriseUrl(url);

        if (isMatch) {
            EnterpriseInfo info = new EnterpriseInfo();
            info.setUrl(url);
            
            // 尝试从网页底部提取更准确的企业名称
            String extractedName = extractCompanyNameFromWebsite(url, targetEnterpriseName);
            if (extractedName != null && !extractedName.isEmpty()) {
                info.setEnterpriseName(extractedName);
            } else {
                info.setEnterpriseName(targetEnterpriseName);
            }
            
            // 注册号需要通过其他方式获取或留空
            return info;
        }

        return null;
    }

    /**
     * 判断URL是否为企业官网格式
     */
    public static boolean isValidEnterpriseUrl(String url) {
        if (url == null)
            return false;

        // 转换为小写进行判断
        String lowerUrl = url.toLowerCase();
        
        // 1. 排除常见的非企业官网域名
        String[] excludeDomains = {
            "downcc.com", "download.csdn.net", "baidu.com", "sina.com", "sohu.com",
            "qq.com", "360.cn", "163.com", "youku.com", "bilibili.com",
            "youtube.com", "google.com", "bing.com", "yahoo.com", "amazon.com",
            "taobao.com", "tmall.com", "jd.com", "pinduoduo.com", "suning.com",
            "51job.com", "zhaopin.com", "liepin.com", "bosszp.com", "lagou.com"
        };
        for (String domain : excludeDomains) {
            if (lowerUrl.contains(domain)) {
                return false;
            }
        }
        
        // 2. 排除包含"translate"、"baidu"等翻译或搜索相关的URL
        if (lowerUrl.contains("translate") || lowerUrl.contains("baidu") || lowerUrl.contains("search")) {
            return false;
        }
        
        // 3. 验证URL格式
        if (!lowerUrl.startsWith("http")) {
            return false;
        }
        
        // 4. 检查是否为常见的企业官网格式（xxx.com或www.xxx.com）
        // 避免深层路径或特殊格式的URL
        if (lowerUrl.contains(".com/") || lowerUrl.contains(".cn/") || lowerUrl.contains(".net/") || 
            lowerUrl.contains(".org/") || lowerUrl.contains(".io/")) {
            // 允许一级路径如 /about, /contact, /index
            String path = lowerUrl.substring(lowerUrl.indexOf("://") + 3);
            if (path.contains("/")) {
                String afterDomain = path.substring(path.indexOf("/") + 1);
                // 只允许简单的路径，不允许包含多个斜杠或特殊字符的复杂路径
                if (afterDomain.contains("/") || afterDomain.contains(".html") || afterDomain.contains(".php") || 
                    afterDomain.contains(".asp") || afterDomain.contains(".jsp")) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 从企业官网提取准确的公司名称
     * @param url 企业官网URL
     * @param targetName 用户输入的目标名称
     * @return 提取到的准确公司名称，如果提取失败则返回null
     */
    private static String extractCompanyNameFromWebsite(String url, String targetName) {
        try {
            // 连接到网站获取页面内容
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 获取页面底部元素
            Elements footerElements = doc.select("footer, .footer, #footer, .copyright, #copyright, .bottom, #bottom");
            Element mainFooter = null;

            // 优先选择明确的底部元素
            if (!footerElements.isEmpty()) {
                mainFooter = footerElements.first();
            } else {
                // 如果没有明确的底部元素，尝试获取页面最后一部分内容
                Elements allElements = doc.select("body > *");
                if (!allElements.isEmpty()) {
                    mainFooter = allElements.last();
                } else {
                    mainFooter = doc.select("body").first();
                }
            }

            if (mainFooter == null) {
                return null;
            }

            // 尝试从底部提取公司名称
            String companyName = extractCompanyNameFromCopyright(mainFooter, targetName);
            if (companyName != null) {
                return companyName;
            }

            // 备用方案：从页面标题提取
            String title = doc.title();
            if (title != null && !title.isEmpty()) {
                // 清理标题中的无关信息
                String cleanedTitle = title.replaceAll("(官网|首页| - |_|\\\")", " ").trim();
                // 如果清理后的标题包含目标名称，返回清理后的标题
                if (cleanedTitle.contains(targetName) || targetName.contains(cleanedTitle)) {
                    return cleanedTitle;
                }
            }

            return null;

        } catch (Exception e) {
            // 发生异常时，返回null
            return null;
        }
    }

    /**
     * 判断是否为企业官网
     * 通过检查页面底部是否有备案号、工商红盾图标或版权声明等特征
     *
     * @param element 包含链接信息的元素
     * @return 是否为企业官网
     * @author wry thanks for AI
     */
    public static EnterpriseInfo isRightEnterpriseOld(Element element, String companyName) {
        if (element == null) {
            return null;
        }

        try {
            // 获取链接的URL
            String url = element.attr("href");
            if (url == null || url.trim().isEmpty()) {
                return null;
            }

            // 如果是相对路径，跳过验证
            if (!url.startsWith("http")) {
                return null;
            }

            // 连接到网站获取页面内容
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Connection", "keep-alive")
                    .timeout(100)
                    .get();

            // 获取页面底部元素
            Elements footerElements = doc.select("footer, .footer, #footer, body > div:last-child");
            Element mainFooter = null;

            // 优先选择footer标签，如果没有则选择其他可能的底部元素
            if (!footerElements.isEmpty()) {
                mainFooter = footerElements.first();
            } else {
                // 如果没有明确的footer元素，尝试获取页面底部的内容
                mainFooter = doc.select("body").first();
            }

            if (mainFooter == null) {
                //TODO: 之后可以考虑更多的方法验证，比如ai识别，介入星火API，让他给我答案（见上）
                return null;
            }

            // 获取公司名称用于版权验证

            // 检查三个特征中的任意一个是否存在
            boolean hasCopyright = hasCopyrightStatement(mainFooter, companyName);
            if (hasCopyright) {
                EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
                enterpriseInfo.setEnterpriseName(extractCompanyNameFromCopyright(mainFooter, companyName));
                enterpriseInfo.setUrl(url);
                enterpriseInfo.setBeianInfo(getBeianInfo(mainFooter));
                return enterpriseInfo;
            }
            return null;

        } catch (Exception e) {
            // 发生异常时，默认认为不是企业官网
            return null;
        }
    }

    /**
     * 获取页面底部备案号信息
     *
     * @param footerElement 页面底部元素
     * @return 是否有备案号
     */
    private static String getBeianInfo(Element footerElement) {
        if (footerElement == null) {
            return null;
        }

        // 查找包含备案号特征的文本
        String footerText = footerElement.text();
        Pattern pattern = java.util.regex.Pattern.compile("([\\w]{1,2}ICP备?\\d+号?)");
        Matcher matcher = pattern.matcher(footerText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }



    /**
     * 从页面底部版权声明中提取公司名称
     *
     * @param footerElement 页面底部元素
     * @param searchCompanyName 搜索时使用的公司名称（用于匹配）
     * @return 提取到的公司名称，如果未找到则返回null
     */
    private static String extractCompanyNameFromCopyright(Element footerElement, String searchCompanyName) {
        if (footerElement == null || searchCompanyName == null || searchCompanyName.trim().isEmpty()) {
            return null;
        }

        String footerText = footerElement.text();

        // 清理搜索公司名称，移除常见后缀以提高匹配率
        String cleanSearchName = searchCompanyName.replaceAll("(有限公司|有限责任公司|股份有限公司|集团|控股|公司)$", "").trim();
        
        // 常见的版权格式模式
        // 模式1: "© 2025 公司名称 版权所有" 或 "Copyright 2025 公司名称 All Rights Reserved"
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("[©Copyright©]*\\s*\\d{4}\\s*([^\\s].*?)\\s*(版权所有|All Rights Reserved|保留所有权利|\\u00A9)");
        java.util.regex.Matcher matcher1 = pattern1.matcher(footerText);
        if (matcher1.find()) {
            String extractedName = matcher1.group(1).trim();
            // 验证提取的名称是否与搜索名称相关
            if (isNameRelated(extractedName, cleanSearchName)) {
                return extractedName;
            }
        }

        // 模式2: "公司名称 © 2025 版权所有" 或 "Company Name Copyright 2025 All Rights Reserved"
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("([^©Copyright©]*?)\\s*[©Copyright©]\\s*\\d{4}.*?(版权所有|All Rights Reserved|保留所有权利)");
        java.util.regex.Matcher matcher2 = pattern2.matcher(footerText);
        if (matcher2.find()) {
            String extractedName = matcher2.group(1).trim();
            if (isNameRelated(extractedName, cleanSearchName)) {
                return extractedName;
            }
        }

        // 模式3: 仅包含公司名称和版权所有，没有年份
        java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("([^©Copyright©]*?)\\s*(版权所有|All Rights Reserved|保留所有权利)");
        java.util.regex.Matcher matcher3 = pattern3.matcher(footerText);
        if (matcher3.find()) {
            String extractedName = matcher3.group(1).trim();
            if (isNameRelated(extractedName, cleanSearchName) && !extractedName.isEmpty()) {
                return extractedName;
            }
        }

        // 模式4: 包含备案号的情况，通常备案号前面是公司名称
        java.util.regex.Pattern pattern4 = java.util.regex.Pattern.compile("(.*?)\\s*[\\u4e00-\\u9fa5]{1,2}ICP备");
        java.util.regex.Matcher matcher4 = pattern4.matcher(footerText);
        if (matcher4.find()) {
            String extractedName = matcher4.group(1).trim();
            if (isNameRelated(extractedName, cleanSearchName) && !extractedName.isEmpty()) {
                return extractedName;
            }
        }

        // 模式5: 直接查找包含关键词的公司名称格式
        // 匹配包含公司名称关键词且以常见公司后缀结尾的文本
        java.util.regex.Pattern pattern5 = java.util.regex.Pattern.compile(".*?" + java.util.regex.Pattern.quote(cleanSearchName) + ".*?((有限责任公司|有限公司|股份有限公司|集团|控股|公司)$)");
        java.util.regex.Matcher matcher5 = pattern5.matcher(footerText);
        if (matcher5.find()) {
            return matcher5.group(0).trim();
        }

        // 最后尝试：如果找不到具体模式，直接返回搜索的公司名称
        // 但只在底部文本确实包含搜索名称时才返回
        if (footerText.contains(searchCompanyName) || footerText.contains(cleanSearchName)) {
            return searchCompanyName;
        }

        return null;
    }

    /**
     * 判断两个公司名称是否相关
     * @param extractedName 提取的公司名称
     * @param searchName 搜索的公司名称（已清理）
     * @return 是否相关
     */
    private static boolean isNameRelated(String extractedName, String searchName) {
        if (extractedName == null || searchName == null) {
            return false;
        }
        
        String lowerExtracted = extractedName.toLowerCase();
        String lowerSearch = searchName.toLowerCase();
        
        // 检查是否包含关系
        if (lowerExtracted.contains(lowerSearch) || lowerSearch.contains(lowerExtracted)) {
            return true;
        }
        
        // 检查是否是同一公司的不同表述
        // 例如：搜索 "阿里巴巴"，提取到 "阿里巴巴集团控股有限公司"
        return false;
    }

    /**
     * 检查页面底部是否有版权声明
     *
     * @param footerElement 页面底部元素
     * @param companyName 公司名称
     * @return 是否有版权声明
     */
    private static boolean hasCopyrightStatement(Element footerElement, String companyName) {
        if (footerElement == null || companyName == null || companyName.trim().isEmpty()) {
            return false;
        }

        String footerText = footerElement.text();

        // 检查是否包含关键词
        boolean hasCopyrightKeyword = footerText.contains("版权所有") ||
                footerText.contains("Copyright") ||
                footerText.contains("©") ||
                footerText.contains("All Rights Reserved") ||
                footerText.contains("保留所有权利");

        // 进行更严格的公司名称匹配
        boolean hasCompanyName = matchCompanyName(footerText, companyName);

        return hasCopyrightKeyword && hasCompanyName;
    }

    /**
     * 精确匹配公司名称
     *
     * @param text 要检查的文本
     * @param companyName 公司名称
     * @return 是否匹配成功
     */
    private static boolean matchCompanyName(String text, String companyName) {
        if (text == null || companyName == null) {
            return false;
        }

        // 清理公司名称，移除常见的公司后缀
        String cleanCompanyName = companyName.replaceAll("(科技|有限|公司|股份|集团)", "").trim();

        // 检查是否包含完整公司名或清理后的公司名
        return text.contains(companyName) ||
                (cleanCompanyName.length() > 0 && text.contains(cleanCompanyName));
    }

    /**
     * 从AI服务返回结果中提取企业信息
     * @param aiResponse AI服务返回的响应Map
     * @param defaultEnterpriseName 默认企业名称
     * @return 提取到的企业信息，如果解析失败则返回null
     */
    public static EnterpriseInfo extractEnterpriseInfoFromAIResponse(Map<String, Object> aiResponse, String defaultEnterpriseName) {
        if (aiResponse == null || !aiResponse.containsKey("choices")) {
            return null;
        }
        
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) aiResponse.get("choices");
            if (choices.isEmpty()) {
                return null;
            }
            
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
            String content = (String) messageObj.get("content");
            
            // 如果返回结果不是null，并且包含网址，则认为是企业官网
            if (content != null && !content.equals("null") && content.contains("http")) {
                // 解析企业名称和官网地址
                String[] parts = content.split("http");
                if (parts.length >= 2) {
                    String enterpriseName = parts[0].trim();
                    String websiteUrl = "http" + parts[1].trim();
                    
                    // 验证URL是否为有效的企业官网地址
                    if (isValidEnterpriseUrl(websiteUrl)) {
                        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
                        enterpriseInfo.setEnterpriseName(enterpriseName.isEmpty() ? defaultEnterpriseName : enterpriseName);
                        enterpriseInfo.setUrl(websiteUrl);
                        return enterpriseInfo;
                    }
                }
            }
        } catch (Exception e) {
            // 解析异常时返回null
            return null;
        }
        
        return null;
    }
}