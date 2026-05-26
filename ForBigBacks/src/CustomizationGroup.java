import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomizationGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupName;
    private List<String> options;
    private List<Double> extraCharges;

    public CustomizationGroup(String groupName) {
        this.groupName = groupName;
        this.options = new ArrayList<>();
        this.extraCharges = new ArrayList<>();
    }

    public void addOption(String option, double extraCharge) {
        options.add(option);
        extraCharges.add(extraCharge);
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Double> getExtraCharges() {
        return extraCharges;
    }

    public double getExtraCharge(int index) {
        return extraCharges.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(groupName + ": ");
        for (int i = 0; i < options.size(); i++) {
            sb.append(options.get(i));
            sb.append(extraCharges.get(i) > 0
                    ? " (+Rs." + extraCharges.get(i) + ")"
                    : " (free)");
            if (i < options.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }
}