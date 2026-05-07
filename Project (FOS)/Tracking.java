public class Tracking {
    private String trackID;
    private String estimatedETA;
    private String status;

    public Tracking() {
    }

    public Tracking(String trackID, String estimatedETA, String status) {
        this.trackID = trackID;
        this.estimatedETA = estimatedETA;
        this.status = status;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public String getEstimatedETA() {
        return estimatedETA;
    }

    public void setEstimatedETA(String estimatedETA) {
        this.estimatedETA = estimatedETA;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}