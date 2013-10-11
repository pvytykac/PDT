package download;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {


    /**
     * Nacita vsetky rozbalene htmlka v zadanom priecinku a zparsuje ich do sql suborov
     * Zatial je to len testovacia verzia, lebo nemame finalnu schemu urcenu
     * @param args
     * @throws Exception
     */
    public static void main( String[] args) throws Exception{
        File folder = new File("D:/pdt_data/decompressed/");
        File[] files = folder.listFiles();
        File outF = new File("D:/pdt_data/output/output.sql");
        if( !outF.exists()){
            outF.createNewFile();
        }

        int count = 0;
        BufferedReader in = null;
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( outF), "windows-1250"));
        for( File f : files){
            in = new BufferedReader( new InputStreamReader( new FileInputStream( f), "windows-1250"));
            String buffer = getContent(in);
            List< Entry> entries = generateSQL( buffer);
            for( Entry e: entries){
                out.append( e.toString());
            }

            in.close();
            ++count;

            if( count % 50 == 0){
                outF = new File("D:/pdt_data/output/output" + count / 50 + ".sql");
                if( !outF.exists()){
                    outF.createNewFile();
                }
                out.flush();
                out.close();
                out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( outF), "windows-1250"));
            }
        }

        out.flush();
        out.close();
    }

    public static String getContent( BufferedReader in) throws Exception{

        StringBuffer buffer = new StringBuffer();
        String line;
        while( (line = in.readLine()) != null){
            buffer.append( line + "\n");
        }

        return buffer.toString();
    }

    public static List< Entry> generateSQL( String input){
        List<Entry> entries = new ArrayList< Entry>();
        StringBuffer output = new StringBuffer();
        String regexp = "f\\[\'(\\S+_\\d+)\']=\'(\\d*)\';cas\\[\'\\S+_\\d+\']=\"(\\d{2}:\\d{2})\";casdo\\[\'\\S+_\\d+\']=\"(\\d{2}:\\d{2})\";n\\[\'\\S+_\\d+\']=\"(.+)\";t\\[\'\\S+_\\d+\']=\"(.?)\";(?:u\\[\'\\S+_\\d+\']=\"(\\S+)\")?";
        Pattern pattern = Pattern.compile( regexp);
        Matcher m = pattern.matcher( input);
        while( m.find()){
                Entry e = new Entry();
                e.stanica = m.group( 1);
                e.f = Integer.valueOf( m.group( 2));
                e.casOd = m.group( 3);
                e.casDo = m.group( 4);
                e.nazov = m.group( 5);
                e.t = m.group( 6).equals( "1") ? true : false;
                e.url = m.group( 7);

                entries.add( e);
        }

        return entries;
    }

    public static class Entry{

        public String stanica;
        public Integer f;
        public Boolean t;
        public String url;
        public String casOd;
        public String casDo;
        public String nazov;


        @Override
        public String toString(){
            try{
            return "INSERT INTO entry VALUES(NULL, " +
                    formatString(stanica) + ", " +
                    formatString(casOd) + ", " +
                    formatString(casDo) + ", " +
                    formatString(nazov) + ", " +
                    f + ", " +
                    t + ", " +
                    formatString(url) + ");\n";
            }catch(Exception e){
                e.printStackTrace();
                return "";
            }
        }

        private String formatString(String value) throws Exception{
            if( value == null){
                return "NULL";
            }
            value = value.replace("'", "\\\'");
            return "'" + value + "'";
        }
    }

}
