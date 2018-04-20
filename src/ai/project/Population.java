
package ai.project;

import java.util.ArrayList;

import static java.lang.Integer.MAX_VALUE;

public class Population {
    public ArrayList<Schedule> schedule = new ArrayList<>();
    private int BestFT = MAX_VALUE ;     //best finish time in this population

    //calculate best finish time of the population
    public int getBestFT() {

        for (int i = 0; i < schedule.size(); i++) {
            if(schedule.get(i).sFT < BestFT){
                this.BestFT = schedule.get(i).sFT;
            }
        }
        return BestFT ;
    }

    public void print(){
        for (int i = 0; i < schedule.size(); i++) {
            System.out.println("SchedNum "+(i+1));
            schedule.get(i).print();
        }
    }

}
