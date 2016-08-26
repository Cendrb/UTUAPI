package com.farast.utuapi;

import com.farast.utuapi.data.AdditionalInfo;
import com.farast.utuapi.data.DataLoader;
import com.farast.utuapi.util.DateUtil;
import com.farast.utuapi.util.SclassDoesNotExistException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        DataLoader loader = new DataLoader("http://localhost:3000");
        try {
            loader.loadPredata();
            boolean res = loader.login("penis@gmail.com", "penis");
            loader.load(1);
            System.out.print("kana");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SclassDoesNotExistException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
