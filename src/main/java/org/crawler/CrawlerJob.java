package org.crawler;


import org.crawler.util.UrlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

/**
 * @author vitalii.levash
 */
public class CrawlerJob implements Runnable{
     private final String url;
     private PageCrawler pageCrawler;

    public CrawlerJob(String url, PageCrawler pageCrawler) {
        this.url = url;
        this.pageCrawler = pageCrawler;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.getElementsByTag("a");
            String base = doc.baseUri();
            links
                    .forEach(link-> {
                        pageCrawler.addUrl(UrlUtils.normalize(link.attr("href"),base));

                        System.out.println("-"+link.attr("href"));
                    });
             if (pageCrawler.getBarrier().getNumberWaiting()==1){
                 pageCrawler.getBarrier().await();
             }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }


}
