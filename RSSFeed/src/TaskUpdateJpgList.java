import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

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
