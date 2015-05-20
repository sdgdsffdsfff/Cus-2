package com.suning.cus.utils;

import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * Created by 15010551 on 2015/3/19.
 */
public class MyUtils {

    /**
     * * 对象转数组
     * * @param obj
     * * @return
     * */
     public static byte[] toByteArray (Object obj) {
         byte[] bytes = null;
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         try {
             ObjectOutputStream oos = new ObjectOutputStream(bos);
             oos.writeObject(obj);
             oos.flush();
             bytes = bos.toByteArray ();
             oos.close();
             bos.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         }
         return bytes;
     }

    /**
     * * 数组转对象
     * * @param bytes
     * * @return
     * */
     public static Object toObject (byte[] bytes) {
         Object obj = null;
         try {
             ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
             ObjectInputStream ois = new ObjectInputStream(bis);
             obj = ois.readObject();
             ois.close();
             bis.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         } catch (ClassNotFoundException ex) {
             ex.printStackTrace();
         }
         return obj;
     }


    /**
     * 随机产生32位数字  - added by 14110105
     * @return UUID字符串
     */
    public static String getUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 小数的String转整数
     */
    public static String Double2Int(String doubleNum) {
        return ((int)Double.parseDouble(doubleNum)) + "";
    }

    public static int String2Int(String intNum) {
        if (TextUtils.isEmpty(intNum)) {
            return 0;
        }
        return Integer.parseInt(intNum);
    }

    public static long String2Long(String longNum) {
        if (TextUtils.isEmpty(longNum)) {
          return 0;
        }
        return Long.parseLong(longNum);
    }
}
