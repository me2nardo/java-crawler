package org.crawler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vitalii.levash
 */
public class PageCrawler {

    private BlockingQueue<String> link = new LinkedBlockingQueue<>();
    private CyclicBarrier barrier = new CyclicBarrier(2);
    private Set<String> result = new HashSet<>();

    private ExecutorService executorService;
    private final String url;

    public PageCrawler(String url) {
        this.url = url;
    }

    public void addUrl(String url){
        link.add(url);
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public void setBarrier(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    public void start() throws InterruptedException, BrokenBarrierException {
        String next = null;
        executorService = Executors.newCachedThreadPool();
        while(!link.isEmpty()){
            next = link.take();

            if (next==null){
                System.out.println("Queue is empty");
            }

            if (!checkUrl(next)) continue;

            result.add(next);
            System.out.println(next);

            CrawlerJob job = new CrawlerJob(next,this);
            executorService.submit(job);

            if (link.isEmpty()){
                barrier.await();
            }

            executorService.shutdown();
            try {

                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
              //
                }
        }
    }

    private boolean checkUrl(String nextUrl) {

        if(result.contains(nextUrl)) { return false; }
        if(nextUrl.startsWith("javascript:"))  { return false; }
        if(nextUrl.startsWith("#"))            { return false; }
        if(nextUrl.endsWith(".swf"))           { return false; }
        if(nextUrl.endsWith(".pdf"))           { return false; }
        if(nextUrl.endsWith(".png"))           { return false; }
        if(nextUrl.endsWith(".gif"))           { return false; }
        if(nextUrl.endsWith(".jpg"))           { return false; }
        if(nextUrl.endsWith(".jpeg"))          { return false; }

        return true;
    }

}
