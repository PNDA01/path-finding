import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Graphe {

    public Map<String, List<liaison>> Stations;
    static Reseau reseau;

    Graphe() throws IOException, ParserConfigurationException, SAXException {
        Stations = new HashMap<>();
        reseau = new Reseau();
        for (liaison l : reseau.intercites.getLiaisons()) {
            ajouterNoeud(l.depart);
            ajouterArc(l.depart, l);
        }
        for (liaison l : reseau.metros.getLiaisons()) {
            ajouterNoeud(l.depart);
            ajouterArc(l.depart, l);
        }
        for (Transport t : reseau.trains) {
            for (liaison l : t.getLiaisons()) {
                ajouterNoeud(l.depart);
                ajouterArc(l.depart, l);
            }
        }
        for (Transport t : reseau.trams) {
            for (liaison l : t.getLiaisons()) {
                ajouterNoeud(l.depart);
                ajouterArc(l.depart, l);
            }
        }
    }

    
    /** 
     * Ajoute le sommet "id" au graphe sans aucun successeur s'il n'existe pas.
     * @param id
     */
    void ajouterNoeud(String id) {
        Stations.putIfAbsent(id, new ArrayList<>());
    }

    
    /** 
     * Ajoute l'arc "l" au sommet "id"
     * @param id
     * @param l
     */
    void ajouterArc(String id, liaison l) {
        Stations.get(id).add(l);
    }

    
    /** 
     * Retourne les sommets et leurs listes d'adjacence avec le transport et ligne (si applicable) qui font la liaison.
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : Stations.keySet()) {
            sb.append(key + ": ");
            for (liaison l : Stations.get(key)) {
                String ligne = (l.type.equals(TypeTransport.Train) || l.type.equals(TypeTransport.Tram)) ? " " + String.valueOf(l.ligne) : "";
                sb.append(l.arrivee + "(" + l.type + ligne + "), ");
            }
            sb.append("\n");
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
        Graphe g = new Graphe();
        System.out.println(g.toString());
    }
}
