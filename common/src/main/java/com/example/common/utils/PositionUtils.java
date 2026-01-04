package com.example.common.utils;

import com.example.common.domain.entity.PositionInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 这个类要改，还十分的粗糙，随便写成这样就行了先，实在不行就介入ai
 * TODO: 记得搞剩下的信息搜集
 * TODO：记得增加现在的逻辑，要搞那个职位提取考虑其他情况，比如页面中有校园招聘和社会招聘情况
 */
public class PositionUtils {

    private static final Logger log = LoggerFactory.getLogger(PositionUtils.class);
    /**
     * 查找企业官网中的招聘信息页面链接
     *
     * @param doc 企业官网主页文档
     * @return 招聘页面URL，如果未找到则返回null
     */
    public static String findRecruitmentPageUrl(Document doc) {
        if (doc == null) {
            log.error("PositionUtils.findRecruitmentPageUrl: 传入的文档为null");
            return null;
        }
        
        // 获取网站基础URL
        String baseUrl = doc.baseUri();
        //System.out.println("PositionUtils.findRecruitmentPageUrl: 开始查找招聘页面，基础URL: {}"+ baseUrl);
        //System.out.println(doc.body());
        // 定义招聘相关关键词（扩展更多中文和英文变体）
        String[] recruitmentKeywords = {
                "招聘", "职位", "工作机会", "诚聘", "人才招聘", "人力资源",
                "加入我们", "招聘信息", "职业发展", "求职", "岗位", "招贤纳士",
                "job", "career", "join", "recruit", "zhaopin", "careers",
                "vacancies", "employment", "opportunities", "talent", "hr",
                "work", "jobs", "position", "positions", "apply", "hiring",
                "招聘岗位", "招聘职位", "加入我们", "招贤纳士", "人才战略","职位信息",
                "职业规划", "员工发展", "校园招聘", "社会招聘", "实习生招聘"
        };
        // 查找导航链接（优先检查主要导航区域，避免遍历所有链接）
        // 按优先级顺序：主导航 < 底部导航
        Elements footerLinks = doc.select("footer a, .footer a, #footer a");
        for (Element link : footerLinks) {
//            //System.out.println("正在搜索："+link.text());
            String text = link.text().toLowerCase();
            String href = link.attr("href").toLowerCase();

            // 跳过无效链接
            if (href.startsWith("javascript:") || href.startsWith("#") || href.isEmpty()) {
                continue;
            }

            // 检查链接文本或href是否包含招聘关键词
            for (String keyword : recruitmentKeywords) {
                if (text.contains(keyword) || href.contains(keyword)) {
                    return link.absUrl("href");
                }
            }
        }

        Elements navLinks = doc.select(
                "nav a, .nav a, #nav a, .header a, " +
                        "ul[class*='nav'] a, " +
                        "div[class*='nav'] a, " + // ✅ 关键！覆盖 w-nav-item
                        "div[class*='menu'] a, " +
                        "div[class*='footer'] a, " + // 避免漏掉footer
                        "a[href*='recruit'], a[href*='career']" // ❗兜底：用href关键词防遗漏
        );
        for (Element link : navLinks) {
//            //System.out.println("正在搜索："+link.text());
            String text = link.text().toLowerCase();
            String href = link.attr("href").toLowerCase();
            
            // 跳过无效链接
            if (href.startsWith("javascript:") || href.startsWith("#") || href.isEmpty()) {
                continue;
            }
            
            // 检查链接文本或href是否包含招聘关键词
            for (String keyword : recruitmentKeywords) {
                if (text.contains(keyword) || href.contains(keyword)) {
                    return link.absUrl("href");
                }
            }
        }


        // 如果主导航中没有找到，检查底部导航

        //System.out.println("PositionUtils.findRecruitmentPageUrl: 找不到招聘页面，正在尝试使用其他方式查找...");
        Elements allLinks = doc.select("a");
        for (Element link : allLinks) {
//            //System.out.println("正在搜索："+link.text());
            if (link.text().toLowerCase().contains("招聘") || link.attr("href").toLowerCase().contains("recruit")) {
                return link.absUrl("href");
            }
        }
        return null;
    }

    /**
     * 查找招聘页面中的二级招聘页面链接（如校园招聘、社会招聘等）
     *
     * @param doc 招聘页面文档
     * @return 二级招聘页面URL列表
     */
    private static List<String> findSecondaryRecruitmentPages(Document doc) {
        List<String> secondaryPages = new ArrayList<>();
        if (doc == null) {
            log.error("PositionUtils.findSecondaryRecruitmentPages: 传入的文档为null");
            return secondaryPages;
        }

        // 定义二级招聘页面相关关键词
        String[] secondaryRecruitmentKeywords = {
                "校园招聘", "社会招聘", "实习生招聘", "校招", "社招", "暑期实习", "实习",
                "campus recruitment", "social recruitment", "internship", "campus", "graduate",
                "fresh graduate", "experienced", "professional", "entry level", "senior"
        };

        // 获取网站基础URL
        String baseUrl = doc.baseUri();

        // 查找所有链接元素
        Elements allLinks = doc.select("a");
        for (Element link : allLinks) {
            String text = link.text().toLowerCase();
            String href = link.attr("href").toLowerCase();

            // 跳过无效链接
            if (href.startsWith("javascript:") || href.startsWith("#") || href.isEmpty()) {
                continue;
            }

            // 检查链接文本或href是否包含二级招聘关键词
            for (String keyword : secondaryRecruitmentKeywords) {
                if (text.contains(keyword) || href.contains(keyword)) {
                    String fullUrl = link.absUrl("href");
                    if (!secondaryPages.contains(fullUrl)) {
                        secondaryPages.add(fullUrl);
                    }
                    break;
                }
            }
        }

        return secondaryPages;
    }

