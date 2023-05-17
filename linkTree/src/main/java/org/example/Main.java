package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<URL, Object> map = null;
        long start = System.currentTimeMillis();
        WebsiteScanner websiteScanner = new WebsiteScanner();
        map = websiteScanner.scanSite("https://lenta.ru/");
        long end = System.currentTimeMillis();
        long time = end - start;

        //сохранение map в файл
//        try (FileOutputStream fos = new FileOutputStream("map.ser");
//             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//            oos.writeObject(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //чтение map из файла
//        try (FileInputStream fis = new FileInputStream("map.ser");
//             ObjectInputStream ois = new ObjectInputStream(fis)) {
//            map = (Map<URL, Object>) ois.readObject();
//            MapToFile.writeMapToFile(map,"map.txt");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        System.out.println("время работы: " + time);
        System.out.println("итоговое количество страниц: " + WebsiteScanner.resultCount);
        System.out.println("отсеяно ссылок на внутренние элементы: " + WebsiteScanner.excludedInnerElement);
        System.out.println("отсеяно повторяющихся ссылок: " + WebsiteScanner.excludedUrlBased);
        MapToFile.writeMapToFile(map,"map.txt");
    }
}
