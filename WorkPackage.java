import java.util.ArrayList;

public class WorkPackage {
    final private String name;
    final private int id;

    public double getDuration() {
        return duration;
    }

    final private double duration;
    private ArrayList<WorkPackage> prev;

    private int sez = Integer.MAX_VALUE;
    public int fp = Integer.MAX_VALUE;;

    public String getName() {return name;}

    public int getSez() {return sez;}

    public int getSaz() {return sez - (int)duration;}

    public void setSez(int s) {
        if (s < sez) {
            sez = s;
        }
    }

    public int getId() {
        return id;
    }

    public WorkPackage(String name, int id, double duration) {
        this.name = name;
        this.id = id;
        this.duration = duration;
    }

    public int getFaz() {
        int maxfez = 0;
        for (WorkPackage wp : prev) {
            if (maxfez < wp.getFez()) maxfez = wp.getFez();
        }
        return maxfez;
    }

    public int getFez() {
        return (int) (this.getFaz() + duration);
    }

    public int setGP(){return (this.getSaz() - this.getFaz()); }//yeni yazildi

    public void setPrev(ArrayList<WorkPackage> p) {
        this.prev = p;
    }

    public int setFP(int min){
        if (min < fp) {
            fp = min;
        }
        return fp;
    }

    public int getFp() {
        return fp;
    }

    public ArrayList<WorkPackage> getPrev() {return prev;}


    public void print() {
        System.out.println("---------------------------------------------------");
        System.out.printf("| Id: %-10s  Name: %-10s\n", id, name);
        System.out.printf("| Duration: %-20s\n", duration);
        System.out.printf("| Faz: %-10s Fez: %-10s\n", getFaz(), getFez());
        System.out.printf("| Saz: %-10s Sez: %-10s\n", getSaz(), getSez());
        System.out.printf("| GP: %-10s  FP: %-10s\n", setGP(), getFp());
    }
}
