package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;


import javax.xml.ws.Endpoint;

public class Main extends JFrame {
    int x;
    public final String windowTitle = "Integracja Systemów - Mikołaj Skrzypczyński";
    public final int width = 1366;
    public final int height = 768;
    public final Dimension windowDimension = new Dimension(width,height);
    public final String [] buttonsLabels =
            {
                    "wczytaj dane z TXT", //0
                    "wczytaj z XML", //1
                    "zapisz dane do TXT", //2
                    "zapisz dane do XML", //3
                    "Import z db", //4
                    "Export do db" //5
            };

    public final String [] columnHeaders =
            {
                    "Producent",
                    "wielkość matrycy",
                    "rozdzielczość",
                    "typ matrycy",
                    "czy dotykowy ekran",
                    "procesor",
                    "liczba rdzeni fizycznych",
                    "taktowanie",
                    "RAM",
                    "pojemność dysku",
                    "typ dysku",
                    "karta graficzna",
                    "pamięć karty graficznej",
                    "system operacyjny",
                    "napęd optyczny"
            };
    private final JPanel panelLabels;
    // table for JButtons

    private JButton[] btns;
    private JPanel panelBtns;
    private JLabel labelRowCount;
    private JLabel labelDuplicatesCount;
    private JScrollPane scrollPane;
    private JTable table;

    private DefaultTableModel tableModel;

    private JFileChooser fileChooser;

    private JPanel panelAddDeleteRows;
    private JButton btnAdd;
    private JButton btnDelete;

    private JPanel pnl;



        private HashMap<Integer,String> originalRows = new HashMap<>();
        private HashSet<Integer> changedRows = new HashSet<Integer>();
        private HashMap<String,Integer> keyCounts = new HashMap<>();

        private HashSet<Integer> duplicatedRows = new HashSet<Integer>();



    public static void main(String[] args) {

        Main app = new Main();
        Endpoint.publish("http://127.0.0.1:8888/laptops",
                new LaptopsBean());
        System.out.println("koniec funkcji main");
    }

    void loadRowsToHashMap(){
        keyCounts.clear();
        changedRows.clear();
       originalRows.clear();
       if(table.getRowCount() == 0) return;

       for(int i = 0; i < this.table.getRowCount();i++){
           String x = "";
           for(int j  = 0; j < this.columnHeaders.length; j++)
           {
               x += table.getValueAt(i,j) == null ? "" :  table.getValueAt(i,j).toString();
           }
           originalRows.put(i,x);

           if(keyCounts.get(x) == null)
               keyCounts.put(x,1);
           else
               keyCounts.replace(x,keyCounts.get(x)+1);
       }
   }

   void checkIfRowsChanged(){
       changedRows.clear();
        if(table.getRowCount() == 0) return;
       for(int i = 0; i < this.table.getRowCount();i++){
           String x = "";
           for(int j  = 0; j < this.columnHeaders.length; j++)
           {
               x += table.getValueAt(i,j) == null ? "" :  table.getValueAt(i,j).toString();

           }

           if(originalRows.get(i) != null && !originalRows.get(i).equals(x) ){
                System.out.println ( (!originalRows.get(i).equals(x)) + " X: " + x + " " + "org: " + originalRows.get(i));
                changedRows.add(i);
           }
       }
   }

    void checkIfDuplicates(){
        duplicatedRows.clear();
        int duplicateCount = 0;
        if(table.getRowCount() == 0) return;

        for(int i = 0; i < this.table.getRowCount();i++){
            String x = "";
            for(int j  = 0; j < this.columnHeaders.length; j++)
            {
                x += table.getValueAt(i,j) == null ? "" :  table.getValueAt(i,j).toString();
            }

            if(keyCounts.get(x) != null && keyCounts.get(x) > 1) {
                duplicatedRows.add(i);
                duplicateCount++;
            }

        }

        labelDuplicatesCount.setText("Znaleziono: " + duplicateCount + " duplikatów.");
    }





