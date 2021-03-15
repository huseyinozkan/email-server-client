/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huseyinozkan
 */
public class ServerListenSMTP extends Thread {

    @Override
    public void run() {

        ServerSocket server;
        try {
            // server smtp portuna göre dinleme yapıyor
            server = new ServerSocket(ServerCommon.SMTP_PORT);
            Socket client = server.accept();

            while (true) {
                
                // veri alınıyor gonderilecek dosya tipi ne göre fonksiyonlar çalışıyor
                InputStreamReader in = new InputStreamReader(client.getInputStream());
                BufferedReader buf = new BufferedReader(in);
                String gelenVeri = buf.readLine();

                if (gelenVeri.equals("mail_gonder_mesaj")) {
                    MailGonderMesajDinle(client);
                } else if (gelenVeri.equals("mail_gonder_dosya")) {
                    MailGonderDosyaDinle(client, server);
                }

            }
        } catch (Exception ex) {
        }

        try { // server kapanırken mailler ve mail sayisi maillog dosyasınına yaziliyor
            Functions.WriteObjectInFile(ServerCommon.Mailler, "MailLog");
            Functions.WriteRecordCountInFile(ServerCommon.Mailler.size(), "MailSayisi.txt");
        } catch (IOException ex) {
            Logger.getLogger(ServerListenSMTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (MailLog Ma : ServerCommon.Mailler) {
            System.out.println(Ma.toString());
        }
    }

    private void MailGonderMesajDinle(Socket client) throws IOException {
        // gelecek mail mesaj tipinde ise bu fonksiyon çalışır
        String gelen;

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("220");
        out.flush();

        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        gelen = buf.readLine(); // helo geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("250");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // mail_from geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("250_sender_ok");
        out.flush();
        
        // mail gonderilecek kisi burada alınır
        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // rcpt_to: geldi
        ServerCommon.mailGonderilecekKisi = gelen.split(":")[1];

        out = new PrintWriter(client.getOutputStream());
        out.println("250_recipient_ok");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // DATA geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("354");
        out.flush();

        // mail basligi ve icerigi burada gelir. bilgileri alıp ServerCommon daki degiskenlere gonderiyoruz
        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // Mesaj Geldi

        if (gelen.split(":")[0].equals("mesaj")) {
            ServerCommon.GelenDosyaAdi = "";
            ServerCommon.GelenHedefBytesLenght = 0;
            ServerCommon.GelenTampon = null;
            ServerCommon.HedefBytes = null;
            ServerCommon.hedefBaslangic = 0;
            ServerCommon.GelenMailIcerik = gelen.split(":")[1];
            ServerCommon.GelenMailBaslik = gelen.split(":")[2];
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("250_message_accepted");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // QUIT: geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("221_closing_connection");
        out.flush();

        ////////Bitis islemleri
        ServerCommon.Mailler.add(new MailLog("gelen", "mesaj", ServerCommon.mailGonderilecekKisi.split("@")[0], ServerCommon.GelenMailBaslik, ServerCommon.GelenMailIcerik, ""));
        ServerCommon.Mailler.add(new MailLog("giden", "mesaj", ServerCommon.kullaniciAdi.split("@")[0], ServerCommon.GelenMailBaslik, ServerCommon.GelenMailIcerik, ""));
    }

    private void MailGonderDosyaDinle(Socket client, ServerSocket server) throws IOException, ClassNotFoundException {
        
        //gelecek mail tipi dosya ise tamponlama mantigiyla aliyoruz.
        
        String gelen;

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("220");
        out.flush();

        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        gelen = buf.readLine(); // helo geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("250");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // mail_from geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("250_sender_ok");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // rcpt_to: geldi
        ServerCommon.mailGonderilecekKisi = gelen.split(":")[1];

        out = new PrintWriter(client.getOutputStream());
        out.println("250_recipient_ok");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // DATA geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("354");
        out.flush();

        ////////////// 1 MSS lik Tampon alma işlemleri baslangic
        // tamponlama mantıgı clietdeki ile aynı 
        ObjectInputStream objectInput = new ObjectInputStream(client.getInputStream());//istemciye onay veriliyor.
        Packet ilkPacket = (Packet) objectInput.readObject();

        if (ilkPacket.getDurum().split("-")[0].equals("kontrol_basla")) {
            ServerCommon.GelenDosyaAdi = ilkPacket.getDosyaAdi();
            ServerCommon.GelenHedefBytesLenght = ilkPacket.getHedefBytesLength();
            ServerCommon.GelenTampon = ilkPacket.getTampon();
            ServerCommon.HedefBytes = new byte[ilkPacket.getHedefBytesLength()];
            ServerCommon.hedefBaslangic = 0;
            ServerCommon.GelenMailBaslik = ilkPacket.getDurum().split("-")[1];
        }

        while (true) {
            try {

                objectInput = new ObjectInputStream(client.getInputStream());
                Packet tamponPacket = (Packet) objectInput.readObject();

                if (tamponPacket.getDurum().equals("kontrol_bitir")) {
                    break;
                }

                Functions.TamponBirlestir(tamponPacket.Tampon);

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Shutting down");
        }

        ///////////////1 MSS lik Tampon alma işlemleri bitti
        out = new PrintWriter(client.getOutputStream());
        out.println("250_message_accepted");
        out.flush();

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine(); // QUIT: geldi

        out = new PrintWriter(client.getOutputStream());
        out.println("221_closing_connection");
        out.flush();

        ////////Bitis islemleri
        // dosya alma işlemleri bittiginde dosyayı gonderenenin altındaki giden kasörüne
        // ve dosyayı alanın altındaki gelen klasörüne kaydediyoruz. her iki bilgiyide
        // Mailler arraylistine kaydediyoruz.
        ServerCommon.FILEPATH = ServerCommon.CONST_FILEPATH + "/" + ServerCommon.mailGonderilecekKisi.split("@")[0] + "/gelen/" + ServerCommon.GelenDosyaAdi;
        ServerCommon.Mailler.add(new MailLog("gelen", "dosya", ServerCommon.mailGonderilecekKisi.split("@")[0], ServerCommon.GelenMailBaslik, "", ServerCommon.FILEPATH));
        ServerCommon.file = new File(ServerCommon.FILEPATH);
        Functions.ConvertBytesToFile(ServerCommon.HedefBytes);

        ServerCommon.FILEPATH = ServerCommon.CONST_FILEPATH + "/" + ServerCommon.kullaniciAdi.split("@")[0] + "/giden/" + ServerCommon.GelenDosyaAdi;
        ServerCommon.Mailler.add(new MailLog("giden", "dosya", ServerCommon.kullaniciAdi.split("@")[0], ServerCommon.GelenMailBaslik, "", ServerCommon.FILEPATH));
        ServerCommon.file = new File(ServerCommon.FILEPATH);
        Functions.ConvertBytesToFile(ServerCommon.HedefBytes);

    }

}
