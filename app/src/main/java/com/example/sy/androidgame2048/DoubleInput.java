package com.example.sy.androidgame2048;

/**
 * Created by Dem-Blue on 2017/10/13.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DoubleInput {
    public static void read(FileInputStream wavinputstream,String filename,int wavsize) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(filename));

        int bytesize =0;
        int sizenum =4;
        int datasize = wavsize;
        int bit =0;
        boolean ischeck =true;
        byte[] datasizebyte = new byte[4];
        byte [] wavbyte = BinaryFile.read(wavinputstream);

        for(int size =0;size<wavsize;size++){
            int num = wavbyte[size];
            if(datasize ==0){
                break;
            }
            if (bytesize==8){
                if(!ischeck){
                    datasize--;
                    fileOutputStream.write(bit);

                }
                bytesize =0;
                if(sizenum-- > 0) {
                    datasizebyte[3-sizenum] = (byte) bit;
                }
                else {
                    if(ischeck){
                        datasize = DoubleExecute.bytesForToInt(datasizebyte);
                        datasize--;
                        fileOutputStream.write(bit);
                        ischeck =false;
                    }
                }
                bit=0;

            }

            if(size>60&& size%2==0){
                //由 1000 1101 到 1011 0001 再到 1000 1101
                bit =bit +( (num&1)<<bytesize);
                bytesize++;
            }

        }
        fileOutputStream.close();
    }


}

