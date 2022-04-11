import java.util.ArrayList;

public class OperationsHeures {
    
    /** 
     * Retourne la somme de minutes a H en format hhmm.
     * @param H
     * @param minutes
     * @return String
     */
    public String add_hours(String H, int minutes) {
        int h = Integer.parseInt(H.substring(0, 2));
        int m = Integer.parseInt(H.substring(2, 4));
        int min = (m + minutes) % 60;
        int heure = (h + (m + minutes) / 60) % 24;
        return String.format("%02d%02d", heure, min);
    }

    
    /** 
     * Retourne la difference en minutes entre H1 et H2 (H1 - H2).
     * @param H1
     * @param H2
     * @return int
     */
    public int substract_hours(String H1, String H2) {
        int h1 = Integer.parseInt(H1.substring(0, 2));
        int m1 = Integer.parseInt(H1.substring(2, 4));
        int h2 = Integer.parseInt(H2.substring(0, 2));
        int m2 = Integer.parseInt(H2.substring(2, 4));
        int heure = h1 - h2;
        int minutes = m1 - m2 + 60 * heure;
        return minutes;
    }

    
    /** 
     * Retourne l'horaire dans "schedule" qui suit l'heure "hour"
     * @param hour
     * @param schedule
     * @return String
     */
    public String get_next(String hour, ArrayList<String> schedule) {
        int H = Integer.parseInt(hour);
        int h = Integer.parseInt(schedule.get(0));
        int i = 0;
        while (h < H) {
            if (i < schedule.size()) {
                h = Integer.parseInt(schedule.get(i));
                i++;
            }
            else
                return null;
        }
        return String.format("%04d", h);
    }

    
    /** 
     * @param heure
     * @return String
     */
    public String toString(String heure) {
        String h = heure.substring(0, 2);
        String min = heure.substring(2, 4);
        return h + "h" + min;
    }
}
