/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agprogramlama;

import java.io.Serializable;

/**
 *
 * @author huseyinozkan
 */
public class MailLog implements Serializable{
    /*
    maillog lama icin olusturulan class.
    */
    String Durum; //giden or gelen
    String Type;  //mesaj or dosya
    String Kullanici;
    String Baslik;
    String Icerik;
    String Path;

    public MailLog(String Durum, String Type, String Kullanici, String Baslik, String Icerik, String Path) {
        this.Durum = Durum;
        this.Type = Type;
        this.Kullanici = Kullanici;
        this.Baslik = Baslik;
        this.Icerik = Icerik;
        this.Path = Path;
    }

    public String getDurum() {
        return Durum;
    }

    public void setDurum(String Durum) {
        this.Durum = Durum;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getKullanici() {
        return Kullanici;
    }

    public void setKullanici(String Kullanici) {
        this.Kullanici = Kullanici;
    }

    public String getBaslik() {
        return Baslik;
    }

    public void setBaslik(String Baslik) {
        this.Baslik = Baslik;
    }

    public String getIcerik() {
        return Icerik;
    }

    public void setIcerik(String Icerik) {
        this.Icerik = Icerik;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    @Override
    public String toString() {
        return "MailLog{" + "Durum=" + Durum + ", Type=" + Type + ", Kullanici=" + Kullanici + ", Baslik=" + Baslik + ", Icerik=" + Icerik + ", Path=" + Path + '}';
    }
    
    
}
