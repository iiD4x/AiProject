package ai.project;

import java.util.ArrayList;
import static ai.project.AIProject.number_of_tasks;
import static java.lang.Integer.max;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Schedule {
public ArrayList<Task> processor1 = new ArrayList<>();
public ArrayList<Task> processor2 = new ArrayList<>();
public int sFT=0;

    //Setup Time .. to set start time and finish time for each task in the processor ignoring dependencies.
public void initialTime(){
    
    for(int i=0; i<processor1.size(); i++){
        if(i==0){       //if it'mainSchedule the first one in the string
            processor1.get(i).setStartTime(0);
            processor1.get(i).setFinshTime(processor1.get(i).getDuration());
        }else{          //if it'mainSchedule not the first one 
            processor1.get(i).setStartTime(processor1.get(i-1).getFinshTime());
            processor1.get(i).setFinshTime(processor1.get(i).getDuration()+processor1.get(i-1).getFinshTime());  //starting time = previouse one finishing time + duration
        }
    }
    
    for(int j=0; j<processor2.size(); j++){
        if(j==0){       //if it'mainSchedule the first one in the string
            processor2.get(j).setStartTime(0);
            processor2.get(j).setFinshTime(processor2.get(j).getDuration());
        }else{          //if it'mainSchedule not the first one 
            processor2.get(j).setStartTime(processor2.get(j-1).getFinshTime());
            processor2.get(j).setFinshTime(processor2.get(j).getDuration() + processor2.get(j-1).getFinshTime());  //starting time = previouse one finishing time + duration
        }
    }
    
}

//update time in tasks
public void updateTime(){
    for(int i=0; i<processor1.size(); i++){
        if((i+1) < processor1.size()){   //still in the array boundaries
            if(processor1.get(i+1).getStartTime() < processor1.get(i).getFinshTime()){
                processor1.get(i+1).setStartTime(processor1.get(i).getFinshTime());
                processor1.get(i+1).setFinshTime(processor1.get(i+1).getStartTime() + processor1.get(i+1).getDuration());
            }
        }
    }
    
    for(int j=0; j<processor2.size(); j++){
        if((j+1) < processor2.size()){   //still in the array boundaries
            if(processor2.get(j+1).getStartTime() < processor2.get(j).getFinshTime()){
                processor2.get(j+1).setStartTime(processor2.get(j).getFinshTime());
                processor2.get(j+1).setFinshTime(processor2.get(j+1).getStartTime() + processor2.get(j+1).getDuration());
            }
        }
    }
    
}

//FitnessFunction
public int FitnessFunction(){
    int maxHieght = AIProject.ts.get(AIProject.ts.size()-1).getHight();
    
    for(int i=1; i<=maxHieght; i++){    //starting from hieght 1
        // Processor-1
        for (int j = 0; j < processor1.size(); j++) {
            if(processor1.get(j).getHight() == i && processor1.get(j).getHight() != 0){ // same high
                
                for(int s = 0; s < processor1.get(j).getPredecessor().size(); s++) {
                    for(int k = 0; k < processor2.size(); k++){// loop as number of predecessores of the Task to check them one by one
                    if(processor2.get(k).getId() == processor1.get(j).getPredecessor().get(s)){//check if predecessore number "s" is equal to the task in the second processor
                        if(processor1.get(j).getStartTime() < processor2.get(k).getFinshTime() && processor1.get(j).getHight() != 0){//update if start time is less than next task finish time
                            
                            processor1.get(j).setStartTime(processor2.get(k).getFinshTime());
                            processor1.get(j).setFinshTime(processor1.get(j).getStartTime() + processor1.get(j).getDuration());
                            break;
                        }
                    }
                  }
              }  
            }
        }
        

        // Processor-2
        for (int j = 0; j < processor2.size(); j++) {
            if(processor2.get(j).getHight() == i){ // match
               // int lastDep = processor2.get(j).getPredecessor().get(processor2.get(j).getPredecessor().size()-1);
                
                for(int s = 0; s < processor2.get(j).getPredecessor().size(); s++) {
                    for(int k = 0; k < processor1.size(); k++){
                    if(processor1.get(k).getId() == processor2.get(j).getPredecessor().get(s)){
                        if(processor2.get(j).getStartTime() < processor1.get(k).getFinshTime() && processor2.get(j).getHight() != 0){//update
                            
                            processor2.get(j).setStartTime(processor1.get(k).getFinshTime());
                            processor2.get(j).setFinshTime(processor2.get(j).getStartTime() + processor2.get(j).getDuration());
                            break;
                        }
                    }
                  }
                }
            }
        }
    }
    //get max finish time
    updateTime();//useless actually but its work is to fix any timing conflicts after calling fitness function

    int getSchedFT = 0;
    if(processor1.get(processor1.size()-1).getFinshTime()>processor2.get(processor2.size()-1).getFinshTime()) {
        getSchedFT = processor1.get(processor1.size() - 1).getFinshTime();
//        System.out.println("Sched FT :" + getSchedFT);
        return getSchedFT;
    } else {
        getSchedFT = processor2.get(processor2.size() - 1).getFinshTime();
//        System.out.println("Sched FT :" + getSchedFT);
        return getSchedFT;
    }
}

//print all Tasks    
    public void print() {
        System.out.println("Schedule Finish Time = "+getsFT());
        System.out.println("Processor #1");
        for (int i = 0; i < processor1.size(); i++) {
            System.out.println("Task #["+processor1.get(i).getId()+"] Duration ["+processor1.get(i).getDuration()+
                    "] Height ["+processor1.get(i).getHight()+"] Start Time ["+ processor1.get(i).getStartTime()
                    +"] Finish Time ["+processor1.get(i).getFinshTime()+"]\n" );
        }
            System.out.println("Processor #2 :\n");
        for (int j = 0; j < processor2.size(); j++) {
            System.out.println("Task #["+processor2.get(j).getId()+"] Duration ["+processor2.get(j).getDuration()+
                    "] Height ["+processor2.get(j).getHight()+"] Start Time ["+ processor2.get(j).getStartTime()
                    +"] Finish Time ["+processor2.get(j).getFinshTime()+"]\n" );
        } 
    }


    public void setSchedFinishTime(int i) {
        sFT = i;
    }

    public int getsFT() {
        return sFT;
    }
}