    /**
     * 查找招聘页面中的分页链接
     *
     * @param doc 招聘页面文档
     * @return 分页链接URL列表
     */
    private static List<String> findPaginationLinks(Document doc) {
        List<String> paginationLinks = new ArrayList<>();
        if (doc == null) {
            log.error("PositionUtils.findPaginationLinks: 传入的文档为null");
            return paginationLinks;
        }

        // 定义分页相关关键词
        String[] paginationKeywords = {
                "下一页", "下一页>", ">>", "next", "more", "page", "翻页", 
                "第二页", "第2页", "page 2", "2", "3", "4", "5"
        };

        // 查找可能的分页链接元素
        Elements possiblePaginationElements = doc.select(
                "a:contains(下一页), a:contains(>>), a:contains(next), a:contains(page), " +
                ".pagination a, .pager a, .page-nav a, .page-navigation a"
        );

        for (Element link : possiblePaginationElements) {
            String text = link.text().toLowerCase();
            String href = link.attr("href").toLowerCase();

            // 跳过无效链接
            if (href.startsWith("javascript:") || href.startsWith("#") || href.isEmpty()) {
                continue;
            }

            // 检查链接文本是否包含分页关键词
            for (String keyword : paginationKeywords) {
                if (text.contains(keyword)) {
                    String fullUrl = link.absUrl("href");
                    if (!paginationLinks.contains(fullUrl)) {
                        paginationLinks.add(fullUrl);
                    }
                    break;
                }
            }
        }

        return paginationLinks;
    }

    /**
     * 从招聘页面提取职位信息
     *
     * @param jobPageDoc 招聘页面文档
     * @return 职位信息列表
     */
    public static List<PositionInfo> extractPositionInfo(Document jobPageDoc) {
        // 创建已处理URL集合，防止循环
        Set<String> processedUrls = new HashSet<>();
        return extractPositionInfo(jobPageDoc, processedUrls);
    }

