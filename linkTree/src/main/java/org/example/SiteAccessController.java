package org.example;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class SiteAccessController {
    private final Semaphore semaphore;
    long last;

    public SiteAccessController() {
        last = System.currentTimeMillis();
        this.semaphore = new Semaphore(1, true);
    }

    public String accessSite(URL page) throws InterruptedException, IOException {
        semaphore.acquire();
        String content;
        try {
            last = System.currentTimeMillis();
            content = new Scanner(page.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            Thread.sleep(120);
        } finally {
            semaphore.release();
        }
        return content;
    }
}