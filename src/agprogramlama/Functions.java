/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author huseyinozkan
 */
public class Functions {

    public static void DialogChooser() throws IOException {
        /*
        bu fonksiyon gonderilecek dosyayı secmemizi saglıyor.
        sectikden sonra dosyanın yolunu ve içerigini byte dizisine ceviriyor
        */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        File file = fileChooser.getSelectedFile();
        ClientCommon.Path = file.getAbsolutePath();
        ClientCommon.KaynakBytes = Files.readAllBytes(file.toPath());
        ClientCommon.TamponBytes = new byte[ClientCommon.TamponSize];
    }

    public static String DosyaAdiBul(String Path) {
        // gelen dosya yolundan dosyanın ismini seciyoruz
        String[] dizi = Path.split("/");
        return dizi[dizi.length - 1];
    }

    public static void TamponBirlestir(byte[] TamponBytes) {
        /*
        gelen tamponlar serverdaki hedef byte dizisinde birleştiriliyor
        */
        try {
            System.arraycopy(TamponBytes, 0, ServerCommon.HedefBytes, ServerCommon.hedefBaslangic, ServerCommon.TamponSize);
            ServerCommon.hedefBaslangic = ServerCommon.hedefBaslangic + ServerCommon.TamponSize;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.arraycopy(TamponBytes, 0, ServerCommon.HedefBytes, ServerCommon.hedefBaslangic, ServerCommon.HedefBytes.length % ServerCommon.TamponSize);
        }
    }

    static void TamponBirlestirForClient(byte[] TamponBytes) {
        /*
        gelen tamponlar client deki hedef byte dizisinde birleştiriliyor
        */
        try {
            System.arraycopy(TamponBytes, 0, ClientCommon.HedefBytes, ClientCommon.hedefBaslangic, ClientCommon.TamponSize);
            ClientCommon.hedefBaslangic = ClientCommon.hedefBaslangic + ClientCommon.TamponSize;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.arraycopy(TamponBytes, 0, ClientCommon.HedefBytes, ClientCommon.hedefBaslangic, ClientCommon.HedefBytes.length % ClientCommon.TamponSize);
        }
    }

    public static void ConvertBytesToFile(byte[] bytes) {
        /*
        server daki byte dizisini dosyaya ceviriyor
        */
        try {
            OutputStream os = new FileOutputStream(ServerCommon.file);
            os.write(bytes);
            System.out.println("Successfully byte inserted");
            os.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    static void ConvertBytesToFileForClient(byte[] bytes) {
        /*
        client deki byte dizisini dosyaya ceviriyor
        */
        try {
            OutputStream os = new FileOutputStream(ClientCommon.file);
            os.write(bytes);
            //System.out.println("Successfully byte inserted");
            os.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    static boolean KullaniciVarmi(String kullaniciAdi, String sifre) {
        /*
        kullanıcının client tarafında olup olmadıgını kontrol ediyor
        */
        try {
            for (Kullanici kulanici : ClientCommon.kullanicilar) {
                if (kulanici.getKullaniciAdi().equals(kullaniciAdi.split("@")[0]) && kulanici.getSifre().equals(sifre.split("@")[0]) && kullaniciAdi.split("@")[1].equals("ozkan.com")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void WriteObjectInFile(ArrayList<MailLog> array, String dosyaAdi) {
        /*
        gonderilen ve alınan mailler icin maillog ları dosyaya yazıyor
        */
        try {

            FileOutputStream fileOut = new FileOutputStream(dosyaAdi);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            for (MailLog array1 : array) {
                objectOut.writeObject(array1);
            }
            objectOut.close();
            System.out.println("The Object  was succesfully writte to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void ReadObjectInFile(ArrayList<MailLog> array, int kayitSayisi, String dosyaAdi) {
        /*
        maillog lar dosyadan okunuyor
        */
        try {
            FileInputStream fileIn = new FileInputStream(dosyaAdi);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            for (int i = 0; i < kayitSayisi; i++) {
                array.add((MailLog) objectIn.readObject());
            }
            objectIn.close();
            System.out.println("The Object  was succesfully read to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void WriteRecordCountInFile(int kayitSayisi, String dosyaAdi) throws IOException {
        /*
        maillog ların sayısı baska bir dosyaya yazılıyor
        */
        File file = new File(dosyaAdi);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file, false);
        BufferedWriter bWriter = new BufferedWriter(fileWriter);
        bWriter.write(String.valueOf(kayitSayisi));
        bWriter.close();
    }

    public static int ReadRecordCountInFile(String dosyaAdi) throws IOException {
        /*
        maillog ların sayısı okunuyor
        */
        File file = new File(dosyaAdi);
        FileReader fileReader = new FileReader(file);
        String line;

        BufferedReader br = new BufferedReader(fileReader);

        line = br.readLine();

        br.close();

        return Integer.parseInt(line);
    }

    public static String MailIcerikIlk3(MailLog Mail) {
        /*
        gelen stringin ilk 3 kelimesini okuyor
        */
        String sonuc = "";
        String[] array = Mail.getIcerik().split(" ");
        if (Mail.getType().equals("dosya")) {
            return DosyaAdiBul(Mail.getPath());
        } else {
            if (array.length < 3) {
                for (String array1 : array) {
                    sonuc += array1 + " ";
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    sonuc += array[i] + " ";
                }
            }
        }
        return sonuc + "...";
    }

    public static String FormatliYazdir(String gelen) {
        /*
        gelen mesaj icerigini yazdırıyor. 6 kelime snra alt satıra geciyor
        */
        String array[] = gelen.split(" ");
        String sonuc = "Sunucu: ";
        for (int i = 0; i < array.length; i++) {
            sonuc += array[i] + " ";
            if (i % 6 == 0 && i>1) {
                sonuc += "\nSunucu: ";
            }
        }
        return sonuc;
    }
}
