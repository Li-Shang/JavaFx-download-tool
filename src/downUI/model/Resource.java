package downUI.model; /**
 * Created by Administrator on 2016/12/12.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class Resource implements Serializable
{
    //该资源的id
    private String id;
    //下载内容的网址
    private String weburl;
    private String fileName;
    private int fileSize = -1;
    //线程数目
    private int threadNum;
    //每个线程已下载字节,初值为0
    private int downBytes[];

    public Resource()
    {

    }
    public Resource(String url, String fileName, int threadNum)
    {
        this.id = UUID.randomUUID().toString();
        this.weburl = url;
        this.fileName = fileName;
        this.threadNum = threadNum;

        //初值为0
        this.downBytes = new int[threadNum];
        try
        {
            //建立http连接
            URL wurl = new URL(weburl);
            HttpURLConnection conn = (HttpURLConnection) wurl.openConnection();

            this.fileSize = conn.getContentLength();
            conn.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void readObject(String objFile)
    {
        try
        {
            FileInputStream fStream = new FileInputStream(objFile);
            ObjectInputStream objStream = new ObjectInputStream(fStream);
            Resource temp = (Resource) objStream.readObject();
            //复制一份temp给当前对象
            //深复制
            this.id = temp.id;
            this.weburl = temp.weburl;
            this.fileName = temp.fileName;
            this.fileSize = temp.fileSize;
            this.threadNum = temp.threadNum;
            //浅复制
            this.downBytes = temp.downBytes;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void writeObject()
    {
        String name = this.fileName + ".r";
        try
        {
            FileOutputStream fStream = new FileOutputStream(name);
            ObjectOutputStream objStream = new ObjectOutputStream(fStream);
            objStream.writeObject(this);

            objStream.flush();
            objStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return "Resource id: " + id + "\n"
                + "URL: " + weburl + "\n"
                + "File: " + fileName + "\n"
                + "Size: " + fileSize + "\n";

    }

    public String getID()
    {
        return this.id;
    }
    public String getUrl()
    {
        return this.weburl;
    }

    public String getFileName()
    {
        return this.fileName;
    }
    public int getThreadNum()
    {
        return this.threadNum;
    }

    //设置，读取本地文件大小
    public void setFileSize(int size)
    {
        this.fileSize = size;
    }
    public int getFileSize()
{
    return this.fileSize;
}

    //设置，访问已下载字节
    public void setDownBytes(int tid, int bytes)
    {
        this.downBytes[tid] = bytes;
    }
    public int getDownBytes(int tid)
    {
        return this.downBytes[tid];
    }
}