    /**
     * 从招聘页面提取职位信息（内部递归方法）
     *
     * @param jobPageDoc    招聘页面文档
     * @param processedUrls 已处理的URL集合，防止循环
     * @return 职位信息列表
     */
    private static List<PositionInfo> extractPositionInfo(Document jobPageDoc, Set<String> processedUrls) {
        List<PositionInfo> positions = new ArrayList<>();

        if (jobPageDoc == null) {
            log.error("PositionUtils.extractPositionInfo: 传入的文档为null");
            return positions;
        }
        
        String jobPageUrl = jobPageDoc.baseUri();
        
        // 如果已经处理过该页面，直接返回空列表
        if (processedUrls.contains(jobPageUrl)) {
            log.info("PositionUtils.extractPositionInfo: 页面 {} 已处理过，跳过", jobPageUrl);
            return positions;
        }
        
        // 将当前页面标记为已处理
        processedUrls.add(jobPageUrl);
        //System.out.println("PositionUtils.extractPositionInfo: 开始从招聘页面提取职位信息，页面URL: {}"+jobPageUrl);
        //System.out.println("PositionUtils.extractPositionInfo: 页面URL: " + jobPageUrl);
        //System.out.println("PositionUtils.extractPositionInfo: URL是否包含roboticplus.com: " + jobPageUrl.contains("roboticplus.com"));
 
        // 定义职位相关关键词
        String[] positionKeywords = {
            "工程师", "开发", "程序员", "架构师", "设计师", "产品设计师", "产品经理", "项目经理",
            "前端", "后端", "全栈", "ui", "ux", "算法", "数据", "运维", "安全",
            "测试", "qa", "研发", "电气工程师", "软件工程师", "硬件工程师", "系统工程师",
            "网络工程师", "数据库工程师", "算法工程师", "数据分析师", "机器学习工程师",
            "人工智能工程师", "自动化工程师",
            "销售", "市场", "运营", "商务", "采购", "法务", "财务", "会计",
            "人事", "hr", "行政", "客服", "咨询", "顾问", "分析师", "策划",
            "销售经理", "市场经理", "运营经理", "商务经理", "采购经理", "财务经理",
            "人力资源经理", "销售总监", "市场总监", "运营总监", "财务总监",
            "经理", "主管", "专员", "助理", "总监", "ceo", "coo", "cto", "cfo", "cmo", "cio",
            "技工", "技术工", "干部" , "质检员", "操作员"
        };
        
        // 使用正则表达式匹配职位关键词
        StringBuilder regexBuilder = new StringBuilder();
        for (int i = 0; i < positionKeywords.length; i++) {
            regexBuilder.append("(");
            regexBuilder.append(positionKeywords[i]);
            regexBuilder.append(")");
            if (i < positionKeywords.length - 1) {
                regexBuilder.append("|");
            }
        }
        
        java.util.regex.Pattern positionPattern = java.util.regex.Pattern.compile(regexBuilder.toString());
        
        // 采用标签级筛选，从内向外匹配
        Elements allElements = jobPageDoc.getAllElements();
        Set<String> uniqueElements = new HashSet<>();
        List<Element> positionElements = new ArrayList<>();
        
        //System.out.println("=== 开始标签级筛选 ===");
        //System.out.println("总元素数量: " + allElements.size());
        
        // 遍历所有元素，从内向外匹配
        for (Element element : allElements) {
            String text = element.text().trim();
            
            // 跳过文本为空或长度过短的元素
            if (text.isEmpty() || text.length() < 2 || text.length() > 200) {
                continue;
            }
            
            // 使用正则表达式匹配职位关键词
            java.util.regex.Matcher matcher = positionPattern.matcher(text);
            if (matcher.find() && isLikelyPositionInfo(text)) {
                String outerHtml = element.outerHtml();
                if (!uniqueElements.contains(outerHtml)) {
                    uniqueElements.add(outerHtml);
                    positionElements.add(element);
                    //System.out.println("匹配到职位元素: " + text);
                }
            }
        }
        
        //System.out.println("=== 标签级筛选完成 ===");
        //System.out.println("匹配到的职位元素数量: " + positionElements.size());
        
        // 遍历找到的职位元素
        for (Element element : positionElements) {
            String text = element.text().trim();
            
            //System.out.println("=== 处理元素 ===");
            //System.out.println("元素文本: " + text);
            //System.out.println("元素HTML: " + element.outerHtml());
            
            // 提取职位信息
            //System.out.println("调用isLikelyPositionInfo方法进行判断");
            if (isLikelyPositionInfo(text)) {
                //System.out.println("isLikelyPositionInfo返回true，继续处理");
                PositionInfo position = new PositionInfo();
                position.setName(text);

                // 设置URL逻辑
                if ("a".equals(element.tagName().toLowerCase())) {
                    position.setUrl(element.absUrl("href"));
                } else {
                    // 如果不是a标签，查找子元素中的a标签
                    Elements links = element.getAllElements();
                    Element childLink = null;
                    for (Element link : links) {
                        if ("a".equals(link.tagName().toLowerCase()) && link.hasAttr("href")) {
                            childLink = link;
                            break;
                        }
                    }
                    if (childLink != null) {
                        position.setUrl(childLink.absUrl("href"));
                    } else {
                        position.setUrl(jobPageDoc.baseUri());
                    }
                }

                // 提取薪资信息（如果存在）
                String salary = extractSalaryInfo(element);
                position.setSalary(salary);

                // 提取描述信息（如果存在）
                String description = extractDescription(element);
                position.setDescription(description);

                // 添加到列表前，检查是否与已添加的职位重复
                boolean isDuplicate = false;
                for (PositionInfo existingPos : positions) {
                    if (existingPos.getName() != null && existingPos.getName().equals(position.getName())) {
                        isDuplicate = true;
                        break;
                    }
                }
                
                if (!isDuplicate) {
                    //System.out.println("添加职位到列表: " + position.getName());
                    positions.add(position);
                }
            } else {
                //System.out.println("isLikelyPositionInfo返回false，跳过此元素");
                // 打印更详细的信息，帮助调试
                String lowerText = text.toLowerCase();
                //System.out.println("详细排除原因分析：");
                //System.out.println("文本长度是否合理：" + (text.length() >= 2 && text.length() < 200));
                // 检查是否包含职位关键词
                boolean hasKeyword = false;
                for (String keyword : new String[]{"工程师", "经理", "设计师"}) {
                    if (lowerText.contains(keyword)) {
                        hasKeyword = true;
                        break;
                    }
                }
                //System.out.println("是否包含核心职位关键词：" + hasKeyword);
            }
        }

        // 实现职位筛选机制，过滤掉离谱的结果
        List<PositionInfo> filteredPositions = new ArrayList<>();
        for (PositionInfo position : positions) {
            if (isValidPosition(position)) {
                filteredPositions.add(position);
            }
        }

        // 无论当前页面是否有职位信息，都尝试查找并处理二级招聘页面（如校园招聘、社会招聘）
        List<String> secondaryPages = findSecondaryRecruitmentPages(jobPageDoc);
        for (String secondaryPageUrl : secondaryPages) {
            try {
                System.out.println("正在处理二级招聘页面: " + secondaryPageUrl);
                // 递归调用extractPositionInfo方法处理二级页面
                Document secondaryDoc = Jsoup.connect(secondaryPageUrl).get();
                List<PositionInfo> secondaryPositions = extractPositionInfo(secondaryDoc, processedUrls);
                filteredPositions.addAll(secondaryPositions);
            } catch (IOException e) {
                log.error("PositionUtils.extractPositionInfo: 处理二级招聘页面时发生异常，URL: {}", secondaryPageUrl, e);
            }
        }

        // 处理当前页面的分页链接 
        List<String> paginationLinks = findPaginationLinks(jobPageDoc);
        for (String paginationUrl : paginationLinks) {
            try {
                // 递归调用extractPositionInfo方法处理分页页面
                Document paginationDoc = Jsoup.connect(paginationUrl).get();
                List<PositionInfo> paginationPositions = extractPositionInfo(paginationDoc, processedUrls);
                filteredPositions.addAll(paginationPositions);
            } catch (IOException e) {
                log.error("PositionUtils.extractPositionInfo: 处理分页页面时发生异常，URL: {}", paginationUrl, e);
            }
        }

        return filteredPositions;
    }

