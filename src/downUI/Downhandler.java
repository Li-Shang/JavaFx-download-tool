package downUI;

import downUI.model.ResourceForUI;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.*;

public class Downhandler
{
    private Thread[] threads;
    private ResourceForUI downResource;

    private class DownThread extends Thread
    {
        private int tid;
        private int startPos;
        private int partSize;
        private RandomAccessFile curPart;
        //本线程已下载的字节数
        private int length;

        public DownThread(int tid, int pos, int size, RandomAccessFile curPart)
        {
            this.tid = tid;
            this.startPos = pos;
            this.partSize = size;
            this.curPart = curPart;
            this.length = downResource.getDownBytes(tid);
        }

        @Override
        public void run()
        {
            try
            {
                //建立http连接
                URL url = new URL(Downhandler.this.downResource.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream inStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                inStream.skip(startPos);

                int hasRead = 0;
                // 读取网络数据，并写入本地文件
                while (length < partSize
                        && (hasRead = inStream.read(buffer)) != -1)
                {
                    //System.out.println("now thread " + this.tid + " is running");
                    if(Downhandler.this.downResource.getStatus() != "downloading")
                    {
                        //线程终止，不再下载并存储已下载字节数
                        Downhandler.this.downResource.setDownBytes(tid, length);
                        System.out.println("now Resource thread " + this.tid + " has downloaded " +
                                Downhandler.this.downResource.getDownBytes(tid));
                        break;
                    }
                    curPart.write(buffer, 0, hasRead);
                    // 累计该线程下载的总大小
                    length += hasRead;
                    Downhandler.this.downResource.setDownBytes(tid, length);
                }
                curPart.close();
                inStream.close();
                conn.disconnect();
                //System.out.println("now thread " + this.tid + " ended");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public Downhandler(ResourceForUI r)
    {
        this.downResource = r;
        this.threads = new Thread[this.downResource.getThreadNum()];
    }

    public void resume()
    {
        //启动线程
        //System.out.println("Resume download!");
        try
        {
            int partSize = this.downResource.getFileSize() / this.downResource.getThreadNum() + 1;

            this.downResource.setStatus("downloading");

            for (int i = 0; i < downResource.getThreadNum(); i++)
            {
                // 计算每条线程的下载的开始位置
                int startPos = i * partSize + this.downResource.getDownBytes(i);
                // 每个线程使用一个RandomAccessFile进行下载
                RandomAccessFile curPart = new RandomAccessFile(downResource.getFileName(), "rw");
                // 定位该线程的下载位置
                curPart.seek(startPos);
                // 创建下载线程
                threads[i] = new DownThread(i, startPos, partSize, curPart);
                // 启动下载线程
                //threads[i].setPriority(Thread.MIN_PRIORITY);
                threads[i].start();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void pause()
    {
        this.downResource.setStatus("paused");
    }
    public void stop()
    {
        this.downResource.setStatus("stop");
    }
}
