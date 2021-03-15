/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.*;
import java.net.*;

/**
 *
 * @author huseyinozkan
 */
public class Server {

    public static String kullaniciAdi;
    public static String sifre;

    public static void main(String[] args) throws IOException {
        /*
        log lanmıs mailler okunuyor Mailler Arraylist ine yaziliyor.
        Daha sonra pop3 ve smtp aynı anda dinleme yapabilmesi için
        Thread ojeleri olusturulup dinleme baslatiliyor
        */

        ServerCommon.mailSayisi = Functions.ReadRecordCountInFile("MailSayisi.txt");
        Functions.ReadObjectInFile(ServerCommon.Mailler, ServerCommon.mailSayisi, "MailLog");
        int i = 0;
        for (MailLog Ma : ServerCommon.Mailler) {
            i++;
            System.out.println(i+ ": " +Ma.toString());
        }
        
        ServerListenPOP3 serverListenPOP3 = new ServerListenPOP3();
        ServerListenSMTP serverListenSMTP = new ServerListenSMTP();
        serverListenPOP3.start();
        serverListenSMTP.start();
    }

}
