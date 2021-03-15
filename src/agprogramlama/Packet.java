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
public class Packet  implements Serializable{
    /*
    tamponlar bu class la gonderiliyor.
    */
    
    public String Durum;
    public byte[] Tampon;
    public int HedefBytesLength;
    public String DosyaAdi;

    public Packet(String Durum, byte[] Tampon, int HedefBytesLength, String DosyaAdi) {
        this.Durum = Durum;
        this.Tampon = Tampon;
        this.HedefBytesLength = HedefBytesLength;
        this.DosyaAdi = DosyaAdi;
    }

    public String getDurum() {
        return Durum;
    }

    public void setDurum(String Durum) {
        this.Durum = Durum;
    }

    public byte[] getTampon() {
        return Tampon;
    }

    public void setTampon(byte[] Tampon) {
        this.Tampon = Tampon;
    }

    public int getHedefBytesLength() {
        return HedefBytesLength;
    }

    public void setHedefBytesLength(int HedefBytesLength) {
        this.HedefBytesLength = HedefBytesLength;
    }

    public String getDosyaAdi() {
        return DosyaAdi;
    }

    public void setDosyaAdi(String DosyaAdi) {
        this.DosyaAdi = DosyaAdi;
    }
    
    
    
}
