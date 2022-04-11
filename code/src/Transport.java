import java.util.ArrayList;

public class Transport {
    private ArrayList<String> arrets;
    private ArrayList<liaison> liaisons;
    private TypeTransport type;

    Transport(ArrayList<liaison> liaisons, TypeTransport type) {
        this.liaisons = liaisons;
        this.type = type;

        arrets = new ArrayList<>();
        for (liaison l : liaisons) {
            if (!arrets.contains(l.depart)) {
                arrets.add(l.depart);
            } else if (!arrets.contains(l.arrivee)) {
                arrets.add(l.arrivee);
            }
        }
    }

    public void setArrets(ArrayList<String> arrets) {
        this.arrets = arrets;
    }

    public void setLiaisons(ArrayList<liaison> liaisons) {
        this.liaisons = liaisons;
    }

    public void setType(TypeTransport type) {
        this.type = type;
    }

    public TypeTransport getType() {
        return type;
    }

    public ArrayList<String> getArrets() {
        return arrets;
    }

    public ArrayList<liaison> getLiaisons() {
        return liaisons;
    }
}
