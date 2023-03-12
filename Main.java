import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;


public class Main {
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://127.0.0.1/netzplandb";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "4634";

    public static void read_from_db(WorkPackageManager manager){
        // JDBC driver name and database URL


        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Reading table in given database...");
            stmt = conn.createStatement();

            String sql = "SELECT * FROM workpackage ORDER BY id";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                WorkPackage wp = new WorkPackage(rs.getString("name"), rs.getInt("id")
                        , rs.getDouble("duration"));
                String sqlPrev = "SELECT previd FROM prevlist WHERE id = "+Integer.toString(wp.getId()) + " ORDER BY previd";
                ResultSet rsPrev = stmt.executeQuery(sqlPrev);
                ArrayList<WorkPackage> preds = new ArrayList<>();
                while(rsPrev.next()) {
                    try {
                        WorkPackage wpsearched = manager.findPackage(rsPrev.getInt("previd"));
                        preds.add(wpsearched);
                    } catch (Exception ex) {
                        System.out.println("Package " + rsPrev.getInt("previd") + " cannot found!");
                        System.exit(-1);
                    }
                }
                wp.setPrev(preds);

                manager.add(wp);
            }

            manager.calculate();
            manager.calculateFP();
            manager.print();
            manager.kritischenPfad();

            } catch (SQLException se) {
                //Handle errors for JDBC
                se.printStackTrace();
            } catch (Exception e) {
                //Handle errors for Class.forName
                e.printStackTrace();
            } finally {
                //finally block used to close resources
                try {
                    if (stmt != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                }// do nothing
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }//end finally try
            }//end try
            System.out.println("Goodbye!");
    }//end JDBCExample

    public static void read_from_file() {
        WorkPackageManager manager = new WorkPackageManager();

        try {
            Scanner scanner = new Scanner(new FileReader("netzplan.txt"));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                WorkPackage wp = manager.getNewPackage(line);
                manager.add(wp);
            }

            manager.calculate();
            manager.calculateFP();
            manager.print();
            manager.kritischenPfad();

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read_from_console() {
        WorkPackageManager manager = new WorkPackageManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to add new work package? [Y/y/Yes/yes/N/n/No/no]");
        String ans = scanner.nextLine();
        while (ans.toLowerCase().startsWith("y")) {
            WorkPackage wp = manager.getNewPackage();
            manager.add(wp);
            System.out.println("Would you like to add new work package? [Y/y/Yes/yes/N/n/No/no]");
            ans = scanner.nextLine();
        }

        manager.calculate();
        manager.calculateFP();
        manager.print();
        manager.kritischenPfad();

    }

    public static void writeWorkpackageDB(WorkPackageManager manager){
        read_from_db(manager);
        WorkPackage wp = manager.getNewPackage();

        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            stmt = conn.createStatement();
            String sql = "INSERT INTO workpackage (id, name, duration) VALUES ("+Integer.toString(wp.getId()) +
                    ", \"" +wp.getName()+ "\"," +wp.getDuration()+")";
            ResultSet rs = stmt.executeQuery(sql);

            for(WorkPackage preds: wp.getPrev()){
                String sqlInsertStr = "INSERT INTO prevlist (id, previd) VALUES ("+Integer.toString(wp.getId())+ ","
                        +Integer.toString(preds.getId())+")";
                stmt.executeQuery(sqlInsertStr);
            }

            manager.calculate();
            manager.calculateFP();
            manager.print();
            manager.kritischenPfad();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }


    public static void main(String[] args) {

        WorkPackageManager manager = new WorkPackageManager();
        //read_from_file();
        //read_from_console();
        //read_from_db(manager);
        writeWorkpackageDB(manager);

    }
}