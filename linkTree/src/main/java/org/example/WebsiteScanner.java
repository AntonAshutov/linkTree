package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebsiteScanner {
    static SiteAccessController siteAccessController;
    public static int excludedInnerElement;
    public static int excludedUrlBased;
    public static int resultCount;
    public static int pagesScanned;
    public static final Set<URL> processedLinks = new HashSet<>();

    public Map<URL, Object> scanSite(String url) {
        excludedInnerElement = 0;
        excludedUrlBased = 0;
        resultCount = 0;
        pagesScanned = 0;
        siteAccessController = new SiteAccessController();
        URL startPage;
        try {
            startPage = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        processedLinks.clear();
        Map<URL, Object> result = new HashMap<>();
        result.put(startPage, null);
        new ForkJoinPool().invoke(new ScanTask(result, startPage));
        return result;
    }

    private static class ScanTask extends RecursiveTask<Void> {
        private final Map<URL, Object> result;
        private final URL page;

        ScanTask(Map<URL, Object> result, URL page) {
            this.result = result;
            this.page = page;
        }

        @Override
        protected Void compute() {
            List<URL> links = extractLinks(page);
            List<ScanTask> tasks = new ArrayList<>();
            for (URL link : links) {
                Map<URL, Object> subMap = new HashMap<>();
                result.put(link, subMap);
                ScanTask task = new ScanTask(subMap, link);
                tasks.add(task);
                task.fork();
            }
            for (ScanTask task : tasks) {
                task.join();
            }
            return null;
        }

        private List<URL> extractLinks(URL page) {
            List<URL> links = new ArrayList<>();
            System.out.println("scanning " + page + "   total count = " + resultCount + "   pages scanned: " + pagesScanned);
            try {
                String pageContent = siteAccessController.accessSite(page);
                Document document = Jsoup.parse(pageContent);
                Elements elements = document.select("a[href]");
//                if (!PageHash.HashIsNew(pageContent)){
//                    return links;
//                }
                for (Element element : elements) {
                    String link = element.attr("href");
                    URL absoluteURL;
                    try {
                        absoluteURL = new URL(page, link);
                    } catch (MalformedURLException e) {
                        continue;
                    }
                    //проверям что не вышли за пределы родительского домена
                    if (!absoluteURL.getHost().equals(page.getHost())) {
                        continue;
                    }
                    //исключаем из списка ссылки  на внутренние элементы страниц
                    if (absoluteURL.toString().contains("#")) {
                        excludedInnerElement++;
                        continue;
                    }
                    synchronized (processedLinks) {
                        if (processedLinks.contains(absoluteURL)) {
                            excludedUrlBased++;
                            continue;
                        }
                        processedLinks.add(absoluteURL);
                    }

                    resultCount++;
                    links.add(absoluteURL);
                }
                pagesScanned++;
                return links;
            } catch (IOException e) {
                return links;
            } catch (InterruptedException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
    }
}
