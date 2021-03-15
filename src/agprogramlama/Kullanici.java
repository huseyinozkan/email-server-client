/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

/**
 *
 * @author huseyinozkan
 */
public class Kullanici {
    /*
    kullanıcı lar icin olusturulmus class
    */
    String KullaniciAdi;
    String Sifre;

    public Kullanici(String KullaniciAdi, String Sifre) {
        this.KullaniciAdi = KullaniciAdi;
        this.Sifre = Sifre;
    }

    public String getKullaniciAdi() {
        return KullaniciAdi;
    }

    public void setKullaniciAdi(String KullaniciAdi) {
        this.KullaniciAdi = KullaniciAdi;
    }

    public String getSifre() {
        return Sifre;
    }

    public void setSifre(String Sifre) {
        this.Sifre = Sifre;
    }
    
    
}
