import java.util.ArrayList;

public class liaison {
    String depart;
    String arrivee;
    Integer duree;
    TypeTransport type;
    int ligne;
    ArrayList<String> horaires = new ArrayList<>();

    liaison() {}

    liaison(String depart, String arrivee, Integer duree, TypeTransport type) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.duree = duree;
        this.type = type;
    }

    liaison(String depart, String arrivee, Integer duree, TypeTransport type, int ligne) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.duree = duree;
        this.type = type;
        this.ligne = ligne;
    }

    
    /** 
     * Compare le depart et l'arrivee de la liaison avec les parametres.
     * @param depart
     * @param arrivee
     * @return boolean
     */
    public boolean equals(String depart, String arrivee) {
        return (this.depart.equals(depart) && this.arrivee.equals(arrivee));
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return "De " + depart + " a " + arrivee + " en " + duree + " minutes\nhoraires: "
        + horaires.toString();
    }
}
