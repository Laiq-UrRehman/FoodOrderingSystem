// Updated: loadOffers() and saveOffers() now catch FileHandler.FileOperationException instead of raw IOException
// Updated: addOffer() throws IllegalArgumentException for null offers or duplicate codes
// Updated: removeOffer() throws IllegalArgumentException for null or blank offer codes

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

    private void loadOffers() {
        offers.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] parts = line.split("\\|");
                if (parts.length != 5) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    String offerCode = parts[0].trim();
                    String description = parts[1].trim();
                    int pointsRequired = Integer.parseInt(parts[2].trim());
                    double discountPKR = Double.parseDouble(parts[3].trim());
                    double minOrderPKR = Double.parseDouble(parts[4].trim());

                    offers.add(new LoyaltyOffer(offerCode, description, pointsRequired, discountPKR, minOrderPKR));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid numbers: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("loyalty_offers.txt not found. No offers loaded.");
        } catch (IOException e) {
            System.out.println("Error reading loyalty_offers.txt: " + e.getMessage());
        }
    }

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

    public List<LoyaltyOffer> getAllOffers() {
        return offers;
    }

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

    public LoyaltyOffer findByCode(String code) {
        if (code == null || code.isBlank())
            return null;
        for (LoyaltyOffer offer : offers) {
            if (offer.getOfferCode().equalsIgnoreCase(code))
                return offer;
        }
        return null;
    }

    public void addOffer(LoyaltyOffer offer) {
        if (offer == null)
            throw new IllegalArgumentException("Offer cannot be null");
        if (offer.getOfferCode() == null || offer.getOfferCode().isBlank())
            throw new IllegalArgumentException("Offer code cannot be null or empty");
        if (findByCode(offer.getOfferCode()) != null)
            throw new IllegalArgumentException("An offer with code " + offer.getOfferCode() + " already exists");

        offers.add(offer);
        saveOffers();
        System.out.println("New offer added: " + offer.getOfferCode());
    }

    public void removeOffer(String offerCode) {
        if (offerCode == null || offerCode.isBlank())
            throw new IllegalArgumentException("Offer code cannot be null or empty");
        offers.removeIf(o -> o.getOfferCode().equalsIgnoreCase(offerCode));
        saveOffers();
        System.out.println("Offer removed: " + offerCode);
    }

    public void refresh() {
        loadOffers();
    }

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