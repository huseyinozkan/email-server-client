/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author huseyinozkan
 */
public class ServerCommon {
    
    /*
    server tarafında kullanılan degiskenler tanımlandi
    */
    
    public static final int POP3_PORT = 1100;
    public static final int SMTP_PORT = 5870;
    
    
    public static String kullaniciAdi;
    public static String sifre;
    public static String mailGonderilecekKisi;
    public static String GelenDosyaAdi;
    public static int GelenHedefBytesLenght;
    public static byte[] GelenTampon;
    public static byte[] HedefBytes = null; // serverda ilk olarak size alınacak ve buraya eklenecek
    public static int hedefBaslangic = 0; //sıfırlanmalı
    public static int TamponSize = 1460; // sabit
    public static final String CONST_FILEPATH = "/home/huseyinozkan/Desktop/AgProgramlama/AgProgramlama_EpostaSunucuVeIstemcisi/Mailler";
    public static String FILEPATH = "/home/huseyinozkan/Desktop/AgProgramlama/AgProgramlama_EpostaSunucuVeIstemcisi/Mailler";
    public static File file;
    public static String GelenMailBaslik;
    public static String GelenMailIcerik;
    public static ArrayList<MailLog> Mailler = new ArrayList<>();
    public static int mailSayisi;
    
    /////// Cliente Giden Dosyalar için
    public static byte[] KaynakBytes = null;
    public static byte[] TamponBytes;
    public static String GelenKutusuPath = "/home/huseyinozkan/Desktop/AgProgramlama/AgProgramlama_EpostaSunucuVeIstemcisi/GelenKutusu";
    
    
    
}
