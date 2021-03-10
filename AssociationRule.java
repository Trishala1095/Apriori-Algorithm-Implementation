import java.util.HashSet;
import java.util.Set;

public class AssociationRule {

    private Set<String> antecedent = new HashSet<>();
    private Set<String> consequent = new HashSet<>();
    private double confidence;
    private double sourceSetSupportValue;

    public Set<String> getAntecedent() {
        return antecedent;
    }

    public void setAntecedent(Set<String> antecedent) {
        this.antecedent = antecedent;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Set<String> getConsequent() {
        return consequent;
    }

    public void setConsequent(Set<String> consequent) {
        this.consequent = consequent;
    }

    public double getSourceSetSupportValue() {
        return sourceSetSupportValue;
    }

    public void setSourceSetSupportValue(double sourceSetSupportValue) {
        this.sourceSetSupportValue = sourceSetSupportValue;
    }
}