    Main(){
        //
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(windowTitle);
        //set window size
        this.setSize(windowDimension);
        //set layout manager to border layout
        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        fileChooser = new JFileChooser();

        panelBtns = new JPanel();
        panelBtns.setLayout(new FlowLayout());

        btns = new JButton[buttonsLabels.length];
        for(int i = 0; i < buttonsLabels.length;i++)
        {
            btns[i] = new JButton(buttonsLabels[i]);
            panelBtns.add(btns[i]);
        }

        pnl = new JPanel(new BorderLayout());
        pnl.add(panelBtns,BorderLayout.PAGE_START);
        labelRowCount = new JLabel("");
        this.labelRowCount.setText("Znaleziono: " + 0 + " rekordów");
        labelDuplicatesCount = new JLabel();
        this.labelDuplicatesCount.setText("Znaleziono: " + 0 + " duplikatów");

        panelLabels = new JPanel();
        panelLabels.setLayout(new FlowLayout());
        panelLabels.add(labelRowCount);
        panelLabels.add(labelDuplicatesCount);

        pnl.add(panelLabels,BorderLayout.PAGE_END);

        //add panel wit
        this.add (pnl,BorderLayout.PAGE_START);
        //  Object [][] test = new Object[1][columnHeaders.length];

        //tabledatamodel
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columnHeaders);
        tableModel.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {

                checkIfDuplicates();
                checkIfRowsChanged();

            }

        });

        //init table
        table = new JTable(){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                System.out.println("Changed rows: " + changedRows.toString() + "Duplicated rows: " + duplicatedRows.toString());


                    if      (duplicatedRows.contains(row)) comp.setBackground(Color.RED);
                    else if (changedRows.contains(row)) comp.setBackground(Color.LIGHT_GRAY);
                    else comp.setBackground(null);





//                if(wasModified.get(row) != null && wasModified.get(row)) {
//                    comp.setBackground(Color.LIGHT_GRAY);
//                }else if(colorByRow.get(row) != null) {
//                    comp.setBackground(colorByRow.get(row));
//                }else {
//                    comp.setBackground(Color.LIGHT_GRAY);
//                }

                return comp;
            }
        };

        table.setModel(tableModel);
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        //btn actions
        btns[0].addActionListener(actionEvent -> loadFromTxt());
        btns[1].addActionListener(actionEvent -> loadFromXml());
        btns[2].addActionListener(actionEvent -> saveToTxt());
        btns[3].addActionListener(actionEvent -> saveToXml());
        btns[4].addActionListener(actionEvent -> importFromDb());
        btns[5].addActionListener(actionEvent -> exportToDb());

        //
        this.add(scrollPane,BorderLayout.CENTER);

        btnAdd = new JButton("Dodaj wiersz");
        btnDelete = new JButton("Usun zaznaczone wiersze");

        btnAdd.addActionListener(actionEvent -> addRowToTable());
        btnDelete.addActionListener(actionEvent -> deleteSelectedRowFromTable());


        panelAddDeleteRows = new JPanel();
        panelAddDeleteRows.setLayout(new FlowLayout());
        panelAddDeleteRows.add(btnAdd);
        panelAddDeleteRows.add(btnDelete);

        this.add(panelAddDeleteRows,BorderLayout.PAGE_END);
        //show window


        this.setVisible(true);


    }

    private void importFromDb() {
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test","root","");
            Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("select count(*) from laptopy");
            rs.next();
            int rowCount = rs.getInt(1);
            rs=stmt.executeQuery("SELECT " +
                    "`Producent`, " +
                    "`wielkosc_matrycy`, " +
                    "`rozdzielczosc`, " +
                    "`typ_matrycy`, " +
                    "`czy_dotykowy_ekran`, " +
                    "`procesor`, " +
                    "`liczba_rdzeni_fizycznych`, " +
                    "`taktowanie`, " +
                    "`RAM`, " +
                    "`pojemnosc_dysku`, " +
                    "`typ_dysku`, " +
                    "`karta_graficzna`, " +
                    "`pamiec_karty_graficznej`, " +
                    "`system_operacyjny`, " +
                    "`naped_optyczny` FROM `laptopy` ");

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columns = resultSetMetaData.getColumnCount();
            Object [][] data = new Object[rowCount][columnHeaders.length];
            int i = 0;
            tableModel.setRowCount(0);
            originalRows.clear();

            while(rs.next()) {
                for (int j = 0; j < columns; j++) {
                    data[i][j] = rs.getString(j + 1);
                }
                i++;
            }
            tableModel.setDataVector(data,columnHeaders);
           // table.setModel(tableModel);
            loadRowsToHashMap();
            checkIfDuplicates();

            this.labelRowCount.setText("Znaleziono: " + table.getRowCount() + " rekordów");
            con.close();
        }catch(Exception e){ System.out.println(e);};

    }


    private void exportToDb() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test","root","");
            Statement stmt=con.createStatement();
            stmt.executeUpdate("delete from laptopy;");
            if (table.getRowCount() > 0)
            {

                for(int i = 0; i < table.getRowCount(); i++)
                {
                    String insertQuery = "insert into laptopy values (";
                    String x = "";
                    for(int j = 0; j < columnHeaders.length;j++){
                        String val = table.getValueAt(i,j) == null ? "" : table.getValueAt(i,j).toString();
                        x += "'" + val + "'";
                        if (j != columnHeaders.length-1)  x += ",";
                    }
                    insertQuery += x + " );";
                    System.out.println(insertQuery);
                    stmt.executeUpdate(insertQuery);
                }

            }

            con.close();
        }catch(Exception e){ System.out.println(e);};

    }




    void addRowToTable(){
        Object[] object = new Object[columnHeaders.length];
        this.tableModel.addRow(object);
    }

    void deleteSelectedRowFromTable(){
        int [] selectedRows = table.getSelectedRows();
        if(selectedRows.length > 0)
        {
            for(int i = selectedRows.length-1; i>=0; i--)
            {
                tableModel.removeRow(selectedRows[i]);
            }
        }
    }




    void loadFromTxt(){
        //fileChooser.setCurrentDirectory();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION){
            //file is selected
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try{

                tableModel.setRowCount(0);
                Scanner s = new Scanner(selectedFile);
                while(s.hasNextLine()){
                    String line = s.nextLine();
                    String [] lineSplitted = line.split(";");
                    tableModel.addRow(lineSplitted);
                }
                this.labelRowCount.setText("Znaleziono: " + table.getRowCount() + " rekordów");
                loadRowsToHashMap();
                checkIfDuplicates();
                s.close();
            }
            catch (FileNotFoundException ex){
                System.out.println("File not found");
            }

        }
    }

    void loadFromXml(){
        //fileChooser.setCurrentDirectory();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION){
            //file is selected
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try{
                tableModel.setRowCount(0);
                originalRows.clear();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                try
                {
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document doc = documentBuilder.parse(selectedFile);
                    doc.getDocumentElement().normalize();
                    System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
                    NodeList nList = doc.getElementsByTagName("laptop");

                    System.out.println(nList.toString());

                    for(int i = 0; i < nList.getLength(); i++){
                        Vector<String> strList = new Vector<String>();
                        Node nNode = nList.item(i);
                        NodeList tempNodeList;

                        if(nNode.getNodeType() == Node.ELEMENT_NODE){
                            Element e =  (Element) nNode;

                            tempNodeList = e.getElementsByTagName("manufacturer");
                            if(tempNodeList.getLength() > 0 )
                                strList.add(e.getElementsByTagName("manufacturer").item(0).getTextContent());
                            else
                                strList.add("");

                            tempNodeList = e.getElementsByTagName("screen");
                            if(tempNodeList.getLength() > 0)
                            {
                                Node screen = tempNodeList.item(0);
                                if (screen.getNodeType() == Node.ELEMENT_NODE) {
                                    Element screen_element = (Element) screen;
                                    if (screen_element.getElementsByTagName("size").getLength() > 0)
                                        strList.add(screen_element.getElementsByTagName("size").item(0).getTextContent());
                                    else
                                        strList.add("");
                                    if (screen_element.getElementsByTagName("resolution").getLength() > 0)
                                        strList.add(screen_element.getElementsByTagName("resolution").item(0).getTextContent());
                                    else
                                        strList.add("");
                                    if (screen_element.getElementsByTagName("type").getLength() > 0)
                                        strList.add(screen_element.getElementsByTagName("type").item(0).getTextContent());
                                    else
                                        strList.add("");
                                    if (screen_element.getElementsByTagName("touchscreen").getLength() > 0)
                                        strList.add(screen_element.getElementsByTagName("touchscreen").item(0).getTextContent());
                                    else
                                        strList.add("");
                                }
                            }
                            else
                                for(int i1 = 0; i1 < 4; i1++) strList.add("");

                            tempNodeList =     e.getElementsByTagName("processor");
                            if (tempNodeList.getLength() > 0){
                                Node processor = tempNodeList.item(0);
                                if (processor.getNodeType() == Node.ELEMENT_NODE) {
                                    Element processor_element = (Element) processor;
                                    if (processor_element.getElementsByTagName("name").getLength() > 0)
                                        strList.add(processor_element.getElementsByTagName("name").item(0).getTextContent());
                                    else strList.add("");

                                    if (processor_element.getElementsByTagName("physical_cores").getLength() > 0)
                                        strList.add(processor_element.getElementsByTagName("physical_cores").item(0).getTextContent());
                                    else strList.add("");

                                    if (processor_element.getElementsByTagName("clock_speed").getLength() > 0)
                                        strList.add(processor_element.getElementsByTagName("clock_speed").item(0).getTextContent());
                                    else strList.add("");

                                }
                                else
                                    for(int i1 = 0; i1 < 3; i1++) strList.add("");
                            }

                            tempNodeList = e.getElementsByTagName("ram");
                            if(tempNodeList.getLength() > 0)
                                strList.add(tempNodeList.item(0).getTextContent());
                            else
                                strList.add("");

                            tempNodeList = e.getElementsByTagName("disc");
                            if( tempNodeList.getLength() > 0)
                            {
                                Node disk = e.getElementsByTagName("disc").item(0);
                                if (disk.getNodeType() == Node.ELEMENT_NODE) {
                                    Element disk_element = (Element) disk;

                                    if (disk_element.getElementsByTagName("storage").getLength() > 0)
                                        strList.add(disk_element.getElementsByTagName("storage").item(0).getTextContent());
                                    else strList.add("");
                                    if (disk_element.getElementsByTagName("type").getLength() > 0)
                                        strList.add(disk_element.getElementsByTagName("type").item(0).getTextContent());
                                    else strList.add("");
                                }
                            }

                            tempNodeList = e.getElementsByTagName("graphic_card");
                            if( tempNodeList.getLength() > 0) {
                                Node graphic_card = e.getElementsByTagName("graphic_card").item(0);
                                if (graphic_card.getNodeType() == Node.ELEMENT_NODE) {
                                    Element graphic_card_element = (Element) graphic_card;
                                    if (graphic_card_element.getElementsByTagName("name").getLength() > 0)
                                        strList.add(graphic_card_element.getElementsByTagName("name").item(0).getTextContent());
                                    else strList.add("");
                                    if (graphic_card_element.getElementsByTagName("memory").getLength() > 0)
                                        strList.add(graphic_card_element.getElementsByTagName("memory").item(0).getTextContent());
                                    else strList.add("");
                                }
                            }

                            if (e.getElementsByTagName("os").getLength() > 0)
                                strList.add(e.getElementsByTagName("os").item(0).getTextContent());
                            else
                                strList.add("");
                            if (e.getElementsByTagName("disc_reader").getLength() > 0)
                                strList.add(e.getElementsByTagName("disc_reader").item(0).getTextContent());
                            else
                                strList.add("");

                            tableModel.addRow(strList);
                        }

                    }


                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    System.out.println("Error");
                }
                this.labelRowCount.setText("Znaleziono: " + table.getRowCount() + " rekordów");
                loadRowsToHashMap();
                checkIfDuplicates();
            }
            catch (Exception ex){
                System.out.println("File not found");
            }
        }
    }

    void saveToXml(){
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION){
            //file is selected
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try{
                FileWriter fileWriter = new FileWriter(selectedFile.getAbsolutePath());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                StringBuilder stringBuilder = new StringBuilder();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.newDocument();

                Element rootElement  = doc.createElement("laptops");
                doc.appendChild(rootElement);

                int rowCount = tableModel.getRowCount();
                int colCount = tableModel.getColumnCount();

                for(int i = 0; i < rowCount; i++)
                {
                    Element laptop = doc.createElement("laptop");
                    String str = "";
                    Element manufacturer = doc.createElement("manufacturer");
                    if(table.getValueAt(i,0) != null )
                        str  = table.getValueAt(i,0).toString();
                    else str = "";
                    manufacturer.setTextContent(str);
                    laptop.appendChild(manufacturer);

                    Element screen = doc.createElement("screen");
                    Element screen_size = doc.createElement("size");
                    if(table.getValueAt(i,1) != null )
                        str  = table.getValueAt(i,1).toString();
                    else str = "";
                    screen_size.setTextContent(str);
                    screen.appendChild(screen_size);

                    Element screen_resolution = doc.createElement("resolution");
                    if(table.getValueAt(i,2) != null )
                        str  = table.getValueAt(i,2).toString();
                    else str = "";
                    screen_resolution.setTextContent(str);
                    screen.appendChild(screen_resolution);

                    Element screen_type = doc.createElement("type");
                    if(table.getValueAt(i,3) != null )
                        str  = table.getValueAt(i,3).toString();
                    else str = "";
                    screen_type.setTextContent(str);
                    screen.appendChild(screen_type);

                    Element screen_touchscreen = doc.createElement("touchscreen");
                    if(table.getValueAt(i,4) != null )
                        str  = table.getValueAt(i,4).toString();
                    else str = "";
                    screen_touchscreen.setTextContent(str);
                    screen.appendChild(screen_touchscreen);
                    laptop.appendChild(screen);

                    Element processor = doc.createElement("processor");
                    Element processor_name = doc.createElement("name");
                    if(table.getValueAt(i,5) != null )
                        str  = table.getValueAt(i,5).toString();
                    else str = "";
                    processor_name.setTextContent(str);
                    processor.appendChild(processor_name);


                    Element processor_physical_cores = doc.createElement("physical_cores");
                    if(table.getValueAt(i,6) != null )
                        str  = table.getValueAt(i,6).toString();
                    else str = "";
                    processor_physical_cores.setTextContent(str);
                    processor.appendChild(processor_physical_cores);

                    Element processor_clock_speed = doc.createElement("clock_speed");
                    if(table.getValueAt(i,7) != null )
                        str  = table.getValueAt(i,7).toString();
                    else str = "";
                    processor_clock_speed.setTextContent(str);
                    processor.appendChild(processor_clock_speed);
                    laptop.appendChild(processor);

                    Element ram = doc.createElement("ram");
                    if(table.getValueAt(i,8) != null )
                        str  = table.getValueAt(i,8).toString();
                    else str = "";
                    ram.setTextContent(str);
                    laptop.appendChild(ram);

                    Element disc = doc.createElement("disc");

                    Element disc_storage = doc.createElement("storage");
                    if(table.getValueAt(i,9) != null )
                        str  = table.getValueAt(i,9).toString();
                    else str = "";
                    disc_storage.setTextContent(str);
                    disc.appendChild(disc_storage);

                    Element disc_type = doc.createElement("type");
                    if(table.getValueAt(i,10) != null )
                        str  = table.getValueAt(i,10).toString();
                    else str = "";
                    disc_type.setTextContent(str);
                    disc.appendChild(disc_type);
                    laptop.appendChild(disc);

                    Element graphic_card = doc.createElement("graphic_card");
                    Element graphic_card_name = doc.createElement("name");
                    if(table.getValueAt(i,11) != null )
                        str  = table.getValueAt(i,11).toString();
                    else str = "";
                    graphic_card_name.setTextContent(str);
                    graphic_card.appendChild(graphic_card_name);

                    Element graphic_card_memory = doc.createElement("memory");
                    if(table.getValueAt(i,12) != null )
                        str  = table.getValueAt(i,12).toString();
                    else str = "";
                    graphic_card_memory.setTextContent(str);
                    graphic_card.appendChild(graphic_card_memory);
                    laptop.appendChild(graphic_card);

                    Element os = doc.createElement("os");
                    if(table.getValueAt(i,13) != null )
                        str  = table.getValueAt(i,13).toString();
                    else str = "";
                    os.setTextContent(str);
                    laptop.appendChild(os);

                    Element disc_reader = doc.createElement("disc_reader");
                    if(table.getValueAt(i,14) != null )
                        str  = table.getValueAt(i,14).toString();
                    else str = "";
                    disc_reader.setTextContent(str);
                    laptop.appendChild(disc_reader);

                    rootElement.appendChild(laptop);
                }


                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);
                FileWriter writer = new FileWriter(selectedFile);
                Result streamResult = new StreamResult(writer);

                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(domSource,streamResult);



            }
            catch (Exception ex){
                System.out.println("error:" + ex.getMessage());
            }

        }
    }

    void saveToTxt(){
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION){
            //file is selected
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            try{
                FileWriter fileWriter = new FileWriter(selectedFile.getAbsolutePath());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                StringBuilder stringBuilder = new StringBuilder();

                int rowCount = tableModel.getRowCount();
                int colCount = tableModel.getColumnCount();

                for(int i = 0; i < rowCount; i++)
                {
                    stringBuilder.setLength(0);
                    for(int j = 0; j < colCount; j++){
                        // System.out.println(table.getValueAt(i,j).toString());
                        String str = "";
                        if ( table.getValueAt(i,j) == null)
                        {
                            str = "";
                        }
                        else{
                            str = table.getValueAt(i,j).toString();
                        }
                        stringBuilder.append(str);
                        stringBuilder.append(';');
                    }

                    bufferedWriter.write(stringBuilder.toString());
                    bufferedWriter.newLine();

                }
                bufferedWriter.close();

            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }

        }
    }
}