package com.github.yangwk.jobscheduler.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtils {
    
    public static byte[] serialize(Object object) {
        if(object == null) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(bos);) {
            output.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("serialize error", e);
        }
    }
    
    public static <T> T deserialize(byte[] data) {
        if(data == null || data.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data); 
                ObjectInputStream input = new ObjectInputStream(bis);) {
            Object object = input.readObject();
            @SuppressWarnings("unchecked")
            T t = (T) object;
            return t;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("deserialize error", e);
        }
    }
    
    
}
