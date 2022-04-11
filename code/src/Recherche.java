import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Recherche {
    
    static String depart;
    static String arrivee;
    static String heure_depart;
    static Graphe graphe;
    static Map<String, Integer> distances;
    static Map<String, Arc> predecesseurs;
    static OperationsHeures op = new OperationsHeures();

    static class Arc {
        liaison arc;
        String horaire;

        Arc(liaison arc, String horaire) {
            this.arc = arc;
            this.horaire = horaire;
        }
    }

    
    /** 
     * Initialise les variables de recherche en les demandant a l'utilisateur. (La recherche est sensible au majuscules)
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void Init() throws IOException, ParserConfigurationException, SAXException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Inserez votre position:");
        depart = sc.nextLine();
        System.out.println("Inserez votre destination:");
        arrivee = sc.nextLine();
        System.out.println("Inserez votre heure de depart: (hhmm)");
        heure_depart = sc.nextLine();
        sc.close();
        graphe = new Graphe();

        distances = new HashMap<>();
        for (String key : graphe.Stations.keySet()) {
            distances.putIfAbsent(key, Integer.MAX_VALUE);
        }
        distances.replace(depart, 0);

        predecesseurs = new HashMap<>();
        for (String key : graphe.Stations.keySet()) {
            predecesseurs.putIfAbsent(key, null);
        }
    }

    
    /** 
     * Trouve le sommet avec distance minimale au depart dans l'ensemble Q.
     * @param Q
     * @return String
     */
    public static String Trouve_min(Set<String> Q) {
        int min = Integer.MAX_VALUE;
        String sommet = "";
        for (String key : Q) {
            if (distances.get(key) < min) {
                min = distances.get(key);
                sommet = key;
            }
        }
        return sommet;
    }

    
    /** 
     * Met a jour la distance du depart au sommet d'arrivee de l2 en regardant si vaut mieux passer par le sommet s1.
     * @param s1
     * @param l2
     */
    public static void maj_Distances(String s1, liaison l2) {
        String heure = op.add_hours(heure_depart, distances.get(s1));
        String h_next = op.get_next(heure, l2.horaires);
        if (h_next == null) {
            System.out.println("Chemin introuvable...");
            System.exit(0);
        }
        int attente = op.substract_hours(h_next, heure);
        if (distances.get(l2.arrivee) > (distances.get(s1) + l2.duree + attente)) {
            distances.replace(l2.arrivee, distances.get(s1) + l2.duree + attente);
            predecesseurs.replace(l2.arrivee, new Arc(l2, h_next));
        }
    }

    
    /** 
     * Effectue la recherche du chemin le plus rapide avec l'algorithme de Dijkstra.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void Dijkstra() throws IOException, ParserConfigurationException, SAXException {
        Set <String> Comp = new HashSet<>(graphe.Stations.keySet());
        while (!Comp.isEmpty()) {
            String s1 = Trouve_min(Comp);
            if (s1.equals(arrivee))
                return;
            try {
                Comp.remove(s1);
            } catch (Exception e) {
                System.out.println("Chemin introuvable...");
            }
            
            for (liaison l : graphe.Stations.get(s1)) {
                maj_Distances(s1, l);
            }
        }
    }

    
    /** 
     * Effectue la recherche, cree le chemin avec "predecesseurs" et affiche le chemin.
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Init();
        Dijkstra();
        ArrayList<Arc> chemin = new ArrayList<>();
        Arc s = predecesseurs.get(arrivee);
        while (s != null) {
            chemin.add(0, s);
            s = predecesseurs.get(s.arc.depart);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nCHEMIN:\n");
        for (Arc a : chemin) {
            sb.append(a.arc.depart + " -> " + a.arc.type + " a " + op.toString(a.horaire) + " (" + a.arc.duree + " min)" + "\n||\n\\/\n");
        }
        String heure_arrive = op.toString(op.add_hours(heure_depart, distances.get(arrivee)));
        sb.append(arrivee + "\nHeure d'arrivee: " + heure_arrive + "\nDuree: " + distances.get(arrivee) + " minutes.");
        System.out.println(sb.toString());
    }
}
