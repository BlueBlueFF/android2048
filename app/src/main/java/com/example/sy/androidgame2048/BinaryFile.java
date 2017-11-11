package com.example.sy.androidgame2048;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by Dem-Blue on 2017/10/13.
 */

public class BinaryFile {
    public static byte[] read(BufferedInputStream bf) throws IOException{

        try{
            byte[] data = new byte[bf.available()];
            bf.read(data);
            return data;
        }finally {
            bf.close();
        }
    }
    public static byte[] read(FileInputStream fileInputStream) throws IOException {
        return read(new BufferedInputStream(fileInputStream));
    }
}
