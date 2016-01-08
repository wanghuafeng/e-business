package com.rong360.crawler.ds.util;

import com.rong360.crawler.page.CrawlerPage;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/27.
 */
public class HtmlUtil {
    public static Map<String, String> extractParam(String sourceCode) {
        Source source = new Source(sourceCode);
        Map<String, String> params = new HashMap<>();
        for (Element element : source.getAllElements()) {
            if ("input".equalsIgnoreCase(element.getName())) {
                if (StringUtils.isNotBlank(element.getAttributeValue("name"))) {
                    params.put(element.getAttributeValue("name"), element.getAttributeValue("value"));
                }
            }
        }
        return params;
    }

    public static void extractParam(CrawlerPage crawlerPage) {

        String sourceCode = crawlerPage.getSourceData().getSourceCode();
        Source source = new Source(sourceCode);
        for (Element element : source.getAllElements()) {
            if ("input".equalsIgnoreCase(element.getName())) {
                if (StringUtils.isNotBlank(element.getAttributeValue("name"))) {
                    if (StringUtils.isBlank(crawlerPage.getUriData().getParams().get(element.getAttributeValue("name")))) {
                        crawlerPage.getUriData().getParams().put(element.getAttributeValue("name"), element.getAttributeValue("value"));
                    }
                }
            }
        }
    }

}
