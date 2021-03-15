/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import agprogramlama.Kullanici;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author huseyinozkan
 */
public class ClientCommon {
    
    // client tarafında kullanılan degiskenler
    public static String kullaniciAdi;
    public static String sifre;
    public static String mailGonderilecekKisi;
    public static String Path = null;
    public static int TamponSize = 1460;
    public static byte[] KaynakBytes = null;
    public static byte[] TamponBytes;
    public static ArrayList<Kullanici> kullanicilar = new ArrayList<>();
    
    //Serverdan gelen dosyalar için
    public static String GelenDosyaAdi;
    public static int GelenHedefBytesLenght;
    public static byte[] GelenTampon;
    public static byte[] HedefBytes;
    public static int hedefBaslangic;
    public static String GelenMailBaslik;
    public static String CONST_FILEPATH = "/home/huseyinozkan/Desktop/AgProgramlama/AgProgramlama_EpostaSunucuVeIstemcisi/GelenKutusu/";
    public static String FILEPATH = "/home/huseyinozkan/Desktop/AgProgramlama/AgProgramlama_EpostaSunucuVeIstemcisi/GelenKutusu/";
    public static File file;
    
}
