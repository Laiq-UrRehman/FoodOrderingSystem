public class RiderSeeder {

    public static void main(String[] args) {
        seedRiders();
    }

    public static void seedRiders() {
        FileHandler<Rider> fileHandler = new FileHandler<>();

        // Locations are spread across the 0-100 grid.
        // These are starting positions; OrderTracking randomizes them per order
        // (only for available riders).
        Rider[] riders = {
            new Rider("RD001", "Kamran Ali",   "Gulberg, Lahore",    "03011112222", "Bike",     true, new Location(10.0, 20.0)),
            new Rider("RD002", "Zain Ul Abdin","DHA Phase 4, Lahore","03122223333", "Bike",     true, new Location(60.0, 50.0)),
            new Rider("RD003", "Hassan Raza",  "Johar Town, Lahore", "03233334444", "Scooter",  true, new Location(80.0, 80.0)),
            new Rider("RD004", "Bilal Ahmed",  "Model Town, Lahore", "03344445555", "Scooter",  true, new Location(30.0, 70.0))
        };

        fileHandler.saveArray(riders, "riders.dat");
        System.out.println("riders.dat seeded with " + riders.length + " riders.");

        // Verify
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