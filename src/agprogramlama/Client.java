/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author huseyinozkan
 */
public class Client {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        
        // kullanicilari burada tanımlıyoruz
        ClientCommon.kullanicilar.add(new Kullanici("huseyin", "123"));
        ClientCommon.kullanicilar.add(new Kullanici("murat", "123"));
        ClientCommon.kullanicilar.add(new Kullanici("ahmet", "123"));
        ClientCommon.kullanicilar.add(new Kullanici("mehmet", "123"));
        ClientCommon.kullanicilar.add(new Kullanici("sait", "123"));
        ClientCommon.kullanicilar.add(new Kullanici("salim", "123"));

        // terminal ekranı başlatma
        TerminalEkrani();

    }

    private static void TerminalEkrani() throws IOException, ClassNotFoundException {
        // pop3 protokolü için olusturuldu
        Socket clientToPOP3 = new Socket("127.0.0.1", ServerCommon.POP3_PORT);
        // smtp protokolu için olusturuldu
        Socket clientToSMTP = new Socket("127.0.0.1", ServerCommon.SMTP_PORT);
        
        Scanner scanner = new Scanner(System.in);
        String kullaniciAdi;
        String sifre;
        String secim;

        do { // kullanici adi ve sifre aliniyor. Böyle bir kullanici yoksa tekrar aliniyor.
            System.out.println("OZKAN.COM E-POSTA UYGULAMASI");
            System.out.print("Kullanıcı Adı: ");
            kullaniciAdi = scanner.nextLine();
            System.out.print("Sifre: ");
            sifre = scanner.nextLine();
        } while (!Functions.KullaniciVarmi(kullaniciAdi, sifre));
        
        // kullanici adi ve sifre dogruysa pop3 protokolüne göre giris islemleri yapiliyor
        SunucuyaLoginYap(clientToPOP3, kullaniciAdi, sifre);

        while (true) { // kullanicinin yapabileceği islemler yazdirilip girdi isteniyor
            System.out.print("OZKAN.COM E-POSTA UYGULAMASI\n"
                    + "1. E-Posta Listele\n"
                    + "2. E-Posta Oku\n"
                    + "3. E-Posta Sil\n"
                    + "4. E-Posta Gonder\n"
                    + "5. Oturumu Kapat\n"
                    + "Secim Yap: ");
            secim = scanner.nextLine();

            switch (secim) {
                case "1": // pop3 protokolüne göre listeleme yapiliyor
                    Listele(clientToPOP3);
                    break;
                case "2": // pop3 protokolüne göre okuma yapiliyor
                    EPostaOku(clientToPOP3);
                    break;
                case "3": // pop3 protokolüne göre silme yapiliyor
                    EPostaSil(clientToPOP3);
                    break;
                case "4": // smtp protokolüne göre e posta gönderme islemi yapiliyor
                    EPostaGonder(clientToSMTP);
                    break;
                case "5": // pop3 protokolüne göre oturum kapatiliyor
                    OturumuKapat(clientToPOP3);
                    return;
                default:
                    System.out.println("\nHatalı Seçim !! \n");
            }
        }
    }

    private static void SunucuyaLoginYap(Socket client, String kullaniciAdi, String sifre) {
        try {
            
            // ilk olarak sunucuya login islemi yapacagini bildiriyor
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println("login");
            out.flush();

            // serverdan dinleme basladigi icin ok mesaji aldi
            InputStreamReader in = new InputStreamReader(client.getInputStream());
            BufferedReader buf = new BufferedReader(in);
            String gelen = buf.readLine();
            if (gelen.equals("ok")) //+OK POP3 server ready 
            {
                System.out.println("******************************");
                System.out.println("Sunucu: +OK POP3 server ready ");
            }

            // servera kullanici adi gonderiliyor.
            out = new PrintWriter(client.getOutputStream());
            out.println(kullaniciAdi);
            out.flush();
            System.out.println("Server: user " + kullaniciAdi.split("@")[0]);
            ClientCommon.kullaniciAdi = kullaniciAdi;

            //kullanici adi icin ok mesajı geldi
            in = new InputStreamReader(client.getInputStream());
            buf = new BufferedReader(in);
            gelen = buf.readLine();
            if (gelen.equals("ok")) //+OK
            {
                System.out.println("Sunucu: +OK");
            }

            //servera parola gonderiliyor
            out = new PrintWriter(client.getOutputStream());
            out.println(sifre);
            out.flush();
            System.out.println("Server: pass " + sifre);
            ClientCommon.sifre = sifre;
            
            //parola icin ok mesaji geldi
            in = new InputStreamReader(client.getInputStream());
            buf = new BufferedReader(in);
            gelen = buf.readLine();
            if (gelen.equals("ok")) //+OK
            {
                System.out.println("Sunucu: +OK");
                System.out.println("******************************");
            }

        } catch (IOException ex) {
        }
    }

    private static void Listele(Socket client) throws IOException {
        
        // listeleme yapmak icin servera list bilgisi gonderdik
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("list");
        out.flush();
        System.out.println("******************************");
        System.out.println("İstemci: list");
        
        // listelenecek bilgiler geldi.
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        String gelen = buf.readLine();// Sunucu: 1 <YOK duyurusu> 2020 yili için…
        String dizi[] = gelen.split("-"); // bilgiler tek string le geliyor. "-" ile ayırıp diziye atıyoruz
        for (String dizi1 : dizi) { // for döngüsü ile yazdırıyoruz.
            System.out.println(dizi1);
        }
        System.out.println("******************************");

    }

    private static void EPostaOku(Socket client) throws IOException, ClassNotFoundException {
        // eposta okumak icin servera bilgi gonderiyoruz.
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("retr");
        out.flush();

        // okunacak numarayı kullanıcıdan alıyoruz
        Scanner scanner = new Scanner(System.in);
        System.out.print("Okunacak No: ");
        int okunacakNo = Integer.parseInt(scanner.nextLine());

        // okunacak numarayı kullaniciya gonderiyoruz
        out = new PrintWriter(client.getOutputStream());
        out.println("" + okunacakNo);
        out.flush();
        System.out.println("\n******************************");
        System.out.println("İstemci: retr " + okunacakNo);

        // okunacak maille ilgili bilgi geliyor.
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        String gelen = buf.readLine();// Mesaj type geldi

        /*
        eger okunacak mail tipi mesaj ise normal bir sekilde okuyoruz.
        mail tipi dosya ise 1 MSS lik tamponlarla dosya geliyor. client de gelen
        kutusu altında kullanici ismiyle kaydediliyor
        */
        
        if (gelen.equals("type_mesaj")) { // gelen mailin tipi mesaj
            in = new InputStreamReader(client.getInputStream());
            buf = new BufferedReader(in);
            gelen = buf.readLine();// Mesaj Baslık Ve içeriği Geldi
            System.out.println("Sunucu: <" + gelen.split("-")[0] + ">");
            System.out.println(Functions.FormatliYazdir(gelen.split("-")[1]));
            System.out.println("Sunucu: .");
            System.out.println("******************************");

        } else if (gelen.equals("type_dosya")) { // gelen mailin tipi dosya
            ////////////// 1 MSS lik Tampon alma işlemleri baslangic
            //istemciye onay veriliyor.
            ObjectInputStream objectInput = new ObjectInputStream(client.getInputStream());
            Packet ilkPacket = (Packet) objectInput.readObject();

            // ilk olarak baslangic mesaji geliyor. bilgiler clientcommon a kaydediliyor.
            if (ilkPacket.getDurum().split("-")[0].equals("kontrol_basla")) {
                ClientCommon.GelenDosyaAdi = ilkPacket.getDosyaAdi();
                ClientCommon.GelenHedefBytesLenght = ilkPacket.getHedefBytesLength();
                ClientCommon.GelenTampon = ilkPacket.getTampon();
                ClientCommon.HedefBytes = new byte[ilkPacket.getHedefBytesLength()];
                ClientCommon.hedefBaslangic = 0;
                ClientCommon.GelenMailBaslik = ilkPacket.getDurum().split("-")[1];
            }

            while (true) {
                try {
                    // paketler object şeklinde alınıyor. objenin içinde tampon byte dizisi var.
                    objectInput = new ObjectInputStream(client.getInputStream());
                    Packet tamponPacket = (Packet) objectInput.readObject();

                    if (tamponPacket.getDurum().equals("kontrol_bitir")) {
                        // tamponlamanın bittiği anlamında mesaj geliyor ve döngüden çıkılıyor.
                        break;
                    }
                    
                    // her gelen tampon burada birleştiriliyor
                    Functions.TamponBirlestirForClient(tamponPacket.Tampon);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Shutting down");
            }

            ClientCommon.FILEPATH = ClientCommon.CONST_FILEPATH + ClientCommon.kullaniciAdi.split("@")[0] + "/" + ClientCommon.GelenDosyaAdi;
            ClientCommon.file = new File(ClientCommon.FILEPATH);
            // burada artık dosyanın tum tamponları alınmıştır. alınan byte ları dosyaya çeviriyoruz.
            Functions.ConvertBytesToFileForClient(ClientCommon.HedefBytes);
            // dosyanın yazıldığı klasörü açıyoruz.
            ProcessBuilder builder = new ProcessBuilder("sh", "-c", "nemo " + (ClientCommon.CONST_FILEPATH + "/" + ClientCommon.kullaniciAdi.split("@")[0]));
            builder.start();
            ///////////////1 MSS lik Tampon alma işlemleri bitti
            
            // gelen dosyayla ilgili bilgiler yazılıyor.
            System.out.println("Sunucu: <" + ClientCommon.GelenMailBaslik + ">");
            System.out.println("Sunucu: " + ClientCommon.GelenDosyaAdi + " Dosyasi Okundu.");
            System.out.println("Sunucu: " + ClientCommon.FILEPATH + " Klasörüne İndirildi.");
            System.out.println("Sunucu: .");
            System.out.println("******************************");

        }
    }

    private static void EPostaSil(Socket client) throws IOException {
        // silinecek numarayı kullanıcıdan alıyoruz.
        Scanner scanner = new Scanner(System.in);
        System.out.print("Silinecek No: ");
        int sayi = Integer.valueOf(scanner.nextLine());

        // sunucuya sil mesajını katarak silinecek numarayı gönderiyoruz
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("dele");
        out.flush();
        System.out.println("\n******************************");
        System.out.println("İstemci: dele " + sayi);
        System.out.println("\n******************************");

        out = new PrintWriter(client.getOutputStream());
        out.println("" + sayi); //Silinecek Sayı gonderildi
        out.flush();
    }

    private static void EPostaGonder(Socket client) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String secim;

        // e posta gonderilecek kisi kullanicidan aliniyor
        System.out.print("Mail Gönderilecek Kişi: ");
        ClientCommon.mailGonderilecekKisi = scanner.nextLine();
        System.out.println("");
        
        while (true) { // gönderilecek mailin tipi kullanıcıdan isteniyor
            System.out.print("1. Mesaj Gonder\n"
                    + "2. Dosya Gonder\n"
                    + "Secim Yap: ");
            secim = scanner.nextLine();

            if (secim.equals("1")) {
                // mesaj gonderilecekse bu fonksiyon çalışıyor
                MesajGonder(client);
                break;
            } else if (secim.equals("2")) {
                // dosya gonderilecekse bu fonksiyon çalışıyor
                DosyaGonder(client);
                break;
            } else {
                System.out.println("\nHatalı Seçim !! \n");
            }
        }
    }

    private static void MesajGonder(Socket client) throws IOException {
        // mesaj basligi ve icerigi alınıyor.
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Mail Baslik: ");
        String baslik = scanner.nextLine();
        System.out.println("\nMesajınızı Giriniz: ");
        String mesaj = scanner.nextLine();
        System.out.println("");

        // servera mesaj gönderilecek bilgisi gönderildi
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("mail_gonder_mesaj");
        out.flush();
        
        // server onay verdi
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        String gelen = buf.readLine();
        if (gelen.equals("220")) //220 soyismin.com
        {
            System.out.println("******************************");
            System.out.println("Sunucu: 220 " + ClientCommon.kullaniciAdi.split("@")[1]);
        }
        
        // haberleşme başladı
        out = new PrintWriter(client.getOutputStream());
        out.println("helo");
        out.flush();
        System.out.println("istemci: HELO " + ClientCommon.kullaniciAdi.split("@")[1]);

        // kullanıcı adı gönderildi
        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250")) //250 soyismin.com at your service 
        {
            System.out.println("Sunucu: 250 " + ClientCommon.kullaniciAdi.split("@")[1] + " at your service ");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("mail_from");
        out.flush();
        System.out.println("istemci: MAIL FROM <" + ClientCommon.kullaniciAdi + ">");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_sender_ok")) //250 adin@soyismin.com... Sender ok
        {
            System.out.println("Sunucu: 250 " + ClientCommon.kullaniciAdi + "... Sender ok");
        }

        
        // mail gonderilecek kisi gonderildi
        out = new PrintWriter(client.getOutputStream());
        out.println("rcpt_to:" + ClientCommon.mailGonderilecekKisi); //RCPT TO: <kuzenininAdi@soyismin.com> 
        out.flush();
        System.out.println("istemci: RCPT TO: <" + ClientCommon.mailGonderilecekKisi + ">");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_recipient_ok")) //250 kuzenininAdi@soyismin.com... Recipient ok 
        {
            System.out.println("Sunucu: 250 " + ClientCommon.mailGonderilecekKisi + "... Recipient ok");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("data"); //DATA
        out.flush();
        System.out.println("istemci: DATA");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("354")) //354 Enter mail, end with "." on a line by itself 
        {
            System.out.println("Sunucu: 354 Enter mail, end with \".\" on a line by itself ");
        }

        ///////// E Posta Mesaj Gönderme işlemleri başlangic
        out = new PrintWriter(client.getOutputStream());
        out.println("mesaj:" + mesaj + ":" + baslik); //DATA
        out.flush();
        System.out.println("İstemci: '" + mesaj + "' Mesajını Gonderildi");
        System.out.println("İstemci: .");

        //////// E Posta Mesaj Gönderme işlemleri bitis
        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_message_accepted")) //250 Message accepted for delivery 
        {
            System.out.println("Sunucu: 250 Message accepted for delivery ");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("QUIT");
        out.flush();
        System.out.println("istemci: QUIT");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("221_closing_connection")) //250 Message accepted for delivery 
        {
            System.out.println("Sunucu: 221 " + ClientCommon.kullaniciAdi.split("@")[1] + " closing connection");
            System.out.println("******************************");
        }
    }

    private static void DosyaGonder(Socket client) throws IOException {
        // dosya gonderme islemleride mesaj göndermeye benziyor. sadece tamponlama mantgı var
        Scanner scanner = new Scanner(System.in);
        System.out.print("Mail Baslik: ");
        String baslik = scanner.nextLine();
        System.out.println("");

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("mail_gonder_dosya");
        out.flush();

        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        String gelen = buf.readLine();
        if (gelen.equals("220")) //220 soyismin.com
        {
            System.out.println("******************************");
            System.out.println("Sunucu: 220 " + ClientCommon.kullaniciAdi.split("@")[1]);
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("helo");
        out.flush();
        System.out.println("istemci: HELO " + ClientCommon.kullaniciAdi.split("@")[1]);

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250")) //250 soyismin.com at your service 
        {
            System.out.println("Sunucu: 250 " + ClientCommon.kullaniciAdi.split("@")[1] + " at your service ");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("mail_from");
        out.flush();
        System.out.println("istemci: MAIL FROM <" + ClientCommon.kullaniciAdi + ">");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_sender_ok")) //250 adin@soyismin.com... Sender ok
        {
            System.out.println("Sunucu: 250 " + ClientCommon.kullaniciAdi + "... Sender ok");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("rcpt_to:" + ClientCommon.mailGonderilecekKisi); //RCPT TO: <kuzenininAdi@soyismin.com> 
        out.flush();
        System.out.println("istemci: RCPT TO: <" + ClientCommon.mailGonderilecekKisi + ">");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_recipient_ok")) //250 kuzenininAdi@soyismin.com... Recipient ok 
        {
            System.out.println("Sunucu: 250 " + ClientCommon.mailGonderilecekKisi + "... Recipient ok");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("data"); //DATA
        out.flush();
        System.out.println("istemci: DATA");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("354")) //354 Enter mail, end with "." on a line by itself 
        {
            System.out.println("Sunucu: 354 Enter mail, end with \".\" on a line by itself ");
        }

        ///////// 1 MSS lik Tampon Gönderme işlemleri başlangic
        // gönderilecek dosya seçiliyor ve kaynak byte dizisine yazılıyor
        Functions.DialogChooser();

        int baslangic = 0; // kaynak dizisinden alınacak tamponların baslangıcı ve bitisi
        int bitis = ClientCommon.TamponBytes.length;
        
        // gonderilecek tamponlarla ilgili baslangıc bilgisi gönderiliyor
        Packet ilkPacket = new Packet("kontrol_basla-" + baslik, null, ClientCommon.KaynakBytes.length, Functions.DosyaAdiBul(ClientCommon.Path)); //server kontrol edilir ve ilk degerler gonderilir
        ObjectOutputStream objectOutput = new ObjectOutputStream(client.getOutputStream());
        objectOutput.writeObject(ilkPacket);

        while (true) { // tamponlar sırayla gonderiliyor
            // kaynalk diziden tampon dizisine kopyalama işlemi
            ClientCommon.TamponBytes = Arrays.copyOfRange(ClientCommon.KaynakBytes, baslangic, bitis);
            
            // pakey objesi gönderiliyor
            Packet packet = new Packet("data", ClientCommon.TamponBytes, 0, "dosyaAdi");
            objectOutput = new ObjectOutputStream(client.getOutputStream());
            objectOutput.writeObject(packet);

            if (bitis > ClientCommon.KaynakBytes.length) {
                // eger kaynak dizisinin sonuna kadar gelindiyse bitir mesajı gonderiliyor ve donguden çıkılıyor
                Packet sonPacket = new Packet("kontrol_bitir", null, 0, "dosyaAdi");
                objectOutput = new ObjectOutputStream(client.getOutputStream());
                objectOutput.writeObject(sonPacket);

                break;
            }
            
            // her defasında bir sonraki tamponu alabilmek için baslangıc ve bitis degerleri değistiriliyor
            baslangic = bitis;
            bitis = bitis + ClientCommon.TamponBytes.length;
        }
        System.out.println("İstemci: " + ClientCommon.Path + " Dosyası Gonderildi");
        System.out.println("İstemci: .");

        //////// 1 MSS lik Tampon Gönderme işlemleri bitis
        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("250_message_accepted")) //250 Message accepted for delivery 
        {
            System.out.println("Sunucu: 250 Message accepted for delivery ");
        }

        out = new PrintWriter(client.getOutputStream());
        out.println("QUIT");
        out.flush();
        System.out.println("istemci: QUIT");

        in = new InputStreamReader(client.getInputStream());
        buf = new BufferedReader(in);
        gelen = buf.readLine();
        if (gelen.equals("221_closing_connection")) //250 Message accepted for delivery 
        {
            System.out.println("Sunucu: 221 " + ClientCommon.kullaniciAdi.split("@")[1] + " closing connection");
            System.out.println("******************************");
        }
    }

    private static void OturumuKapat(Socket client) throws IOException {
        // oturumu kapatma islemleri
        // servera cikma istegi gonderiliyor
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.println("quit");
        out.flush();
        System.out.println("******************************");
        System.out.println("İstemci: quit");

        // server onay veriyor
        InputStreamReader in = new InputStreamReader(client.getInputStream());
        BufferedReader buf = new BufferedReader(in);
        String gelen = buf.readLine();// Sunucu: +OK
        System.out.println("Sunucu: +OK");
        System.out.println("******************************");
    }

}
