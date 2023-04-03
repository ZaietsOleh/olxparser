package com.olehzaiets.olxparser.parser;

import com.olehzaiets.olxparser.common.Constants;
import com.olehzaiets.olxparser.common.Tools;
import com.olehzaiets.olxparser.common.WebDriverManagerWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OlxParser {

    private static final String ARG_NAME = "-firstPage";

    private static boolean parseOnlyFirstPage = false;

    @Autowired
    private WebDriverManagerWrapper webDriverManager;

    @Autowired
    private OlxSearchedRequestMapper requestMapper;

    public static void proceedStartArguments(String[] args) {
        parseOnlyFirstPage = Arrays.stream(args).anyMatch(param -> Objects.equals(param, ARG_NAME));
    }

    /**
     * Parsing olx pages
     * @param data pass direct url or searched text
     * */
    public Set<AdInformation>  parse(String data) {
        String currentUrl = requestMapper.map(data);
        WebDriver driver = webDriverManager.createChromeWebDriver();
        Set<AdInformation> allAdsInfo = new LinkedHashSet<>();
        Set<String> processedLinks = new LinkedHashSet<>();
        while (!Objects.equals(currentUrl, "")) {
            System.out.println("Processing page: " + currentUrl);
            driver.get(currentUrl);
            Document doc = Jsoup.parse(driver.getPageSource());
            allAdsInfo.addAll(parsePages(doc, processedLinks));

            if (parseOnlyFirstPage) {
                currentUrl = "";
            } else {
                currentUrl = getNextPage(doc);
            }
        }
        return allAdsInfo;
    }

    private String getNextPage(Document doc) {
        try {
            return Constants.OLX_URL_PATTERN + doc.getElementsByAttributeValue("data-cy", "pagination-forward").first().attr("href");
        } catch (Exception ignored) { }

        return "";
    }

    private List<AdInformation> parsePages(Document doc, Set<String> processedLinks) {
        List<AdInformation> ads = new ArrayList<>();
        List<String> links = getItemsUrl(doc);
        links.removeAll(processedLinks);
        links.forEach(link -> Tools.safeRun("Failed to parse: " + link, () -> ads.add(parseAdPage(link))));
        processedLinks.addAll(links);

        return ads;
    }

    private List<String> getItemsUrl(Document document) {
        List<String> urls = new ArrayList<>();
        Elements elements = document.getElementsByAttributeValue("data-cy", "l-card");
        elements.forEach(element -> {
            Tools.safeRun("Failed to get link!", () -> {
                String link = element.getElementsByClass("css-rc5s2u")
                        .first()
                        .attr("href");

                urls.add(Constants.OLX_URL_PATTERN  + link);
            });
        });

        return urls;
    }

    private AdInformation parseAdPage(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        String name = getOwnText(doc, "data-cy", "ad_title");
        String price = getOwnText(doc, "class", "css-ddweki er34gjf0");
        String postedAt = getOwnText(doc, "data-cy", "ad-posted-at");
        String id = getOwnText(doc, "class", "css-12hdxwj er34gjf0");
        String description = getOwnText(doc, "class", "css-bgzo2k er34gjf0");

        List<String> imgUrls = new ArrayList<>();
        Elements imgElements = doc.getElementsByAttributeValue("class", "swiper-zoom-container").select("img");
        for (Element imgElement : imgElements) {
            imgUrls.add(imgElement.attr("src"));
        }

        return new AdInformation(id, url, name, price, postedAt, description, imgUrls);
    }

    private String getOwnText(Document doc, String key, String value) throws Exception {
        return doc.getElementsByAttributeValue(key, value).first().ownText();
    }

}
