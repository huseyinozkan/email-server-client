/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huseyinozkan
 */
public class ServerListenPOP3 extends Thread {

    @Override
    public void run() {

        ServerSocket server;
        try {
            // pop3 protokolu portunu girerek serversocket baslatıyorum ve dinlemeye basliyorum
            server = new ServerSocket(ServerCommon.POP3_PORT);
            Socket client = server.accept();
            
            // sonsuz dinleme basliyor
            while (true) {
                
                InputStreamReader in = new InputStreamReader(client.getInputStream());
                BufferedReader buf = new BufferedReader(in);
                String gelenVeri = buf.readLine();
                
                // servera gelen degere gore giriş, listeleme, okuma, silme ve cikis fonksiyonlarını cagiriyorum.
                if (gelenVeri.equals("login")) {
                    LoginDinle(client);
                } else if (gelenVeri.equals("list")) {
                    ListDinle(client);
                } else if (gelenVeri.equals("retr")) {
                    RetrDinle(client);
                } else if (gelenVeri.equals("dele")) {
                    DeleteDinle(client);
                } else if (gelenVeri.equals("quit")) {
                    QuitDinle(client);
                }

            }
        } catch (Exception ex) {
        }

        try { // server kapatılıyorsa Maillerimin loglarını ve sayısını obje şeklinde dosyaya yazdırıyorum
            Functions.WriteObjectInFile(ServerCommon.Mailler, "MailLog");
            Functions.WriteRecordCountInFile(ServerCommon.Mailler.size(), "MailSayisi.txt");
        } catch (IOException ex) {
            Logger.getLogger(ServerListenSMTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(ServerCommon.Mailler.toString());
    }

    private void LoginDinle(Socket client) {
        try {
            // login icin istemciye ok mesajı gonderiyorum
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println("ok");
            out.flush();
            
            // kullanici adi geliyor
            InputStreamReader in = new InputStreamReader(client.getInputStream());
            BufferedReader buf = new BufferedReader(in);
            ServerCommon.kullaniciAdi = buf.readLine();

            // kullanici adini aldigim icin ok mesaji gonderiyorum
            out = new PrintWriter(client.getOutputStream());
            out.println("ok");
            out.flush();

            // sifre geliyor
            in = new InputStreamReader(client.getInputStream());
            buf = new BufferedReader(in);
            ServerCommon.sifre = buf.readLine();

            // sifreyi aldigim icin ok mesaji gonderiyorum
            out = new PrintWriter(client.getOutputStream());
            out.println("ok");
            out.flush();

        } catch (IOException ex) {
        }
    }

    private void ListDinle(Socket client) throws IOException {
        /*
        sunucu mailleri listelemek istediginde Maillog daki bilgilerden 
        kullanici adini ve gelen mailleri ffiltreleyerek aralarına "-" koyup
        birlestirip istemciye gonderiyorum.
        */
        String gonderilecekListe = "";
        int i = 0;
        for (MailLog Mail : ServerCommon.Mailler) {
            if (Mail.getKullanici().equals(ServerCommon.kullaniciAdi.split("@")[0]) && Mail.getDurum().equals("gelen")) {
                i++;
                gonderilecekListe = gonderilecekListe + "Sunucu: " + i + " <" + Mail.getBaslik() + "> " + Functions.MailIcerikIlk3(Mail) + "-";
            }
        }
        gonderilecekListe = gonderilecekListe + "Sunucu: .";
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println(gonderilecekListe);
        out.flush();

    }

    private void RetrDinle(Socket client) throws IOException {
        
        // okunacak kayıt numarası geliyor. loglardan kaydı buluyoruz 
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        int OkunacakKayit = Integer.parseInt(buf.readLine()); //Okunacak kayit no geldi
        MailLog mailLog = null;

        int i = 0;
        for (MailLog Mail : ServerCommon.Mailler) {
            if (Mail.getKullanici().equals(ServerCommon.kullaniciAdi.split("@")[0]) && Mail.getDurum().equals("gelen")) {
                i++;
                if (i == OkunacakKayit) {
                    File file = new File(Mail.getPath());
                    mailLog = Mail;
                    break;
                }
            }
        }

        // okunacak kaydın türü mesajsa ön bilgi gonderip baslık ve icerigi gonderiyoruz
        if (mailLog.getType().equals("mesaj")) {
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println("type_mesaj"); // gidecek mail tipi gönderildi
            out.flush();

            String mesaj = mailLog.getBaslik() + "-" + mailLog.getIcerik();
            out = new PrintWriter(client.getOutputStream());
            out.println(mesaj);
            out.flush();

        } else {
            // gidecek mail dosya türünde ise tamponlama mantıgıyla gonderiyoruz.
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println("type_dosya");
            out.flush();

            ///////// 1 MSS lik Tampon Gönderme işlemleri başlangic
            // tamponlama mantıgı client de anlatılanla aynı
            File file = new File(mailLog.getPath());
            ServerCommon.KaynakBytes = Files.readAllBytes(file.toPath());
            ServerCommon.TamponBytes = new byte[ClientCommon.TamponSize];

            int baslangic = 0;
            int bitis = ServerCommon.TamponBytes.length;

            Packet ilkPacket = new Packet("kontrol_basla-" + mailLog.getBaslik(), null, ServerCommon.KaynakBytes.length, Functions.DosyaAdiBul(mailLog.getPath())); //server kontrol edilir ve ilk degerler gonderilir
            ObjectOutputStream objectOutput = new ObjectOutputStream(client.getOutputStream());
            objectOutput.writeObject(ilkPacket);

            while (true) {
                ServerCommon.TamponBytes = Arrays.copyOfRange(ServerCommon.KaynakBytes, baslangic, bitis);

                Packet packet = new Packet("data", ServerCommon.TamponBytes, 0, "dosyaAdi");
                objectOutput = new ObjectOutputStream(client.getOutputStream());
                objectOutput.writeObject(packet);

                if (bitis > ServerCommon.KaynakBytes.length) {

                    Packet sonPacket = new Packet("kontrol_bitir", null, 0, "dosyaAdi");
                    objectOutput = new ObjectOutputStream(client.getOutputStream());
                    objectOutput.writeObject(sonPacket);

                    break;
                }

                baslangic = bitis;
                bitis = bitis + ServerCommon.TamponBytes.length;
            }
            System.out.println(mailLog.getPath() + " Dosyası Gonderildi");

            //////// 1 MSS lik Tampon Gönderme işlemleri bitis
        }
    }

    private void DeleteDinle(Socket client) throws IOException {
        // silinecek kayıtno alınıyor
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        int silinecekKayit = Integer.parseInt(buf.readLine());

        // ilinecek kayıt noya sahip mail bulunuyor ve maillog dan siliniyor
        int i = 0;
        for (MailLog Mail : ServerCommon.Mailler) {
            if (Mail.getKullanici().equals(ServerCommon.kullaniciAdi.split("@")[0]) && Mail.getDurum().equals("gelen")) {
                i++;
                if (i == silinecekKayit) {
                    File file = new File(Mail.getPath());
                    file.delete();
                    ServerCommon.Mailler.remove(Mail);
                    break;
                }
            }
        }
    }

    private void QuitDinle(Socket client) throws IOException {
        // çıkış için onay bekliyor ve çıkıyor
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("ok"); // Çıkış Onaylandı
        out.flush();
    }

}
