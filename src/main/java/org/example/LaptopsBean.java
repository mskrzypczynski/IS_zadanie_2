package org.example;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)

public class LaptopsBean implements LaptopsInterface {

    static List<String> dbColumns = Arrays.asList(
            "producent",
            "wielkosc_matrycy",
            "rozdzielczosc",
            "typ_matrycy",
            "czy_dotykowy_ekran",
            "procesor",
            "liczba_rdzeni_fizycznych",
            "taktowanie",
            "ram",
            "pojemnosc_dysku",
            "typ_dysku",
            "karta_graficzna",
            "pamiec_karty_graficznej",
            "system_operacyjny",
            "naped_optyczny");



@WebMethod
@Override
public int getManufacturerLaptopNumber(String manufacturer) {
    int count = 0;
    try{

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test","root","");
        Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        String query = "select count(*) from laptopy where producent like \'%s\';".formatted(manufacturer);
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        count = rs.getInt(1);
        System.out.println(count);



        con.close();
    }catch(Exception e){ System.out.println(e);};


    return count;

}

@WebMethod
@Override
    public int getResolutionLaptopNumber(String resolution) {
    int count = 0;
    try{

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test","root","");
        Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        String query = "select count(*) from laptopy where wielkosc_matrycy like \'%s\';".formatted(resolution);
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);

        rs.next();
        count = rs.getInt(1);



        con.close();
    }catch(Exception e){ System.out.println(e);};


        return count;
    }

    @WebMethod
    @Override
    public Laptop[] getLaptopListByFeatures(String f1, String f2, String f3, String f4, String f5) {
        ArrayList<Laptop> laptops = new ArrayList<>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");
            Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT " +
                    "`producent`, " +
                    "`wielkosc_matrycy`, " +
                    "`rozdzielczosc`, " +
                    "`typ_matrycy`, " +
                    "`czy_dotykowy_ekran`, " +
                    "`procesor`, " +
                    "`liczba_rdzeni_fizycznych`, " +
                    "`taktowanie`, " +
                    "`ram`, " +
                    "`pojemnosc_dysku`, " +
                    "`typ_dysku`, " +
                    "`karta_graficzna`, " +
                    "`pamiec_karty_graficznej`, " +
                    "`system_operacyjny`, " +
                    "`naped_optyczny` FROM `laptopy` " +
                    " where 1=1 ";

            if (f1 != "") query += " and liczba_rdzeni_fizycznych like \"%s\"".formatted(f1);
            if (f2 != "") query += " and czy_dotykowy_ekran like \"%s\"".formatted(f2);
            if (f3 != "") query += " and ram like \"%s\"".formatted(f3);
            if (f4 != "") query += " and procesor like \"%s\"".formatted(f4);
            if (f5 != "") query += " and karta_graficzna like \"%s\"".formatted(f5);

            query += ";";
            System.out.println(query);


            ResultSet rs=stmt.executeQuery(query);

            while(rs.next()) {
                Laptop laptop = new Laptop();
                for(String columnName : dbColumns) {
                    String value = (rs.getString(columnName));
                    switch (columnName) {
                        case "producent":
                            laptop.setManufacturer(value);
                            break;
                        case "wielkosc_matrycy":
                            laptop.setScreenSize(value);
                            break;
                        case "rozdzielczosc":
                            laptop.setResolution(value);
                            break;
                        case "typ_matrycy":
                            laptop.setScreenType(value);
                            break;
                        case "czy_dotykowy_ekran":
                            laptop.setScreenTouchscreen(value);
                            break;
                        case "procesor":
                            laptop.setProcessorName(value);
                            break;
                        case "liczba_rdzeni_fizycznych":
                            try{
                                laptop.setProcessorPhysicalCores(Integer.valueOf(value));
                            }catch (Exception e) {

                            }
                            break;
                        case "taktowanie":
                            laptop.setProcessorSpeed(value);
                            break;
                        case "ram":
                            laptop.setRam(value);
                            break;
                        case "pojemnosc_dysku":
                            laptop.setDiscStorage(value);
                            break;
                        case "typ_dysku":
                            laptop.setDiscType(value);
                            break;
                        case "karta_graficzna":
                            laptop.setGraphicCardName(value);
                            break;
                        case "pamiec_karty_graficznej":
                            laptop.setGraphicCardMemory(value);
                            break;
                        case "system_operacyjny":
                            laptop.setOs(value);
                            break;
                        case "naped_optyczny":
                            laptop.setDiscReader(value);
                            break;
                    }
                }
                laptops.add(laptop);
            }



            con.close();
        }catch(Exception e){ System.out.println(e);};
        System.out.println(laptops);
        Laptop[] arr = new Laptop[laptops.size()];
        int i = 0;
        for(Laptop l : laptops){
            arr[i] = l;
            i++;
        }
        return arr;
    };


@WebMethod
    @Override
    public Laptop[] getAllLaptops()
    {
        ArrayList<Laptop> laptops = new ArrayList<>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");
            Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs=stmt.executeQuery("SELECT " +
                    "`producent`, " +
                    "`wielkosc_matrycy`, " +
                    "`rozdzielczosc`, " +
                    "`typ_matrycy`, " +
                    "`czy_dotykowy_ekran`, " +
                    "`procesor`, " +
                    "`liczba_rdzeni_fizycznych`, " +
                    "`taktowanie`, " +
                    "`ram`, " +
                    "`pojemnosc_dysku`, " +
                    "`typ_dysku`, " +
                    "`karta_graficzna`, " +
                    "`pamiec_karty_graficznej`, " +
                    "`system_operacyjny`, " +
                    "`naped_optyczny` FROM `laptopy` ");

            while(rs.next()) {
                Laptop laptop = new Laptop();
                for(String columnName : dbColumns) {
                    String value = (rs.getString(columnName));
                    switch (columnName) {
                        case "producent":
                            laptop.setManufacturer(value);
                            break;
                        case "wielkosc_matrycy":
                            laptop.setScreenSize(value);
                            break;
                        case "rozdzielczosc":
                            laptop.setResolution(value);
                            break;
                        case "typ_matrycy":
                            laptop.setScreenType(value);
                            break;
                        case "czy_dotykowy_ekran":
                            laptop.setScreenTouchscreen(value);
                            break;
                        case "procesor":
                            laptop.setProcessorName(value);
                            break;
                        case "liczba_rdzeni_fizycznych":
                            try{
                                laptop.setProcessorPhysicalCores(Integer.valueOf(value));
                            }catch (Exception e) {

                            }
                            break;
                        case "taktowanie":
                            laptop.setProcessorSpeed(value);
                            break;
                        case "ram":
                            laptop.setRam(value);
                            break;
                        case "pojemnosc_dysku":
                            laptop.setDiscStorage(value);
                            break;
                        case "typ_dysku":
                            laptop.setDiscType(value);
                            break;
                        case "karta_graficzna":
                            laptop.setGraphicCardName(value);
                            break;
                        case "pamiec_karty_graficznej":
                            laptop.setGraphicCardMemory(value);
                            break;
                        case "system_operacyjny":
                            laptop.setOs(value);
                            break;
                        case "naped_optyczny":
                            laptop.setDiscReader(value);
                            break;
                    }
                }
               laptops.add(laptop);
            }



            con.close();
        }catch(Exception e){ System.out.println(e);};
        System.out.println(laptops);
        Laptop[] arr = new Laptop[laptops.size()];
        int i = 0;
        for(Laptop l : laptops){
            arr[i] = l;
            i++;
        }
        return arr;
    };

}
