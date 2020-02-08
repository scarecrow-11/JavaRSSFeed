//Main Class Imports
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//TaskFetchRSS Class Imports
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

//TaskUpdateJpgList Class Imports
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

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
        File file = new File("a.rss");

        //Task Fetch
        TaskFetchRSS taskFetchRSS = new TaskFetchRSS(rssUrl, file);
        executorService.scheduleAtFixedRate(taskFetchRSS, 0, 15, TimeUnit.SECONDS);

        //Task Update
        TaskUpdateJpgList taskUpdateJpgList = new TaskUpdateJpgList(file);
        System.out.println(executorService.isTerminating());
        executorService.scheduleAtFixedRate(taskUpdateJpgList, 20, 20, TimeUnit.SECONDS);

    }
}

//RSS Fetcher Task
public class TaskFetchRSS implements Runnable{
    URL url;
    File file;

    public TaskFetchRSS(URL url, File file) {
        this.url = url;
        this.file = file;
    }

    @Override
    public void run() {
        synchronized (this.getClass()) {
            try {
                String contents = readRSSFeed();
                writeRSSFeedToFile(contents);
                System.out.println("Fetching Finished!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String readRSSFeed(){
        BufferedReader reader = null;
        try {
            URLConnection con = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            //Read line by line
            String line = reader.readLine();
            StringBuilder strBuilder = new StringBuilder();
            do {
                strBuilder.append(line+"\n");
            } while ((line = reader.readLine()) != null);

            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public void writeRSSFeedToFile(String contents){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(contents);  //Update File Contents
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//Updater Task
public class TaskUpdateJpgList implements Runnable{
    File file;

    public TaskUpdateJpgList(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        synchronized (this.getClass()) {
            try {
                Document doc = readFromRssFile();
                ArrayList<String> imgList = getImgReferences(doc);
                writeToFile(imgList);
                System.out.println("Update Finished!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Document readFromRssFile(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getImgReferences(Document doc){
        NodeList nodeList = doc.getElementsByTagName("media:content");
        ArrayList<String> imgList= new ArrayList<String>();
        for(int i = 0; i < nodeList.getLength(); ++i){
            String s = nodeList.item(i).getAttributes().getNamedItem("url").getNodeValue();
            imgList.add(s);
        }
        return imgList;
    }

    public void writeToFile(ArrayList<String> imgList){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : imgList){
            stringBuilder.append(s+"\n");
        }
        //Write To File
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(stringBuilder.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}