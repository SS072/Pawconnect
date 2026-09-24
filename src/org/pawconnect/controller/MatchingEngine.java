package org.pawconnect.controller;

import org.pawconnect.model.MatchResult;
import org.pawconnect.model.Pet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Algorithmic Compatibility Engine.
 * Evaluates candidate animals against prospective adopter housing, lifestyle, and household constraints.
 * Implements a weighted scoring heuristic across Housing, Energy, Children, and Pet Cohabitation.
 */
public class MatchingEngine {

    public static class Criteria {
        public String preferredSpecies; // "Any", "Dog", "Cat", "Rabbit"
        public String housingType;       // "Apartment", "Independent House", "Farm / Villa"
        public boolean hasYard;
        public int activityLevel;        // 1 to 5
        public boolean hasChildren;
        public boolean hasExistingDogs;
        public boolean hasExistingCats;
        public int hoursAwayPerDay;      // 0 to 12
    }

    public List<MatchResult> calculateMatches(List<Pet> candidates, Criteria criteria) {
        List<MatchResult> results = new ArrayList<>();

        for (Pet pet : candidates) {
            // Filter out adopted pets
            if ("ADOPTED".equalsIgnoreCase(pet.getAdoptionStatus())) {
                continue;
            }

            // Species filter if user specified a preference
            if (criteria.preferredSpecies != null && 
                !criteria.preferredSpecies.equalsIgnoreCase("Any") && 
                !criteria.preferredSpecies.equalsIgnoreCase(pet.getSpecies())) {
                continue;
            }

            int score = 0;
            MatchResult match = new MatchResult(pet, 0);

            // 1. Housing & Space Assessment (Max 25 pts)
            if ("Apartment".equalsIgnoreCase(criteria.housingType)) {
                if ("Cat".equalsIgnoreCase(pet.getSpecies()) || "Rabbit".equalsIgnoreCase(pet.getSpecies())) {
                    score += 25;
                    match.addFactor("Indoor profile perfectly suited for apartment residence.");
                } else if ("Dog".equalsIgnoreCase(pet.getSpecies())) {
                    if ("Small".equalsIgnoreCase(pet.getSize()) || pet.getActivityLevel() <= 2) {
                        score += 22;
                        match.addFactor("Calm energy profile well-adapted to apartment living.");
                    } else if (pet.getActivityLevel() >= 4 && !criteria.hasYard) {
                        score += 5;
                        match.addConcern("High activity breed in apartment without yard may require frequent outdoor exercise.");
                    } else {
                        score += 15;
                    }
                }
            } else { // House / Farm
                score += 25;
                if (criteria.hasYard) {
                    match.addFactor("Secure outdoor yard provides excellent enrichment space.");
                }
            }

            // 2. Activity / Energy Match (Max 25 pts)
            int energyDiff = Math.abs(criteria.activityLevel - pet.getActivityLevel());
            if (energyDiff == 0) {
                score += 25;
                match.addFactor("Energy level matches household lifestyle exactly (Rating: " + pet.getActivityLevel() + "/5).");
            } else if (energyDiff == 1) {
                score += 20;
                match.addFactor("Compatible energy profile with minimal routine adjustments.");
            } else if (energyDiff == 2) {
                score += 12;
                match.addConcern("Slight energy mismatch; schedule adjustments recommended.");
            } else {
                score += 4;
                match.addConcern("Significant energy disparity (Household: " + criteria.activityLevel + "/5, Pet: " + pet.getActivityLevel() + "/5).");
            }

            // 3. Children Safety Assessment (Max 25 pts)
            if (criteria.hasChildren) {
                if (pet.isGoodWithChildren()) {
                    score += 25;
                    match.addFactor("Documented gentle temperament around young children.");
                } else {
                    score += 0;
                    match.addConcern("Not recommended for households with young children.");
                }
            } else {
                score += 25; // Neutral baseline
            }

            // 4. Cohabitation With Other Pets (Max 25 pts)
            int cohabitationPoints = 0;
            int conditions = 0;

            if (criteria.hasExistingDogs) {
                conditions++;
                if (pet.isGoodWithDogs()) {
                    cohabitationPoints += 15;
                    match.addFactor("Socialized and compatible with existing canine companions.");
                } else {
                    match.addConcern("Prefers to be the only canine in the home.");
                }
            }

            if (criteria.hasExistingCats) {
                conditions++;
                if (pet.isGoodWithCats()) {
                    cohabitationPoints += 10;
                    match.addFactor("Low prey drive; safe around feline companions.");
                } else {
                    match.addConcern("High prey drive; unsuitable for households with cats.");
                }
            }

            if (conditions == 0) {
                score += 25; // No existing pets to consider
            } else {
                score += Math.min(25, cohabitationPoints + (conditions == 1 ? 10 : 0));
            }

            // 5. Workday Schedule / Hours Away Adjustment
            if (criteria.hoursAwayPerDay > 8 && pet.getAgeMonths() < 12) {
                score -= 15;
                match.addConcern("Young animal requires more supervision than available during long workdays.");
            }

            MatchResult finalizedResult = new MatchResult(pet, score);
            for (String f : match.getMatchingFactors()) finalizedResult.addFactor(f);
            for (String c : match.getPotentialConcerns()) finalizedResult.addConcern(c);
            results.add(finalizedResult);
        }

        // Sort descending by score
        results.sort(Comparator.comparingInt(MatchResult::getScore).reversed());
        return results;
    }
}
