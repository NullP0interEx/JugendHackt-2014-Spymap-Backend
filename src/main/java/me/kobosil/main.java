package me.kobosil;


import com.scireum.open.xml.NodeHandler;
import com.scireum.open.xml.StructuredNode;
import com.scireum.open.xml.XMLReader;
import me.kobosil.Models.CamEntry;

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
        final MySQL sql = new MySQL();
        // http://download.geofabrik.de/
        if (!new File("config.prop").exists()) {
            writeToFile("host=xxx");
            writeToFile("port=xxx");
            writeToFile("database=xxx");
            writeToFile("user=xxx");
            writeToFile("password=xxx");
            writeToFile("xml_file=berlin-latest.osm");
            writeToFile("logFile=last.log");
            writeToFile("loggerLevel=INFO");
        }
        final HashMap<String, String> conf = loadConfig();
        initLog(conf);
        try {
            while (!sql.isConnected()) {
                sql.connect(conf.get("host"), Integer.parseInt(conf.get("port")), conf.get("database"), conf.get("user"), conf.get("password"));
            }
            XMLReader r = new XMLReader();
            r.addHandler("node", new NodeHandler() {

                @Override
                public void process(StructuredNode node) {
                    try {
                        HashMap<String, String> tags = new HashMap<String, String>();
                        for (StructuredNode tag : node.queryNodeList("tag")) {
                            tags.put(tag.queryString("@k"), tag.queryString("@v"));
                        }
                        if (tags.values().contains("surveillance") || tags.keySet().contains("surveillance")) {
                            long id = Long.parseLong(node.queryString("@id"));
                            Double lat = Double.parseDouble(node.queryString("@lat"));
                            Double lon = Double.parseDouble(node.queryString("@lon"));
                            CamEntry cam = new CamEntry(id, lat, lon, tags);
                            LOGGER.info(cam.toString());
                            while (!sql.isConnected()) {
                                sql.connect(conf.get("host"), Integer.parseInt(conf.get("port")), conf.get("database"), conf.get("user"), conf.get("password"));
                            }
                            sql.insertCam(cam);
                        }


                    /*    for(StructuredNode tag : node.queryNodeList("tag"))
                    System.out.println(tag.);
                    System.out.println(node.queryValue("price").asDouble(0d));*/
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            r.parse(new FileInputStream(conf.get("xml_file")));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }*/

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
