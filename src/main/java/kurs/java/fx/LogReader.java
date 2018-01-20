package kurs.java.fx;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;


public class LogReader 
{
	private static boolean canBreak = false;

    public static void startReading(String filename) throws InterruptedException, IOException {
        canBreak = false;
        String line;
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
            while (!canBreak)
            {
                line = lnr.readLine();
                if (line == null) {
                    //System.out.println("czekam 3 sekundy");
                    Thread.sleep(3000);
                    continue;
                }
                processLine(line);
            }
            lnr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void stopReading() {
        canBreak = true;
    }

    private static void processLine(String s) {
        //processing line
    	 System.out.println(s);
    }
}
