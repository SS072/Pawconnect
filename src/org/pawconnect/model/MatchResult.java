package org.pawconnect.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the algorithmic compatibility match score and rationale between
 * an applicant's living environment and a specific Pet.
 */
public class MatchResult {
    private final Pet pet;
    private final int score; // 0 to 100 percentage
    private final List<String> matchingFactors;
    private final List<String> potentialConcerns;

    public MatchResult(Pet pet, int score) {
        this.pet = pet;
        this.score = Math.max(0, Math.min(100, score));
        this.matchingFactors = new ArrayList<>();
        this.potentialConcerns = new ArrayList<>();
    }

    public Pet getPet() { return pet; }
    public int getScore() { return score; }
    public List<String> getMatchingFactors() { return matchingFactors; }
    public List<String> getPotentialConcerns() { return potentialConcerns; }

    public void addFactor(String factor) {
        this.matchingFactors.add(factor);
    }

    public void addConcern(String concern) {
        this.potentialConcerns.add(concern);
    }
}
