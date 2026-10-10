package placement.interfaces;

import placement.model.Job;

public interface Eligible {

    boolean checkEligibility(Job job);
}