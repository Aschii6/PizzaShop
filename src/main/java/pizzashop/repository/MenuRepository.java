package pizzashop.repository;

import pizzashop.model.MenuDataModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MenuRepository {
    private static String filename = "data/menu.txt";
    private List<MenuDataModel> listMenu;

    private void readMenu(){
//        ClassLoader classLoader = MenuRepository.class.getClassLoader();
        File file = new File(filename);
        this.listMenu= new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = null;
            while((line=br.readLine())!=null){
                MenuDataModel menuItem=getMenuItem(line);
                listMenu.add(menuItem);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private MenuDataModel getMenuItem(String line){
        MenuDataModel item=null;
        if (line==null|| line.equals("")) return null;
        StringTokenizer st=new StringTokenizer(line, ",");
        String name= st.nextToken();
        double price = Double.parseDouble(st.nextToken());
        item = new MenuDataModel(name, 0, price);
        return item;
    }*/

    private MenuDataModel getMenuItem(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        StringTokenizer st = new StringTokenizer(line, ",");
        if (st.countTokens() < 2) {
            System.out.println("Invalid menu line: " + line);
            return null;
        }

        String name = st.nextToken().trim();
        if (name.isEmpty()) {
            System.out.println("Invalid item name in menu line: " + line);
            return null;
        }

        try {
            double price = Double.parseDouble(st.nextToken().trim());
            return new MenuDataModel(name, 0, price);
        } catch (NumberFormatException e) {
            System.out.println("Invalid price format in menu line: " + line);
            return null;
        }
    }

    public List<MenuDataModel> getMenu(){
        readMenu();//create a new menu for each table, on request
        return listMenu;
    }
}