    // ----------------上方为暴露使用方法------------------



    /**
     * 从元素中提取职位名称
     *
     * @param text 包含职位信息的文本
     * @return 职位名称
     */
    private static boolean isLikelyPositionInfo(String text) {
        if (text == null || text.isEmpty()) {
            //System.out.println("排除原因：文本为null或空");
            return false;
        }
        
        // 转换为小写进行匹配
        String lowerText = text.toLowerCase();
        //System.out.println("=== 分析文本是否为职位信息 ===");
        //System.out.println("待分析文本: " + text);
        //System.out.println("文本长度: " + text.length());
        
        // 简化排除规则，只保留最必要的排除关键词
        String[] minimalExcludeKeywords = {
            "公司简介", "企业介绍", "组织架构", "管理团队", "发展历程"
        };
        
        // 检查是否包含排除关键词
        boolean containsExcludeKeyword = false;
        //System.out.println("开始检查排除关键词...");
        for (String keyword : minimalExcludeKeywords) {
            if (lowerText.contains(keyword)) {
                //System.out.println("排除原因：包含排除关键词：" + keyword);
                containsExcludeKeyword = true;
                break;
            }
        }
        //System.out.println("排除关键词检查结果：containsExcludeKeyword=" + containsExcludeKeyword);
        
        if (containsExcludeKeyword) {
            return false;
        }
        
        // 检查是否为部门名称组合（包含多个部门名称用空格、顿号或其他分隔符分隔）
        boolean isDepartmentCombination = lowerText.matches(".*[\\s、,，/]+.*[\\s、,，/]+.*") &&
                                          (lowerText.contains("部") || lowerText.contains("部门") ||
                                           lowerText.contains("事业部") || lowerText.contains("中心") ||
                                           lowerText.contains("组") || lowerText.contains("团队"));
        //System.out.println("部门组合检查：isDepartmentCombination=" + isDepartmentCombination);
        if (isDepartmentCombination) {
            //System.out.println("排除原因：是部门名称组合");
            return false;
        }
        
        // 职位关键词列表，专注于核心职位名称
        String[] positionKeywords = {
            // 技术类核心职位
            "工程师", "开发", "程序员", "架构师", "设计师", "产品设计师", "产品经理", "项目经理",
            "前端", "后端", "全栈", "ui", "ux", "算法", "数据", "运维", "安全",
            "测试", "qa", "研发", "电气工程师", "软件工程师", "硬件工程师", "系统工程师",
            "网络工程师", "数据库工程师", "算法工程师", "数据分析师", "机器学习工程师",
            "人工智能工程师", "自动化工程师",
            // 业务类核心职位
            "销售", "市场", "运营", "商务", "采购", "法务", "财务", "会计",
            "人事", "hr", "行政", "客服", "咨询", "顾问", "分析师", "策划",
            "销售经理", "市场经理", "运营经理", "商务经理", "采购经理", "财务经理",
            "人力资源经理", "销售总监", "市场总监", "运营总监", "财务总监",
            // 其他核心职位
            "经理", "主管", "专员", "助理", "总监", "ceo", "coo", "cto", "cfo", "cmo", "cio"
        };
        
        // 职位后缀列表
        String[] positionSuffixes = {
            "工程师", "经理", "主管", "专员", "助理", "顾问", "分析师", "设计师", "总监", "架构师",
            "师", "长", "员", "理", "官", "总"
        };
        
        boolean containsPositionKeyword = false;
        
        //System.out.println("=== 开始匹配职位关键词 ===");
        //System.out.println("待匹配文本：" + text);
        //System.out.println("待匹配文本(lowercase)：" + lowerText);
        
        // 详细记录每个关键词的匹配结果
        //System.out.println("=== 关键词匹配详情 ===");
        for (String keyword : positionKeywords) {
            boolean matched = lowerText.contains(keyword);
            //System.out.println("关键词'" + keyword + "'匹配结果：" + matched);
            if (matched) {
                containsPositionKeyword = true;
                //System.out.println("匹配到职位关键词：" + keyword);
                break;
            }
        }
        
        if (!containsPositionKeyword) {
            //System.out.println("未匹配到任何职位关键词");
        }
        
        // 特殊判断：如果包含职位后缀，也可能是职位
        boolean containsPositionSuffix = false;
        //System.out.println("=== 职位后缀匹配详情 ===");
        for (String suffix : positionSuffixes) {
            boolean matched = lowerText.contains(suffix);
            //System.out.println("后缀'" + suffix + "'匹配结果：" + matched);
            if (matched) {
                containsPositionSuffix = true;
                //System.out.println("匹配到职位后缀：" + suffix);
                break;
            }
        }
        

        // 简化综合判断：只要包含职位关键词、职位后缀或职位前缀，就可能是职位信息
        boolean isPosition = containsPositionKeyword || containsPositionSuffix ;
        
        // 调整字数限制：职位信息可能包含更多内容，将限制放宽到200个字符以内
        boolean isLengthReasonable = text.length() >= 2 && text.length() < 300;
        
        //System.out.println("=== 最终判断结果 ===");
        //System.out.println("判断结果：containsPositionKeyword=" + containsPositionKeyword + ", containsPositionSuffix=" + containsPositionSuffix);
        //System.out.println("判断结果：isPosition=" + isPosition + ", isLengthReasonable=" + isLengthReasonable + ", 最终结果=" + (isPosition && isLengthReasonable));
        return isPosition && isLengthReasonable;
    }

