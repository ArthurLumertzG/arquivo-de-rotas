package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Read All Lines of file
 * @param filePath
 *      The name of the file to read
 * @return
 *      All lines of file
 * @throws IOException
 *      Signals that an I/O exception of some sort has occurred.
 */

public class FileUtils {

    public static List<String>  readAllLines(String filePath) throws IOException {

        List<String> rows = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String r;
            while ((r = reader.readLine()) != null) {
                rows.add(r);
            }
        }

        return rows;
    }

    /**
     * Writes lines in the file
     * @param filePath
     *          The name of the file to write
     * @param rows
     *          Roes to be writing in the file
     * @throws IOException
     *          Signals thta o I/O of some sort has occurred
     */
    public static void writeLines(String filePath, List<String> rows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String r : rows) {
                writer.write(r);
                writer.newLine();
            }
        }
    }

    /**
     * Writes text on the file
     * @param filePath
     *          The name of the file to write
     * @param text
     *          The text to be writing in the file
     * @throws IOException
     *          Signals thta o I/O of some sort has occurred
     */
    public static void writeText(String filePath, String text, boolean isAppend) throws  IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, isAppend))) {
            writer.write(text);
            if (isAppend){
                writer.newLine();
            }
        }
    }


    /**
     * Remove the file of disk
     * @param filePath
     *          The name of the file to delete
     * @return
     *          TRUE if the file has been removed
     *                  or
     *          FALSE if not
     * @throws IOException
     *          Signals thta o I/O of dome dort has occurred
     */
    public static boolean deleteFile(String filePath) throws IOException {
        return Files.deleteIfExists(Path.of(filePath));
    }
}
