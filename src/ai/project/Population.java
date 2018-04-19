
package ai.project;

import java.util.ArrayList;

import static java.lang.Integer.MAX_VALUE;

public class Population {
    ArrayList<Schedule> schedule = new ArrayList<>();
    private int BestFT = MAX_VALUE ;     //best finish time in this population

    //calculate best finish time of the population
    public int getBestFT() {

        for (int i = 0; i < schedule.size(); i++) {
            if(schedule.get(i).getsFT() < BestFT){
                this.BestFT = schedule.get(i).getsFT();
            }
        }
        return BestFT ;
    }

    public void print(){
        for (int i = 0; i < schedule.size(); i++) {
            System.out.println(i+"  FT : "+schedule.get(i).getsFT());
        }
    }

}