    /**
     * 验证职位信息是否有效，过滤掉离谱的结果
     *
     * @param position 职位信息对象
     * @return 是否为有效职位
     */
    private static boolean isValidPosition(PositionInfo position) {
        if (position == null || position.getName() == null || position.getName().isEmpty()) {
            //System.out.println("排除原因：职位信息为空或职位名称为空");
            return false;
        }
        
        String positionName = position.getName().trim();
        String lowerPositionName = positionName.toLowerCase();
        
        // 过滤掉包含明显非职位关键词的结果
        String[] invalidKeywords = {
            "联系方式", "邮箱", "电话", "地址", "邮编", "传真",
            "公司网站", "官方网站", "版权所有", "备案号", "友情链接",
            "首页", "关于", "服务", "解决方案", "新闻",
            "案例", "博客", "论坛", "帮助", "隐私政策", "使用条款"
        };
        
        for (String keyword : invalidKeywords) {
            if (lowerPositionName.contains(keyword)) {
                //System.out.println("排除原因：职位名称包含无效关键词：" + keyword);
                return false;
            }
        }
        
        // 过滤掉看起来像部门列表的结果
        if (positionName.matches(".*[、,，/].*[、,，/].*")) {
            //System.out.println("排除原因：职位名称包含多个分隔符，可能是部门列表");
            return false;
        }
        
        // 过滤掉过长的职位名称（超过50个字符可能是描述性文本）
        if (positionName.length() > 50) {
            //System.out.println("排除原因：职位名称过长，可能是描述性文本");
            return false;
        }
        
        // 确保职位名称至少包含一个职位关键词
        return true;
    }
    
