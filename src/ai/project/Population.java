
package ai.project;

import java.util.ArrayList;

import static java.lang.Integer.MAX_VALUE;

public class Population {
    ArrayList<Schedule> schedule = new ArrayList<>();
    private int BestFT ;     //best finish time in this population

    public int getBestFT() {
        int minFT = MAX_VALUE ;      // best finish time
        for (int i = 0; i < schedule.size(); i++) {
            if(schedule.get(i).getsFT() < minFT){
                minFT = schedule.get(i).getsFT();
            }
        }
        return BestFT = minFT;
    }

}
