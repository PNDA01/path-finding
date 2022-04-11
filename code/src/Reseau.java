import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Reseau {
    public Transport intercites;
    public ArrayList<Transport> trains = new ArrayList<>();
    public Transport metros;
    public ArrayList<Transport> trams = new ArrayList<>();
    public OperationsHeures op = new OperationsHeures();

    Reseau() throws IOException, ParserConfigurationException, SAXException {
        init_intercites();
        init_metro();
        init_trams();
        init_trains();
    }

 
 /** 
  * Initialise "intercites" avec la lecture de InterCites.txt
  * @throws FileNotFoundException
  */
 void init_intercites() throws FileNotFoundException {
        Boolean hours = false;
        ArrayList<liaison> liaisons = new ArrayList<>();

        File file = new File("src/Donnees/InterCites.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (Pattern.matches("//.*", line)) {
                hours = true;
            } else if (!Pattern.matches("%.*", line)) {
                if (hours) {
                    String[] elts = line.split("[ \t]+");
                    for (liaison l : liaisons) {
                        if (l.equals(elts[0], elts[1])) {
                            l.horaires.add(elts[2]);
                        }
                    }
                } else {
                    String[] elts = line.split("[ \t]+");
                    liaison l = new liaison(elts[0], elts[1], Integer.valueOf(elts[2]), TypeTransport.Intercites);
                    liaisons.add(l);
                }
            }
        }
        sc.close();
        intercites = new Transport(liaisons, TypeTransport.Intercites);
    }

 
 /** 
  * Initialise "metros" avec la lecture de metro.txt
  * @throws FileNotFoundException
  */
 void init_metro() throws FileNotFoundException {
        String h_debut = "";
        String h_fin = "";
        int intervalle = 0;
        ArrayList<liaison> liaisons = new ArrayList<>();

        int etape = 0;
        int cond = 0;

        File file = new File("src/Donnees/metro.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                etape++;
                // System.out.println("etape: "+etape);
            } else if (!Pattern.matches("%.*", line)) {
                switch (etape) {
                    case 1:
                        String[] elts = line.split("[ \t]+");
                        liaison l = new liaison(elts[0], elts[1], Integer.valueOf(elts[2]), TypeTransport.Metro);
                        liaisons.add(l);
                        break;
                    case 2:
                        switch (cond) {
                            case 0:
                                h_debut = line;
                                cond++;
                                break;
                            case 1:
                                intervalle = Integer.parseInt(line);
                                cond++;
                                break;
                            case 2:
                                h_fin = line;
                                break;
                        }
                        break;
                }
            }
        }
        String heure = h_debut;
        int fin = Integer.parseInt(h_fin);
        int compteur = 0;
        while (Integer.parseInt(heure) <= fin) {
            for (liaison l : liaisons) {
                l.horaires.add(heure);
                heure = op.add_hours(heure, l.duree);
            }
            compteur++;
            heure = op.add_hours(h_debut, intervalle*compteur);
        }
        sc.close();
        metros = new Transport(liaisons, TypeTransport.Metro);
    }

 
 /** 
  * Initialise "trains" avec la lecture de train.xml
  * @throws IOException
  * @throws ParserConfigurationException
  * @throws SAXException
  */
 void init_trains() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = df.newDocumentBuilder();
        Document document = builder.parse("src/Donnees/train.xml");

        NodeList lignes = document.getElementsByTagName("line");
        for (int i = 0; i < lignes.getLength(); i++) {
            Node ligne = lignes.item(i);
            ArrayList<liaison> liaisons = new ArrayList<>();
            if (ligne.getNodeType() == Node.ELEMENT_NODE) {
                Element el_ligne = (Element) ligne;
                NodeList junctions = el_ligne.getElementsByTagName("junction");
                for (int j = 0; j < junctions.getLength(); j++) {
                    Node junction = junctions.item(j);
                    if (ligne.getNodeType() == Node.ELEMENT_NODE) {
                        Element el_junction = (Element) junction;
                        String depart = el_junction.getElementsByTagName("start-station").item(0).getTextContent();
                        String arrivee = el_junction.getElementsByTagName("arrival-station").item(0).getTextContent();
                        String h_depart = el_junction.getElementsByTagName("start-hour").item(0).getTextContent();
                        String h_arrivee = el_junction.getElementsByTagName("arrival-hour").item(0).getTextContent();
                        boolean found = false;
                        for (liaison l : liaisons) {
                            if (l.equals(depart, arrivee)) {
                                l.horaires.add(h_depart);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            int duree = op.substract_hours(h_arrivee, h_depart);
                            liaison l = new liaison(depart, arrivee, duree, TypeTransport.Train, i+1);
                            l.horaires.add(h_depart);
                            liaisons.add(l);
                        }
                    }
                }
            }
            trains.add(new Transport(liaisons, TypeTransport.Train));
        }
    }

 
 /** 
  * Initialise "trams" avec la lecture de tram.xml
  * @throws IOException
  * @throws ParserConfigurationException
  * @throws SAXException
  */
 void init_trams() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = df.newDocumentBuilder();
        Document document = builder.parse("src/Donnees/tram.xml");

        NodeList lignes = document.getElementsByTagName("ligne");
        for (int i = 0; i < lignes.getLength(); i++) {
            Node ligne = lignes.item(i);
            ArrayList<liaison> liaisons = new ArrayList<>();
            if (ligne.getNodeType() == Node.ELEMENT_NODE) {
                Element el_ligne = (Element) ligne;
                String[] stations = el_ligne.getElementsByTagName("stations").item(0).getTextContent().split(" ");
                NodeList heures_passage = el_ligne.getElementsByTagName("heures-passage");
                String[] lst_heures = heures_passage.item(0).getTextContent().split(" ");
                for (int j = 0; j < (stations.length - 1); j++) {
                    int duree = op.substract_hours(lst_heures[j + 1], lst_heures[j]);
                    liaison l = new liaison(stations[j], stations[j + 1], duree, TypeTransport.Tram, i+1);
                    liaisons.add(l);
                }
                for (int k = 0; k < heures_passage.getLength(); k++) {
                    String[] heures = heures_passage.item(k).getTextContent().split(" ");
                    for (int u = 0; u < (heures.length - 1); u++) {
                        liaisons.get(u).horaires.add(heures[u]);
                    }
                }
                ArrayList<liaison> toRemove = new ArrayList<>();
                for (liaison l : liaisons) {
                    if (l.depart.equals(l.arrivee)) {
                        toRemove.add(l);
                    }
                }
                liaisons.removeAll(toRemove);
            }
            trams.add(new Transport(liaisons, TypeTransport.Tram));
        }
    }

    
    /** 
     * Retourne la methode toString de tout les liaisons de tout les transports
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("INTERCITES:\n");
        for (liaison l : intercites.getLiaisons()) {
            sb.append(l.toString() + "\n");
        }
        sb.append("\nMETROS:\n");
        for (liaison l : metros.getLiaisons()) {
            String d = l.toString();
            sb.append(d + "\n");
        }
        
        sb.append("\nTRAINS:\n");
        for (Transport t : trains) {
            sb.append("ligne " + (trains.indexOf(t) + 1) + ":\n");
            for (liaison l : t.getLiaisons()) {
                String d = l.toString();
                sb.append(d + "\n");
            }
        }
        sb.append("\nTRAMS:\n");
        for (Transport t : trams) {
            sb.append("ligne " + (trams.indexOf(t) + 1) + ":\n");
            for (liaison l : t.getLiaisons()) {
                String d = l.toString();
                    sb.append(d + "\n");
            }
        }
        return sb.toString();
    }
  
    
    /** 
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Reseau reseau = new Reseau();
        System.out.println(reseau.toString());
    }
}
