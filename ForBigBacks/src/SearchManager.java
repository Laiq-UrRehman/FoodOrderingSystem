// Added: New class handling all search, filter, sort, and smart suggestion logic

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchManager {

    private FileHandler<Restaurant> fileHandler = new FileHandler<>();

    public Restaurant[] getAllRestaurants() {
        Restaurant[] restaurants = fileHandler.loadArray("restaurants.dat");
        if (restaurants == null) {
            System.out.println("No restaurants found.");
            return new Restaurant[0];
        }
        return restaurants;
    }

    // ── Restaurant Search

    public List<Restaurant> searchRestaurants(String query, Restaurant[] restaurants) {
        List<Restaurant> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Restaurant r : restaurants) {
            if (r.getName().toLowerCase().contains(lower)) {
                results.add(r);
            }
        }
        return results;
    }

    // ── Menu Item Search

    public List<FoodItem> searchMenuItems(String query, Restaurant restaurant) {
        List<FoodItem> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getName().toLowerCase().contains(lower)
                    || item.getCategory().toLowerCase().contains(lower)) {
                results.add(item);
            }
        }
        return results;
    }

    // ── Cuisine Filter

    public List<Restaurant> filterByCuisine(String cuisine, Restaurant[] restaurants) {
        List<Restaurant> results = new ArrayList<>();
        String lower = cuisine.toLowerCase();
        for (Restaurant r : restaurants) {
            if (r.getCuisineType().toLowerCase().equals(lower)) {
                results.add(r);
            }
        }
        return results;
    }

    // ── Category Filter

    public List<FoodItem> filterByCategory(String category, Restaurant restaurant) {
        List<FoodItem> results = new ArrayList<>();
        String lower = category.toLowerCase();
        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getCategory().toLowerCase().equals(lower)) {
                results.add(item);
            }
        }
        return results;
    }

    // ── Top Rated ─────────────────────────────────────────────────────────────

    public List<Restaurant> getTopRatedRestaurants(Restaurant[] restaurants) {
        List<Restaurant> sorted = new ArrayList<>();
        for (Restaurant r : restaurants)
            sorted.add(r);
        Collections.sort(sorted, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant a, Restaurant b) {
                return Double.compare(b.getRating(), a.getRating());
            }
        });
        return sorted;
    }

    public List<FoodItem> getTopRatedItems(Restaurant restaurant) {
        List<FoodItem> sorted = new ArrayList<>(restaurant.getMenu().getItems());
        Collections.sort(sorted, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem a, FoodItem b) {
                return Double.compare(b.getRating(), a.getRating());
            }
        });
        return sorted;
    }

    public List<FoodItem> getTopRatedItemsGlobal(Restaurant[] restaurants) {
        List<FoodItem> all = new ArrayList<>();
        for (Restaurant r : restaurants)
            all.addAll(r.getMenu().getItems());
        Collections.sort(all, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem a, FoodItem b) {
                return Double.compare(b.getRating(), a.getRating());
            }
        });
        return all;
    }

    // ── Smart Suggestions ─────────────────────────────────────────────────────

    public List<FoodItem> getPopularItems(Restaurant restaurant) {
        List<FoodItem> sorted = new ArrayList<>(restaurant.getMenu().getItems());
        Collections.sort(sorted, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem a, FoodItem b) {
                return Integer.compare(b.getOrderCount(), a.getOrderCount());
            }
        });
        return sorted;
    }

    public List<FoodItem> getPopularItemsGlobal(Restaurant[] restaurants) {
        List<FoodItem> all = new ArrayList<>();
        for (Restaurant r : restaurants)
            all.addAll(r.getMenu().getItems());
        Collections.sort(all, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem a, FoodItem b) {
                return Integer.compare(b.getOrderCount(), a.getOrderCount());
            }
        });
        return all;
    }

    public List<String> getTrendingCategories(Restaurant[] restaurants) {
        Map<String, Integer> counts = new HashMap<>();
        for (Restaurant r : restaurants) {
            for (FoodItem item : r.getMenu().getItems()) {
                String cat = item.getCategory();
                counts.put(cat, counts.getOrDefault(cat, 0) + item.getOrderCount());
            }
        }
        List<String> categories = new ArrayList<>(counts.keySet());
        Collections.sort(categories, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(counts.get(b), counts.get(a));
            }
        });
        return categories;
    }

    public List<FoodItem> getSuggestedItems(Customer customer, Restaurant[] restaurants, int limit) {
        List<FoodItem> suggestions = new ArrayList<>();
        String preferred = customer.getPreferredCategory();

        if (preferred != null) {
            for (Restaurant r : restaurants) {
                for (FoodItem item : r.getMenu().getItems()) {
                    if (item.getCategory().equalsIgnoreCase(preferred)) {
                        suggestions.add(item);
                    }
                }
            }
            Collections.sort(suggestions, new Comparator<FoodItem>() {
                @Override
                public int compare(FoodItem a, FoodItem b) {
                    return Integer.compare(b.getOrderCount(), a.getOrderCount());
                }
            });
        }

        if (suggestions.size() < limit) {
            List<FoodItem> global = getPopularItemsGlobal(restaurants);
            for (FoodItem item : global) {
                if (!suggestions.contains(item)) {
                    suggestions.add(item);
                }
                if (suggestions.size() >= limit)
                    break;
            }
        }

        return suggestions.subList(0, Math.min(limit, suggestions.size()));
    }

    public void printRestaurants(List<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants found.");
            return;
        }
        for (Restaurant r : restaurants)
            System.out.println(r);
    }

    public void printItems(List<FoodItem> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (FoodItem item : items)
            System.out.println(item);
    }

    public void printCategories(List<String> categories) {
        if (categories.isEmpty()) {
            System.out.println("No trending categories found.");
            return;
        }
        System.out.println("=== Trending Categories ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
    }
}