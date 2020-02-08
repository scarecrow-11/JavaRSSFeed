import java.io.*;
import java.net.URL;
import java.net.URLConnection;

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
