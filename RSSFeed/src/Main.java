import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ///Thread Executor Service
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(2);

        URL rssUrl = null;
        try {
            rssUrl = new URL("http://rss.cnn.com/rss/edition.rss");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        File file = new File("D:\\a.rss");  //A copy included in project

        //Task Fetch
        TaskFetchRSS taskFetchRSS = new TaskFetchRSS(rssUrl, file);
        executorService.scheduleAtFixedRate(taskFetchRSS, 0, 15, TimeUnit.SECONDS);

        //Task Update
        TaskUpdateJpgList taskUpdateJpgList = new TaskUpdateJpgList(file);
        executorService.scheduleAtFixedRate(taskUpdateJpgList, 0, 20, TimeUnit.SECONDS);

    }
}
