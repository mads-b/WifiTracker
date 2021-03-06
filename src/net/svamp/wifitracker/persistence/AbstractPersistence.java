package net.svamp.wifitracker.persistence;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

/**
 * Abstract persistence class implementing some methods used
 * by both the external and the internal persistence writer.
 */
public abstract class AbstractPersistence implements Persistence {

    /**
     * Opens a writer to a file. Makes directories if the path does not exist.
     * @param path Path to file
     * @param file Filename
     * @return An implementation of Writer.
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
    InputStream getInputStream (String path, String file) throws FileNotFoundException {
        File f = new File(path,file);
        return new BufferedInputStream(new FileInputStream(f));
    }

    /**
     * Dumps the contents of an InputStream to a String object. Closes the stream afterwards.
     * @param stream InputStream to read from
     * @return String containing the entire InputStream until EOF is reached
     * @throws IOException If something went horribly wrong
     */
    String streamToString (InputStream stream) throws IOException {
        String s = new Scanner(stream).useDelimiter("\\A").next();
        stream.close();
        return s;
    }

    /**
     * Writes a string directly to a file.
     * @param path File path
     * @param data String to write to file
     * @throws IOException
     */
    void writeStringToFile (String path,String file, String data) throws IOException {
        File f = new File(path,file);
        File folder = new File(path);
        if(!folder.exists()) {
            Log.d("UNMADEFOLDER",path+" is not a directory.. Trying to make it!");
            if(!folder.mkdir()) {
                Log.e("MKDIRERR","Cannot make folder "+path+"! What to do?");
            }
        }
        if(!f.exists()) {
            Log.d("UNMADEFILE",file+" does not exist.. Trying to make it!");
            f.createNewFile();
        }
        writeStringToFile(new FileOutputStream(f), data);
    }

    /**
     * Writes a string directly to a file.
     * @param stream Open stream to file
     * @param data String to write to file
     * @throws IOException
     */
    void writeStringToFile (OutputStream stream, String data) throws IOException {
        Writer writer = new OutputStreamWriter(new BufferedOutputStream(stream));
        writer.write(data);
        writer.close();
    }

    public static Persistence getPersistence(Context context) {
        String storageOption = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("dataPointStorageOption", "external");
        if(storageOption.equals("external")) {
            return new ExternalPersistence(context);
        }
        else {
            return new InternalPersistence(context);
        }
    }
}

