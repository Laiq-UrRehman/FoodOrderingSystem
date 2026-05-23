public class RiderSeeder {

    public static void main(String[] args) {
        seedRiders();
    }

    public static void seedRiders() {
        FileHandler<Rider> fileHandler = new FileHandler<>();

        Rider[] riders = {
            new Rider("RD001", "Kamran Ali",    "Gulberg, Lahore",       "03011112222", "Bike",     true, new Location(10.0, 20.0)),
            new Rider("RD002", "Zain Ul Abdin", "DHA Phase 4, Lahore",   "03122223333", "Bike",     true, new Location(60.0, 50.0)),
            new Rider("RD003", "Hassan Raza",   "Johar Town, Lahore",    "03233334444", "Scooter",  true, new Location(80.0, 80.0)),
            new Rider("RD004", "Bilal Ahmed",   "Model Town, Lahore",    "03344445555", "Scooter",  true, new Location(30.0, 70.0)),
            new Rider("RD005", "Usman Tariq",   "Bahria Town, Lahore",   "03455556666", "Bike",     true, new Location(15.0, 40.0)),
            new Rider("RD006", "Ahsan Javed",   "Cantt, Lahore",         "03566667777", "Car",      true, new Location(45.0, 25.0)),
            new Rider("RD007", "Saad Khan",     "Wapda Town, Lahore",    "03677778888", "Bike",     true, new Location(75.0, 15.0)),
            new Rider("RD008", "Farhan Malik",  "Iqbal Town, Lahore",    "03788889999", "Scooter",  true, new Location(90.0, 35.0)),
            new Rider("RD009", "Ali Hamza",     "Garden Town, Lahore",   "03899990000", "Bike",     true, new Location(55.0, 90.0)),
            new Rider("RD010", "Hamza Yousuf",  "Valencia, Lahore",      "03911112222", "Car",      true, new Location(20.0, 85.0)),
            new Rider("RD011", "Muneeb Aslam",  "Faisal Town, Lahore",   "03022223333", "Bike",     true, new Location(65.0, 10.0)),
            new Rider("RD012", "Danish Iqbal",  "Township, Lahore",      "03133334444", "Scooter",  true, new Location(95.0, 60.0))
        };

        fileHandler.saveArray(riders, "riders.dat");
        System.out.println("riders.dat seeded with " + riders.length + " riders.");

        Rider[] loaded = fileHandler.loadArray("riders.dat");
        if (loaded == null) { System.out.println("Failed to load riders.dat"); return; }

        System.out.println("\n===== Loaded Riders =====");
        for (Rider r : loaded) {
            System.out.println("ID: " + r.getPersonID()
                    + " | Name: " + r.getName()
                    + " | Vehicle: " + r.getVehicleType()
                    + " | Available: " + r.getStatus()
                    + " | Location: " + r.getLocation());
        }
    }
}