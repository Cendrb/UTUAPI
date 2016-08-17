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
            boolean res = loader.login("penis@gmail.com", "penis");
            loader.loadPredata();
            loader.load(1);
            loader.getEditor().requestCUTask(null, "XXXXXXXXXXXXXXXXXXXXXXXXXX", "api", DateUtil.CZ_DATE_FORMAT.parse("20. 08. 2016"), loader.getSubjects().get(0), loader.getGroupCategories().get(0).getSgroups().get(1),loader.getAdditionalInfosList());
            System.out.print("kana");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SclassDoesNotExistException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (DataLoader.SclassUnknownException e) {
            e.printStackTrace();
        } catch (DataLoader.AdminRequiredException e) {
            e.printStackTrace();
        }
    }
}
