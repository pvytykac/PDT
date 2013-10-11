package download;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader {

    /**
     * Zparsuje index stranku kde su zozipovane subory a stiahne vsetko co konci *.html.gz
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        List<String> files = new ArrayList<String>();
        String urlS = "http://s.cnl.sk/~genci/PDT/DATA/";
        Pattern p = Pattern.compile(".*<a href=\"(\\d+\\.html\\.gz)\">.*");

        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL(urlS);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher( line);
                if( m.matches()){
                    files.add( m.group( 1));
                }
            }
        }catch(Exception exc){
            exc.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        try{
        for( String s: files){
            String fileUrl = urlS + s;

            URL oracle = new URL(fileUrl);
            URLConnection con = oracle.openConnection();

            File f = new File(s);
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream( f);
            ReadableByteChannel rbc = Channels.newChannel(oracle.openStream());
            FileOutputStream fos = new FileOutputStream( s);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            System.out.println( s + " saved.");
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
