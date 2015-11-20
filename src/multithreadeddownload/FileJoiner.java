package multithreadeddownload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class FileJoiner implements Enumeration {

    ArrayList<String> listOfFiles = null;
    int current = 0;

    public FileJoiner(ArrayList<String> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    @Override
    public boolean hasMoreElements() {
        if (current < listOfFiles.size()) {
            System.out.println(current);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object nextElement() {
        InputStream is = null;

        if (!hasMoreElements()) {
            throw new NoSuchElementException();
        } else {
            try {
                String nextElement = listOfFiles.get(current);
                System.out.println(nextElement);
                current++;
                is = new FileInputStream(nextElement);
            } catch (FileNotFoundException e) {
            }
        }
        return is;
    }

}
