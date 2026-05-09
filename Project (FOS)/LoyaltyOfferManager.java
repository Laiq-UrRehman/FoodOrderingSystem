import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyOfferManager {

    private static final String FILE_PATH = "loyalty_offers.txt";
    private List<LoyaltyOffer> offers;

    public LoyaltyOfferManager() {
        offers = new ArrayList<>();
        loadOffers();
    }

    // ── File Reading ───────────────────────────────────────────────────────────

    
    //  * Reads all offers from loyalty_offers.txt.
    //  * Each line format: offerCode | description | pointsRequired | discountPKR | minOrderPKR
    //  * Lines starting with '#' or blank lines are skipped.
     
    private void loadOffers() {
        offers.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and blank lines
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 5) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    String offerCode      = parts[0].trim();
                    String description    = parts[1].trim();
                    int    pointsRequired = Integer.parseInt(parts[2].trim());
                    double discountPKR    = Double.parseDouble(parts[3].trim());
                    double minOrderPKR    = Double.parseDouble(parts[4].trim());

                    offers.add(new LoyaltyOffer(offerCode, description, pointsRequired,
                                                discountPKR, minOrderPKR));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid numbers: " + line);
                }
            }
            System.out.println(offers.size() + " loyalty offers loaded from file.");

        } catch (FileNotFoundException e) {
            System.out.println("loyalty_offers.txt not found. No offers loaded.");
        } catch (IOException e) {
            System.out.println("Error reading loyalty_offers.txt: " + e.getMessage());
        }
    }

    // ── File Writing ───────────────────────────────────────────────────────────

    /**
     * Writes all current offers back to loyalty_offers.txt.
     * Useful after adding or removing an offer at runtime (e.g. by admin).
     */
    public void saveOffers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("# Format: offerCode | description | pointsRequired | discountPKR | minOrderPKR");
            writer.newLine();
            writer.write("# Lines starting with # are comments and are ignored");
            writer.newLine();
            writer.newLine();

            for (LoyaltyOffer offer : offers) {
                String line = String.format("%s | %s | %d | %.0f | %.0f",
                        offer.getOfferCode(),
                        offer.getDescription(),
                        offer.getPointsRequired(),
                        offer.getDiscountPKR(),
                        offer.getMinOrderPKR());
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Offers saved to loyalty_offers.txt.");

        } catch (IOException e) {
            System.out.println("Error saving offers: " + e.getMessage());
        }
    }

    // ── Offer Management ───────────────────────────────────────────────────────

    /** Returns all loaded offers. */
    public List<LoyaltyOffer> getAllOffers() {
        return offers;
    }

    /**
     * Returns offers the customer qualifies for based on their
     * current points balance and cart total.
     */
    public List<LoyaltyOffer> getAvailableOffers(int pointsBalance, double cartTotalPKR) {
        List<LoyaltyOffer> available = new ArrayList<>();
        for (LoyaltyOffer offer : offers) {
            if (pointsBalance >= offer.getPointsRequired()
                    && cartTotalPKR >= offer.getMinOrderPKR()) {
                available.add(offer);
            }
        }
        return available;
    }

    /** Find an offer by its code (e.g. "LOYAL-A"). Returns null if not found. */
    public LoyaltyOffer findByCode(String code) {
        for (LoyaltyOffer offer : offers) {
            if (offer.getOfferCode().equalsIgnoreCase(code)) return offer;
        }
        return null;
    }

    /**
     * Add a new offer at runtime and immediately save to file.
     * Can be called by restaurantAdmin to introduce new deals.
     */
    public void addOffer(LoyaltyOffer offer) {
        offers.add(offer);
        saveOffers();
        System.out.println("New offer added: " + offer.getOfferCode());
    }

    /**
     * Remove an offer by code and save changes to file.
     */
    public void removeOffer(String offerCode) {
        offers.removeIf(o -> o.getOfferCode().equalsIgnoreCase(offerCode));
        saveOffers();
        System.out.println("Offer removed: " + offerCode);
    }

    /** Reload offers fresh from the file (e.g. if the file was edited externally). */
    public void refresh() {
        loadOffers();
    }

    // ── Display ────────────────────────────────────────────────────────────────

    public void printAllOffers() {
        if (offers.isEmpty()) {
            System.out.println("No offers available.");
            return;
        }
        System.out.println("=== All Loyalty Offers ===");
        for (LoyaltyOffer offer : offers) {
            System.out.println(offer);
        }
    }

    public void printAvailableOffers(int pointsBalance, double cartTotalPKR) {
        List<LoyaltyOffer> available = getAvailableOffers(pointsBalance, cartTotalPKR);
        if (available.isEmpty()) {
            System.out.println("No offers available for your points balance and cart total.");
            return;
        }
        System.out.println("=== Available Loyalty Offers ===");
        for (LoyaltyOffer offer : available) {
            System.out.println(offer);
        }
    }
}