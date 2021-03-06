package us.codecraft.xsoup;

import junit.framework.Assert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class XsoupTest {

    private String html = "<html><body><div id='test'>aaa<div><a href=\"https://github.com\">github.com</a></div></div></body></html>";

    private String htmlClass = "<html><body><div class='a b c'><div><a href=\"https://github.com\">github.com</a></div></div><div>b</div></body></html>";

    @Test
    public void testSelect() {

        String html = "<html><div><a href='https://github.com'>github.com</a></div>" +
                "<table><tr><td>a</td><td>b</td></tr></table></html>";

        Document document = Jsoup.parse(html);

        String result = Xsoup.compile("//a/@href").evaluate(document).get();
        Assert.assertEquals("https://github.com", result);

        List<String> list = Xsoup.compile("//tr/td/text()").evaluate(document).list();
        Assert.assertEquals("a", list.get(0));
        Assert.assertEquals("b", list.get(1));
    }

    @Test
    public void testParent() {

        Document document = Jsoup.parse(html);

        String result = Xsoup.select(document, "/html/body/div/div/a").get();
        Assert.assertEquals("<a href=\"https://github.com\">github.com</a>", result);

        result = Xsoup.select(document, "/html//div/div/a").get();
        Assert.assertEquals("<a href=\"https://github.com\">github.com</a>", result);

        result = Xsoup.select(document, "/html/div/div/a").get();
        Assert.assertNull(result);

    }

    @Test
    public void testByAttribute() {

        Document document = Jsoup.parse(html);

        String result = Xsoup.select(document, "//a[@href]").get();
        Assert.assertEquals("<a href=\"https://github.com\">github.com</a>", result);

        result = Xsoup.select(document, "//a[@id]").get();
        Assert.assertNull(result);

        result = Xsoup.select(document, "//div[@id=test]").get();
        String expectedDiv = "<div id=\"test\">\n" +
                " aaa\n" +
                " <div>\n" +
                "  <a href=\"https://github.com\">github.com</a>\n" +
                " </div>\n" +
                "</div>";
        Assert.assertEquals(expectedDiv, result);

        result = Xsoup.select(document, "//div[@id='test']").get();
        Assert.assertEquals(expectedDiv, result);
        result = Xsoup.select(document, "//div[@id=\"test\"]").get();
        Assert.assertEquals(expectedDiv, result);
    }

    @Test
    public void testClass() {

        Document document = Jsoup.parse(htmlClass);

        String result = Xsoup.select(document, "//div[@class=a]").get();
        Assert.assertEquals("<div class=\"a b c\">\n" +
                " <div>\n" +
                "  <a href=\"https://github.com\">github.com</a>\n" +
                " </div>\n" +
                "</div>", result);

        result = Xsoup.select(document, "//div[@class=d]").get();
        Assert.assertNull(result);

    }

    @Test
    public void testNth() {

        Document document = Jsoup.parse(htmlClass);

        String result = Xsoup.select(document, "//body/div[1]").get();
        Assert.assertEquals("<div class=\"a b c\">\n" +
                " <div>\n" +
                "  <a href=\"https://github.com\">github.com</a>\n" +
                " </div>\n" +
                "</div>", result);

        result = Xsoup.select(document, "//body/div[2]").get();
        Assert.assertEquals("<div>\n" +
                " b\n" +
                "</div>", result);

    }

    @Test
    public void testAttribute() {

        Document document = Jsoup.parse(htmlClass);

        String result = Xsoup.select(document, "//a/@href").get();
        Assert.assertEquals("https://github.com", result);

        result = Xsoup.select(document, "//a/text()").get();
        Assert.assertEquals("github.com", result);

        result = Xsoup.select(document, "//div[@class=a]/html()").get();
        Assert.assertEquals("<div>\n" +
                " <a href=\"https://github.com\">github.com</a>\n" +
                "</div>", result);

    }

    @Test
    public void testWildcard() {

        Document document = Jsoup.parse(htmlClass);

        String result = Xsoup.select(document, "//*[@href]/@href").get();
        Assert.assertEquals("https://github.com", result);

        result = Xsoup.select(document, "//*[@class=a]/html()").get();
        Assert.assertEquals("<div>\n" +
                " <a href=\"https://github.com\">github.com</a>\n" +
                "</div>", result);

        List<String> list = Xsoup.select(document, "//*[@*]/html()").list();
        Assert.assertEquals("<div>\n" +
                " <a href=\"https://github.com\">github.com</a>\n" +
                "</div>", list.get(0));
        Assert.assertEquals("github.com",list.get(1));
    }

    @Test
    public void testFuzzyValueMatch() {

        Document document = Jsoup.parse(html);

        String result = Xsoup.select(document, "//*[@id~=te]/text()").get();
        Assert.assertEquals("aaa",result);
        result = Xsoup.select(document, "//*[@id$=st]/text()").get();
        Assert.assertEquals("aaa",result);
        result = Xsoup.select(document, "//*[@id*=es]/text()").get();
        Assert.assertEquals("aaa",result);
        result = Xsoup.select(document, "//*[@id~='tes[t]+']/text()").get();
        Assert.assertEquals("aaa",result);

        result = Xsoup.select(document, "//*[@id~=te]/allText()").get();
        Assert.assertEquals("aaa github.com",result);
    }

    @Test
    public void testRegex() {

        Document document = Jsoup.parse(html);

        String result = Xsoup.select(document, "//*[@id~=te]/regex('gi\\w+ub')").get();
        Assert.assertEquals("github",result);

        result = Xsoup.select(document, "//a/regex('@href','.*gi\\w+ub.*')").get();
        Assert.assertEquals("https://github.com",result);

        result = Xsoup.select(document, "//a/regex('@href','.*(gi\\w+ub).*',1").get();
        Assert.assertEquals("github",result);
    }

}
