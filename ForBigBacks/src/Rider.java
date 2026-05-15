public class Rider extends Person {

    private static final long serialVersionUID = 1L;

    private String vehicleType;
    private boolean isAvailable;
    private boolean isAssigned;

    public Rider() {}

    public Rider(String personID, String name, String address, String phoneNumber, String vehicleType, boolean isAvailable) {
        super(personID, name, address, phoneNumber);
        this.vehicleType = vehicleType;
        this.isAvailable = isAvailable;
        this.isAssigned = false;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void assignOrder() {
        if (isAvailable) {
            isAssigned = true;
            return;
        }
        isAssigned = false;
    }

    public boolean getStatus() {
        return isAvailable;
    }
}