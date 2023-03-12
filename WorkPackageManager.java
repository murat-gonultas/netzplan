import java.util.ArrayList;
import java.util.Scanner;

public class WorkPackageManager {
    final private ArrayList<WorkPackage> packages = new ArrayList<>();

    public void add(WorkPackage p) {
        packages.add(p);
    }

    public int getMaxId(){
        int maxId = 0;
        for(WorkPackage wp:packages){
            if(maxId < wp.getId())
                maxId = wp.getId();
        }
        return maxId;
    }

    public void calculate() {
        if (packages.size() == 0) return;

        WorkPackage wplast = packages.get(packages.size()-1);
        wplast.setSez(wplast.getFez());

        for (int i = packages.size()-1; i >= 0; i--) {
            ArrayList<WorkPackage> mypreds = packages.get(i).getPrev();
            for (WorkPackage wps : mypreds) {
                wps.setSez(packages.get(i).getSaz());
            }
        }
    }

    public void calculateFP(){
        int min = 0;
        WorkPackage wplast = packages.get(packages.size()-1);
        wplast.fp = min;
        for (int i = packages.size() -1; i >= 0; i-- ) {
            ArrayList<WorkPackage> mypreds = packages.get(i).getPrev();
            for (WorkPackage wps : mypreds) {
                wps.setFP( packages.get(i).getFaz() - wps.getFez());
            }
        }
    }

    WorkPackage findPackage(int id) throws Exception {
        for (WorkPackage wp: packages) {
            if (wp.getId() == id) {
                return wp;
            }
        }
        throw new Exception("Could not found such a package");
    }

    public WorkPackage getNewPackage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Name: ");
        String wpname = scanner.nextLine();
        System.out.println("Id: ");
        int wpid = scanner.nextInt();
        System.out.println("Duration: ");
        double wpduration = scanner.nextDouble();
        ArrayList<WorkPackage> predecessor = new ArrayList<>();
        int wppred = -1;
        do {
            System.out.println("Get predecessor (end with -1): ");
            wppred = scanner.nextInt();
            if (wppred != -1) {
                try {
                    WorkPackage wpsearched = findPackage(wppred);
                    predecessor.add(wpsearched);
                } catch (Exception ex) {
                    System.out.println("Package cannot found!");
                }
            }
        } while (wppred != -1);
        WorkPackage wp = new WorkPackage(wpname, wpid, wpduration);
        wp.setPrev(predecessor);

        return wp;
    }

    public WorkPackage getNewPackage(String line) {

        String[] tokens = line.split(" ");

        String wpname = tokens[0];
        int wpid = Integer.parseInt(tokens[1]);
        double wpduration = Double.parseDouble(tokens[2]);
        ArrayList<WorkPackage> preds = new ArrayList<>();
        for (int i = 3; i < tokens.length; i++) {
            try {
            WorkPackage wpsearched = findPackage(Integer.parseInt(tokens[i]));
                preds.add(wpsearched);
            } catch (Exception ex) {
                System.out.println("Package " + tokens[i] + " cannot found!");
                System.exit(-1);
            }
        }

        WorkPackage wp = new WorkPackage(wpname, wpid, wpduration);
        wp.setPrev(preds);

        return wp;
    }

    public void kritischenPfad(){
        for (WorkPackage wp : packages) {
            if(wp.setGP() == 0 && wp.fp == 0) System.out.printf("Diese Arbeitspaket ist kritische Pfad :" +wp.getName() +"\n");
        }
    }

    public void print() {
        for (WorkPackage wp : packages) {
            wp.print();
        }
    }
}
