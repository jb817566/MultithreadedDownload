package multithreadeddownload;

import queue.QManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import queue.Q;

public class MultithreadedDownload {

    private static Map<String, String> optionalDownloadMap = new HashMap<String, String>();
    private static String u = null;

    /*
     args:
     0 - download url   
     1 - true=use file ext from url, false = use ext from 4
     2 - number of parts
     3 - if(args[1]==false), then use this title(the resulting file name).
     4 - if(args[1]==false), then use this file extension
    
     NOTE: Downloads default to Public folder
     */
    public static void main(String[] args) {

        QManager manager = new QManager();

        if (args.length == 0) {
            System.out.println("Invalid number of arguments. Please input a url followed by the number of pieces.");
            return;
        } else {
            u = args[0].replace("\"", "");
        }

        String loc = "";
        if (Boolean.parseBoolean(args[1])) {
            loc = "/tmp/" + u.split("/")[u.split("/").length - 1];
        } else {
            loc = "/tmp/" + args[3] + "." + args[4];
        }
        System.out.println("Downloading to: " + loc);
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(u).openConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long contLength = urlConnection.getContentLength();

        ArrayList<DownloadPart> parts = FileSplitter.splitInto(contLength, Integer.parseInt(args[2]), loc, u);
        ArrayList<Object> objects = new ArrayList<Object>();
        objects.addAll(parts);

        Q pool = manager.getPool(Integer.parseInt(args[2]), objects);

        pool.start(false);
        pool.waitFor();
        ArrayList<String> f = new ArrayList<String>();
        for (DownloadPart d : parts) {
            f.add(d.getPath());
        }

        joinFiles(f, loc);
    }

    private static void joinFiles(ArrayList<String> list, String path) {
        
        File ofile = new File(sanitizePath(path));
        if (ofile.isDirectory()) {
            ofile.delete();
        }
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;
        try {
            fos = new FileOutputStream(ofile, true);
            for (String filePath : list) {
                File file = new File(sanitizePath(filePath));
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        cleanup(list);
    }

    private static void cleanup(List<String> cleanThis) {
        for (String str : cleanThis) {
            new File(sanitizePath(str)).delete();
        }
    }
    
    public static String sanitizePath(String toClean){
        File folder = new File(toClean).getParentFile();
        String fileName = new File(toClean).getName();
        return folder.toString() + "\\" + fileName.replaceAll("[:\\\\/*\"?|<>]", "_");
    }

}
