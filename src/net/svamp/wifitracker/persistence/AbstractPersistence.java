package net.svamp.wifitracker.persistence;

import java.io.*;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: mads
 * Date: 03.10.12
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPersistence implements Persistence {

    /**
     * Opens a writer to a file. Makes directories if the path does not exist.
     * @param path Path to file
     * @param file Filename
     * @return
     * @throws IOException
     */
    protected Writer getWriter(String path,String file) throws IOException {
        File f = new File(path,file);
        f.mkdirs();
        return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f)));
    }

    /**
     * Opens an InputStream given a path to a file
     * @param path Path file resides in
     * @param file Filename
     * @return Inputstream reading from file
     * @throws FileNotFoundException
     */
    protected InputStream getInputStream(String path, String file) throws FileNotFoundException {
        File f = new File(path,file);
        return new BufferedInputStream(new FileInputStream(f));
    }

    /**
     * Dumps the contents of an InputStream to a String object. Closes the stream afterwards.
     * @param stream InputStream to read from
     * @return String containing the entire InputStream until EOF is reached
     * @throws IOException If something went horribly wrong
     */
    protected String streamToString(InputStream stream) throws IOException {
        String s = new Scanner(stream).useDelimiter("\\A").next();
        stream.close();
        return s;
    }

    protected void writeStringToFile(String path,String data) throws IOException {
        File f = new File(path);
        f.mkdirs();
        writeStringToFile(new FileOutputStream(f),data);
    }

    protected void writeStringToFile(OutputStream stream,String data) throws IOException {
        Writer writer = new OutputStreamWriter(new BufferedOutputStream(stream));
        writer.write(data);
        writer.close();
    }
}

