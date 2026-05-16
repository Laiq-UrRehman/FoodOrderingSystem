public class Rider extends Person {

    private static final long serialVersionUID = 1L;

    private String vehicleType;
    private boolean isAvailable;
    private boolean isAssigned;
    private Location location;

    public Rider() {}

    public Rider(String personID, String name, String address, String phoneNumber, String vehicleType, boolean isAvailable, Location location) {
        super(personID, name, address, phoneNumber);
        this.vehicleType = vehicleType;
        this.isAvailable = isAvailable;
        this.isAssigned = false;
        this.location = location;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public boolean isAssigned() {
        return isAssigned;
    }
 
    public void setAssigned(boolean assigned) {
        this.isAssigned = assigned;
    }

    public Location getLocation(){ 
        return location; 
    }
    public void setLocation(Location loc){ 
        this.location = loc; 
    }

    public void assignOrder() {
        if (isAvailable) {
            isAssigned  = true;
            isAvailable = false;
            return;
        }
        isAssigned = false;
    }

    public boolean getStatus() {
        return isAvailable;
    }

    public String getVehicleType() {
        return vehicleType;
    }
}