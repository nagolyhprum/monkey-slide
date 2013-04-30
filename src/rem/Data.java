package rem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Data {

    private static void write(String file, String data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
        }
    }

    private static String read(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String s = "";
            int data;
            while ((data = fis.read()) != -1) {
                s += ((char) data);
            }
            fis.close();
            return s;
        } catch (IOException e) {
            return "";
        }
    }

    public static void setHighscore(int highscore) {
        if (getHighscore() < highscore) {
            write("highscore.txt", "" + highscore);
        }
    }

    public static int getHighscore() {
        try {
            return Integer.parseInt(read("highscore.txt"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void addToTotalCoins(int totalCoins) {
        write("coins.txt", "" + (totalCoins + getTotalCoins()));
    }

    public static int getTotalCoins() {
        try {
            return Integer.parseInt(read("coins.txt"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
