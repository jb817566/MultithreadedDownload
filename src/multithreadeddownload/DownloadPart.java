package multithreadeddownload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadPart implements Comparable {

    private String path = "";
    private String u = "";
    private String piece = "";
    private long b;
    private long e;

    public DownloadPart(long begin, long end, String p, String url, String pieceNum) {
        path = p;
        u = url;
        b = begin;
        e = end;
        piece = pieceNum;
    }

    public String getSuffix(){
        return piece;
    }
    
    public String getPath(){
        return path + "." + piece;
    }
    
    public long getBegin() {
        return b;
    }

    public long getEnd() {
        return e;
    }

    @Override
    public int compareTo(Object o) {
        DownloadPart obj = (DownloadPart) o;
        if (obj.getBegin() < this.getBegin()) {
            return -1;
        } else if (obj.getBegin() > this.getBegin()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {

        URL url = null;
        try {
            url = new URL(u);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        urlConnection.setRequestProperty("Range", "bytes=" + b + "-" + e);
        try {
            urlConnection.connect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            inputStream = urlConnection.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String saveFilePath = path + "." + piece;
        new File(path).mkdirs();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(MultithreadedDownload.sanitizePath(saveFilePath));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        int bytesRead = -1;
        byte[] buffer = new byte[4096];
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

}
