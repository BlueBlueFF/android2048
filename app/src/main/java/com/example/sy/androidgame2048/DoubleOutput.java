package com.example.sy.androidgame2048;

/**
 * Created by Dem-Blue on 2017/10/13.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;



public class DoubleOutput {

    public static void Write(FileInputStream data,FileInputStream wav,String wavrename,int datasize,int wavsize,byte[] datasizebyte)
            throws IOException {
        FileOutputStream outputStream =new FileOutputStream(new File(wavrename));

        int bytesize=0;
        int datanum = 0;
        int sizenum =4;
        int temp=0;
        byte[] wavbyte = BinaryFile.read(wav);


        for(int size=0;size<wavsize;size++){

            temp = wavbyte[size];
            if(bytesize==0){
                bytesize=8;
                if(sizenum-->0){
                    datanum = datasizebyte[3-sizenum];
                }
                else {
                    datanum=data.read();
                }
            }
            if(size>60&&size%2==0){
                //做与运算然后右移
                int bit = datanum & 1;
                datanum= datanum>>1;
                bytesize--;
                switch (bit) {
                    //与1111 1110 做与运算 最后一位置零与
                    case 0:
                        temp = temp & 0xfe;
                        break;
                    case 1:
                        temp = temp | 1;
                        break;
                    default:
                        break;
                }
                wavbyte[size] =(byte) temp;
            }

        }
        outputStream.write(wavbyte,0,wavsize);
        outputStream.close();
    }

}
