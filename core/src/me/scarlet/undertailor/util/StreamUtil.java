package me.scarlet.undertailor.util;

import java.io.Closeable;
import java.io.IOException;

public class StreamUtil {
    
    public static void closeQuietly(Closeable... streams) {
        for(Closeable stream : streams) {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ignored) {}
            }
        }
    }
}
