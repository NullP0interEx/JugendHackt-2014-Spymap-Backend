package me.kobosil;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.*;

/**
 * User: Roman@NullP0interEx
 * Date: 13.09.2014
 * Time: 08:08
 * Root: me.kobosil@Spymap
 */
public class main {

    public static final Logger LOGGER = Logger.getLogger("Spymap");

    public static void main(String args[]) {
        MySQL sql = new MySQL();
        // http://download.geofabrik.de/
        if (!new File("config.prop").exists()) {
            writeToFile("host=xxx");
            writeToFile("port=xxx");
            writeToFile("database=xxx");
            writeToFile("user=xxx");
            writeToFile("password=xxx");
            writeToFile("xml_file=berlin-latest.osm");
            writeToFile("logFile=pinger.log");
            writeToFile("loggerLevel=INFO");
        }
        HashMap<String, String> conf = loadConfig();
        initLog(conf);
        try {
            File stocks = new File(conf.get("xml_file"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stocks);
            doc.getDocumentElement().normalize();

            System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
            NodeList nodes = doc.getElementsByTagName("stock");
            System.out.println("==========================");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Stock Symbol: " + getValue("symbol", element));
                    System.out.println("Stock Price: " + getValue("price", element));
                    System.out.println("Stock Quantity: " + getValue("quantity", element));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

    public static void initLog(HashMap<String, String> conf) {
        try {
            LOGGER.setUseParentHandlers(false);
            FileHandler fh = new FileHandler(conf.get("logFile"));
            ConsoleHandler ch = new ConsoleHandler();
            fh.setFormatter(new Formatter() {
                public String format(LogRecord record) {
                    return "[" + record.getLevel() + "]["
                            + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "] "
                            + record.getMessage() + "\n";
                }
            });
            ch.setFormatter(new Formatter() {
                public String format(LogRecord record) {
                    return "[" + record.getLevel() + "]["
                            + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "] "
                            + record.getMessage() + "\n";
                }
            });
            LOGGER.addHandler(ch);
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.parse(conf.get("loggerLevel")));
            LOGGER.info("Start Spymap...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> loadConfig() {
        HashMap<String, String> conf = new HashMap<String, String>();
        try {
            FileInputStream fstream = new FileInputStream(new File("config.prop"));
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                conf.put(line.split("=")[0], line.split("=")[1]);
            }
            in.close();
        } catch (Exception e) {
            LOGGER.severe("Config Error: " + e.getMessage());
        }
        return conf;
    }

    public static void writeToFile(String text) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("config.prop"), true));
            bw.write(text);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
        }
    }
}
