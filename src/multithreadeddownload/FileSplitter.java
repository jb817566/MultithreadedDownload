package multithreadeddownload;

import java.util.ArrayList;

public class FileSplitter {

    public static ArrayList<DownloadPart> splitInto(long byteSize, int pieces, String savePath, String url) {
        long pos = 0L;
        long pieceLen = byteSize / pieces;
        long lastPieceLeftoverPos = byteSize - (pieces * pieceLen);

        char ch = 'a';
        ArrayList<DownloadPart> list = new ArrayList<DownloadPart>();
        for (int i = 0; i < pieces; i++) {
//            System.out.println(pos + " to " + (pos + pieceLen));
            list.add(new DownloadPart(pos, pos + pieceLen - 1, savePath, url, String.valueOf(ch)));

            pos += pieceLen;
            ch++;
        }
        if (lastPieceLeftoverPos != 0) {
            list.add(new DownloadPart(pos, byteSize, savePath, url, String.valueOf(ch)));
//            System.out.println("Leftover: " + (pos + 1) + " - " + byteSize);
        }
        return list;
    }

}
