package com.example.sy.androidgame2048;

/**
 * Created by Dem-Blue on 2017/10/13.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
public class DoubleExecute {
    public static void DataToWav(String datafilename,String wavfilenaem,String wavrefilename) throws IOException {
        File datafile = new File(datafilename);
        File wavfile = new File(wavfilenaem);
        FileInputStream datastream =null;
        FileInputStream wavstream=null;
        datastream = new FileInputStream(datafile);
        wavstream = new FileInputStream(wavfile);

        int datasize = datastream.available();
        int wavsize = wavstream.available();

        byte[] datasizebyte = new byte[4];
        datasizebyte = IntengerToBytes(datasize);//前四位隐藏文件大小

        DoubleOutput.Write(datastream,wavstream,wavrefilename,datasize,wavsize,datasizebyte);

        datastream.close();
        wavstream.close();

    }
    /**
     * 转换整形数据为byte[]
     *前4位隐藏文件大小
     * @param number
     * @return
     */
    public static byte[] IntengerToBytes(final int number) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((number >> (i * 8)) & 0xff);
        }
        return bytes;
    }
    public static void WavToData(String wavname,String dataname) throws IOException {
        File wavfile = new File(wavname);
        FileInputStream wavinputstream = new FileInputStream(wavfile);
        int wavsize = wavinputstream.available();
        DoubleInput.read(wavinputstream,dataname,wavsize);
    }

    public static int toInteger(final byte bit) {
        if (bit >= 0) {
            return (int) bit;
        } else {
            return (int) (bit + 256);
        }
    }

    public static int bytesForToInt(final byte[] bytes) {
        if (bytes.length != 4) {
            return 0;
        }
        int result = 0;
        try {
            result = toInteger(bytes[3]);
            result = (result << 8) + toInteger(bytes[2]);
            result = (result << 8) + toInteger(bytes[1]);
            result = (result << 8) + toInteger(bytes[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
