package com.farast.utuapi;

import com.farast.utuapi.data.DataLoader;
import com.farast.utuapi.util.SclassDoesNotExistException;
import org.xml.sax.SAXException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        DataLoader loader = new DataLoader("http://localhost:3000");
        try {
            boolean res = loader.login("penis@gmail.com", "penis");
            loader.loadPredata();
            loader.load(1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SclassDoesNotExistException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        System.out.print("kana");
    }
}
