package downUI.model;

import downUI.Downhandler;

import javafx.beans.property.*;

/**
 * Created by Administrator on 2017/1/4.
 */

public class ResourceForUI
{
    //该资源的id
    private StringProperty id;

    private StringProperty fileName;
    //下载内容的网址
    private StringProperty weburl;

    private StringProperty status;

    private IntegerProperty fileSize;

    private DoubleProperty progress;

    private IntegerProperty speed;
    //线程数目
    private IntegerProperty threadNum;
    //每个线程已下载字节,初值为0
    private IntegerProperty[] downBytes;

    private Downhandler down;

    public ResourceForUI(Resource r)
    {
        this.id = new SimpleStringProperty(r.getID());
        this.fileName = new SimpleStringProperty(r.getFileName());
        this.weburl = new SimpleStringProperty(r.getUrl());
        this.fileSize = new SimpleIntegerProperty(r.getFileSize());
        this.threadNum = new SimpleIntegerProperty(r.getThreadNum());
        this.status = new SimpleStringProperty("waiting ...");
        this.speed = new SimpleIntegerProperty(0);
        this.downBytes = new SimpleIntegerProperty[threadNum.getValue()];
        for(int i = 0; i < threadNum.getValue(); i ++)
        {
            this.downBytes[i] = new SimpleIntegerProperty(0);
        }
        this.progress = new SimpleDoubleProperty(0.00);

        this.down = new Downhandler(this);
    }

    public StringProperty fileNameProperty()
    {
        return this.fileName;
    }
    public StringProperty weburlProperty()
    {
        return this.weburl;
    }
    public StringProperty statusProperty()
    {
        return this.status;
    }
    public IntegerProperty fileSizeProperty()
    {
        return this.fileSize;
    }
    public IntegerProperty speedProperty()
    {
        return this.speed;
    }
    public DoubleProperty progressProperty()
    {
        return this.progress;
    }

    public void resumeDownload()
    {
        //System.out.println(this.fileName.getValue() + " resume download");
        //启动新线程
        new Thread( () ->
        {
            //启动下载
            down.resume();
            //监控下载进度
            int allDownBytes = 0, oldDownBytes = 0;
            while(true)
            {
                oldDownBytes = allDownBytes;
                allDownBytes = 0;
                for(IntegerProperty d : this.downBytes)
                {
                    allDownBytes += d.getValue();
                }

                this.setSpeed( (allDownBytes - oldDownBytes)/200 );
                //System.out.println(this.getSpeed());
                progress.set(((double)allDownBytes)/this.getFileSize());
                //System.out.println(progress.getValue());

                if(allDownBytes >= this.getFileSize())
                {
                    this.setStatus("finished");
                    return;
                }

                try {
                    Thread.sleep(200);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } ).start();

    }
    public void pauseDownload()
    {
        //System.out.println(this.fileName.getValue() + " pause download");
        this.down.pause();
    }
    public void stopDownload()
    {
        //System.out.println(this.fileName.getValue() + " stop download");
        this.down.stop();
    }

    //getters and setters
    public String getUrl()
    {
        return this.weburl.getValue();
    }
    public String getFileName()
    {
        return this.fileName.getValue();
    }
    public int getFileSize()
    {
        return this.fileSize.getValue();
    }
    public int getThreadNum()
    {
        return this.threadNum.getValue();
    }

    public String getStatus()
    {
        return this.status.getValue();
    }
    public void setStatus(String s)
    {
        this.status.setValue(s);
    }
    public StringProperty getStatusProperty()
    {
        return this.status;
    }

    public int getSpeed()
    {
        return this.speed.getValue();
    }
    public void setSpeed(int s)
    {
        this.speed.setValue(s);
    }

    //设置，访问已下载字节
    public void setDownBytes(int tid, int bytes)
    {
        this.downBytes[tid].set(bytes);
    }
    public int getDownBytes(int tid)
    {
        return this.downBytes[tid].getValue();
    }
}