    /**
     * 从元素中提取薪资信息
     *
     * @param element 包含职位信息的元素
     * @return 薪资信息
     */
    private static String extractSalaryInfo(Element element) {
        if (element == null) {
            return null;
        }
        
        // 定义所有要检查的文本源（按优先级排序）
        List<String> textSources = new ArrayList<>();
        
        // 1. 当前元素文本
        textSources.add(element.text().trim());
        
        // 2. 父元素文本
        Element parent = element.parent();
        if (parent != null) {
            textSources.add(parent.text().trim());
        }
        
        // 3. 下一个兄弟元素文本
        Element nextSibling = element.nextElementSibling();
        if (nextSibling != null) {
            textSources.add(nextSibling.text().trim());
        }
        
        // 4. 上一个兄弟元素文本
        Element prevSibling = element.previousElementSibling();
        if (prevSibling != null) {
            textSources.add(prevSibling.text().trim());
        }
        
        // 5. 当前元素的所有子元素文本（递归检查）
        Elements allChildren = element.getAllElements();
        for (Element child : allChildren) {
            if (child != element) { // 跳过当前元素本身
                String childText = child.text().trim();
                if (!childText.isEmpty()) {
                    textSources.add(childText);
                }
            }
        }
        
        // 6. 父元素的所有子元素文本（递归检查）
        if (parent != null) {
            Elements parentAllChildren = parent.getAllElements();
            for (Element child : parentAllChildren) {
                if (child != parent && child != element) { // 跳过父元素本身和当前元素
                    String childText = child.text().trim();
                    if (!childText.isEmpty()) {
                        textSources.add(childText);
                    }
                }
            }
        }
        
        // 定义常见薪资格式的正则表达式（增强版）
        String[] salaryPatterns = {
            // 10-15k, 10-15K, 10.5-15.5k, 10k-15k, 10k~15k, 10k 至 15k
            "(\\d+[\\.-]?\\d*)[kK]\\s*[-—~至]\\s*(\\d+[\\.-]?\\d*)[kK]",
            // 12k, 12K, 12.5k
            "(\\d+[\\.-]?\\d*)[kK]",
            // 10,000-15,000元/月, 10000-15000元, 10000至15000元
            "(\\d{1,3}(?:,\\d{3})*|[\\d]+)[-—~至](\\d{1,3}(?:,\\d{3})*|[\\d]+)\\s*元",
            // 12,000元/月, 12000元
            "(\\d{1,3}(?:,\\d{3})*|[\\d]+)\\s*元",
            // 年薪10-15万, 10-15万年薪, 10-15万/年, 10-15万 至 20万
            "(\\d+[\\.-]?\\d*)[-—~至](\\d+[\\.-]?\\d*)\\s*万",
            // 12万年薪, 12万/年
            "(\\d+[\\.-]?\\d*)\\s*万",
            // 10-15k/月, 10-15k/年
            "(\\d+[\\.-]?\\d*)[kK]\\s*[-—~至]\\s*(\\d+[\\.-]?\\d*)[kK]\\s*/\\s*(月|年)",
            // 12k/月, 12k/年
            "(\\d+[\\.-]?\\d*)[kK]\\s*/\\s*(月|年)",
            // 10,000-15,000元/月, 10000-15000元/年
            "(\\d{1,3}(?:,\\d{3})*|[\\d]+)[-—~至](\\d{1,3}(?:,\\d{3})*|[\\d]+)\\s*元\\s*/\\s*(月|年)",
            // 12,000元/月, 12000元/年
            "(\\d{1,3}(?:,\\d{3})*|[\\d]+)\\s*元\\s*/\\s*(月|年)",
            // 10-15万/月, 10-15万/年
            "(\\d+[\\.-]?\\d*)[-—~至](\\d+[\\.-]?\\d*)\\s*万\\s*/\\s*(月|年)",
            // 12万/月, 12万/年
            "(\\d+[\\.-]?\\d*)\\s*万\\s*/\\s*(月|年)",
            // 30-50万年薪
            "(\\d+[\\.-]?\\d*)[-—~至](\\d+[\\.-]?\\d*)\\s*万年薪",
        };
        
        // 遍历所有文本源
        for (String text : textSources) {
            if (text == null || text.isEmpty()) {
                continue;
            }
            
            // 检查特殊情况：薪资面议
            if (text.contains("薪资面议") || text.contains("待遇面议") || text.contains("薪资保密") || text.contains("面议")) {
                return "薪资面议";
            }
            
            // 遍历所有正则表达式，尝试匹配薪资信息
            for (String pattern : salaryPatterns) {
                java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher matcher = regex.matcher(text);
                
                if (matcher.find()) {
                    // 根据匹配的组返回相应的薪资格式
                    if (matcher.groupCount() == 5) {
                        if (pattern.contains("[$€£]") && pattern.contains("/")) {
                            // 国际薪资格式带单位：$10,000-$15,000/月
                            String currency1 = matcher.group(1);
                            String amount1 = matcher.group(2);
                            String amount2 = matcher.group(4);
                            String timeUnit = matcher.group(5);
                            return currency1 + amount1 + "-" + currency1 + amount2 + "/" + timeUnit;
                        }
                    } else if (matcher.groupCount() == 4) {
                        if (pattern.contains("[$€£]") && !pattern.contains("/")) {
                            // 国际薪资格式：$10,000-$15,000
                            String currency1 = matcher.group(1);
                            String amount1 = matcher.group(2);
                            String amount2 = matcher.group(4);
                            return currency1 + amount1 + "-" + currency1 + amount2;
                        } else if (pattern.contains("USD|EUR|GBP")) {
                            // 国际薪资格式：10,000 USD - 15,000 USD
                            String amount1 = matcher.group(1);
                            String currency = matcher.group(2);
                            String amount2 = matcher.group(3);
                            return amount1 + " " + currency + " - " + amount2 + " " + currency;
                        } else {
                            // 范围薪资带单位和时间，如10-15k/月
                            return matcher.group(1) + "k-" + matcher.group(2) + "k/" + matcher.group(4);
                        }
                    } else if (matcher.groupCount() == 3) {
                        if (pattern.contains("元") && pattern.contains("/")) {
                            // 范围薪资带元单位和时间，如10000-15000元/月
                            return matcher.group(1) + "-" + matcher.group(2) + "元/" + matcher.group(3);
                        } else if (pattern.contains("万") && pattern.contains("/")) {
                            // 范围薪资带万单位和时间，如10-15万/月
                            return matcher.group(1) + "-" + matcher.group(2) + "万/" + matcher.group(3);
                        } else if (pattern.contains("万年薪")) {
                            // 年薪范围，如30-50万年薪
                            return matcher.group(1) + "-" + matcher.group(2) + "万年薪";
                        } else if (pattern.contains("k") && pattern.contains("/")) {
                            // 范围薪资带k单位和时间，如10-15k/年
                            return matcher.group(1) + "k-" + matcher.group(2) + "k/" + matcher.group(3);
                        } else if (pattern.contains("[$€£]") && pattern.contains("/")) {
                            // 国际薪资格式单一值带单位：$12,000/月
                            String currency = matcher.group(1);
                            String amount = matcher.group(2);
                            String timeUnit = matcher.group(3);
                            return currency + amount + "/" + timeUnit;
                        }
                    } else if (matcher.groupCount() == 2) {
                        if (pattern.contains("k") && pattern.contains("/")) {
                            // 单一薪资带k单位和时间，如12k/月
                            return matcher.group(1) + "k/" + matcher.group(2);
                        } else if (pattern.contains("元") && pattern.contains("/")) {
                            // 单一薪资带元单位和时间，如12000元/月
                            return matcher.group(1) + "元/" + matcher.group(2);
                        } else if (pattern.contains("万") && pattern.contains("/")) {
                            // 单一薪资带万单位和时间，如12万/月
                            return matcher.group(1) + "万/" + matcher.group(2);
                        } else if (pattern.contains("[-—~至]") && pattern.contains("k")) {
                            // 范围薪资，如10-15k
                            return matcher.group(1) + "k-" + matcher.group(2) + "k";
                        } else if (pattern.contains("[-—~至]") && pattern.contains("元")) {
                            // 范围薪资，如10000-15000元
                            return matcher.group(1) + "-" + matcher.group(2) + "元";
                        } else if (pattern.contains("[-—~至]") && pattern.contains("万")) {
                            // 范围薪资，如10-15万
                            return matcher.group(1) + "-" + matcher.group(2) + "万";
                        } else {
                            // 单一薪资，如12k
                            return matcher.group(1) + "k";
                        }
                    } else if (matcher.groupCount() == 1) {
                        if (pattern.contains("k")) {
                            // 单一薪资，如12k
                            return matcher.group(1) + "k";
                        } else if (pattern.contains("元")) {
                            // 单一薪资，如12000元
                            return matcher.group(1) + "元";
                        } else if (pattern.contains("万")) {
                            // 单一薪资，如12万
                            return matcher.group(1) + "万";
                        }
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * 从元素中提取描述信息
     *
     * @param element 包含职位信息的元素
     * @return 描述信息
     */
    private static String extractDescription(Element element) {
        if (element == null) {
            return null;
        }
        
        // 定义描述关键词
        String[] descriptionKeywords = {
            // 中文关键词（扩展更多常见描述关键词）
            "职责", "要求", "任职", "条件", "负责", "工作", "内容", "职位", "描述", "岗位",
            "岗位职责", "岗位要求", "职位职责", "职位要求", "工作内容", "任职资格", "应聘条件",
            "技能要求", "工作经验", "学历要求", "能力要求", "岗位说明", "职位说明", "工作要求",
            "岗位描述", "职位描述", "工作描述", "招聘要求", "录用条件", "职位资格", "岗位条件",
            "工作责任", "岗位责任", "职位责任", "主要职责", "核心职责", "基本要求", "岗位职责描述",
            "任职条件", "工作任务", "岗位任务", "职位任务", "工作范围", "岗位职责范围",
            // 英文关键词
            "Responsibilities", "Requirements", "Qualifications", "Duties", "Role", "Job", "Description",
            "Responsibility", "Requirement", "Qualification", "Duty", "Position", "Summary", "Details",
            "Responsibilities and Duties", "Job Requirements", "Position Requirements", "Job Qualifications",
            "Required Skills", "Essential Duties", "Core Responsibilities", "Key Requirements", "Job Summary"
        };
        
        StringBuilder description = new StringBuilder();
        String elementText = element.text().trim();
        
        // 方法1：尝试获取父元素的所有文本内容（排除职位名称本身）
        Element parent = element.parent();
        if (parent != null) {
            String parentText = parent.text().trim();
            
            // 如果父元素文本长度远大于当前元素文本，说明可能包含描述信息
            if (parentText.length() > elementText.length() + 20) {
                // 移除职位名称部分，保留剩余文本作为描述
                String desc = parentText.replace(elementText, "").trim();
                if (!desc.isEmpty()) {
                    description.append(desc).append(" ");
                }
            }
        }
        
        // 方法2：尝试获取当前元素的所有兄弟元素的文本内容
        Element currentSibling = element.nextElementSibling();
        while (currentSibling != null) {
            String siblingText = currentSibling.text().trim();
            if (!siblingText.isEmpty() && siblingText.length() > 8) {
                // 检查兄弟元素是否包含薪资格式，避免重复提取
                if (!siblingText.matches(".*\\d+[kK万元].*")) {
                    description.append(siblingText).append(" ");
                    break; // 只取第一个有意义的兄弟元素
                }
            }
            currentSibling = currentSibling.nextElementSibling();
        }
        
        // 方法3：尝试获取元素的所有子元素的文本内容（排除职位名称本身）
        Elements children = element.children();
        if (!children.isEmpty()) {
            for (Element child : children) {
                String childText = child.text().trim();
                if (!childText.isEmpty() && !childText.equals(elementText) && childText.length() > 5) {
                    description.append(childText).append(" ");
                }
            }
        }
        
        // 方法4：查找包含描述关键词的元素（替代选择器查找）
        if (parent != null && description.length() == 0) {
            Elements allParentElements = parent.getAllElements();
            for (Element el : allParentElements) {
                if (el != parent && el != element) {
                    String elText = el.text().trim();
                    if (elText.length() > 50 && elText.length() < 1000) {
                        // 检查是否包含多个描述关键词
                        int keywordCount = 0;
                        for (String keyword : descriptionKeywords) {
                            if (elText.contains(keyword)) {
                                keywordCount++;
                                if (keywordCount >= 2) {
                                    description.append(elText).append(" ");
                                    break;
                                }
                            }
                        }
                        if (description.length() > 0) {
                            break;
                        }
                    }
                }
            }
        }
        
        // 如果父元素中没有找到，在当前元素的子元素中查找
        if (description.length() == 0) {
            Elements allChildElements = element.getAllElements();
            for (Element el : allChildElements) {
                if (el != element) {
                    String elText = el.text().trim();
                    if (elText.length() > 50 && elText.length() < 1000) {
                        // 检查是否包含多个描述关键词
                        int keywordCount = 0;
                        for (String keyword : descriptionKeywords) {
                            if (elText.contains(keyword)) {
                                keywordCount++;
                                if (keywordCount >= 2) {
                                    description.append(elText).append(" ");
                                    break;
                                }
                            }
                        }
                        if (description.length() > 0) {
                            break;
                        }
                    }
                }
            }
        }
        
        // 方法5：尝试从父元素的父元素中提取描述
        if (description.length() == 0 && parent != null) {
            Element grandParent = parent.parent();
            if (grandParent != null) {
                String grandParentText = grandParent.text().trim();
                if (grandParentText.length() > elementText.length() + 50) {
                    String desc = grandParentText.replace(elementText, "").trim();
                    if (!desc.isEmpty()) {
                        description.append(desc).append(" ");
                    }
                }
            }
        }
        
        // 方法6：查找包含特定关键词的元素作为描述
        if (description.length() == 0) {
            // 首先在当前元素的父元素中查找
            if (parent != null) {
                Elements allElements = parent.getAllElements();
                for (Element el : allElements) {
                    String elText = el.text().trim();
                    if (elText.length() > 20 && elText.length() < 1000) {
                        int keywordCount = 0;
                        for (String keyword : descriptionKeywords) {
                            if (elText.contains(keyword)) {
                                keywordCount++;
                                // 如果包含多个关键词，更可能是描述内容
                                if (keywordCount >= 2) {
                                    description.append(elText).append(" ");
                                    break;
                                }
                            }
                        }
                        // 包含单个关键词也可以尝试使用
                        if (description.length() == 0 && keywordCount == 1) {
                            description.append(elText).append(" ");
                        }
                    }
                    if (description.length() > 0) {
                        break;
                    }
                }
            }
            
            // 如果父元素中没有找到，在当前元素的所有子元素中查找
            if (description.length() == 0) {
                Elements allElements = element.getAllElements();
                for (Element el : allElements) {
                    String elText = el.text().trim();
                    if (elText.length() > 20 && elText.length() < 1000) {
                        int keywordCount = 0;
                        for (String keyword : descriptionKeywords) {
                            if (elText.contains(keyword)) {
                                keywordCount++;
                                // 如果包含多个关键词，更可能是描述内容
                                if (keywordCount >= 2) {
                                    description.append(elText).append(" ");
                                    break;
                                }
                            }
                        }
                        // 包含单个关键词也可以尝试使用
                        if (description.length() == 0 && keywordCount == 1) {
                            description.append(elText).append(" ");
                        }
                    }
                    if (description.length() > 0) {
                        break;
                    }
                }
            }
        }
        
        // 方法7：查找包含特定关键词的标签内容（替代标签选择器）
        if (description.length() == 0) {
            // 查找所有文本长度在50-1000之间的元素
            Elements targetElements;
            if (parent != null) {
                targetElements = parent.getAllElements();
            } else {
                targetElements = element.getAllElements();
            }
            
            for (Element el : targetElements) {
                String elText = el.text().trim();
                if (elText.length() > 50 && elText.length() < 1000) {
                    // 检查是否包含任何描述关键词
                    int keywordCount = 0;
                    for (String keyword : descriptionKeywords) {
                        if (elText.contains(keyword)) {
                            keywordCount++;
                            if (keywordCount >= 2) { // 至少包含两个关键词
                                description.append(elText).append(" ");
                                break;
                            }
                        }
                    }
                    if (description.length() > 0) {
                        break;
                    }
                }
            }
        }
        
        // 清理和格式化描述信息
        String result = description.toString().trim();
        if (result.isEmpty()) {
            return null;
        }
        
        // 去除重复的空格
        result = result.replaceAll("\\s{2,}", " ");
        
        // 限制描述长度，保持简洁
        if (result.length() > 300) {
            result = result.substring(0, 300) + "...";
        }
        
        return result;
    }
}

