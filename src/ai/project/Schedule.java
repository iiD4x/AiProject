package ai.project;

import java.util.ArrayList;

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
public int FitnessFunction(Schedule currentSchedule){
    int maxHieght = AIProject.ts.get(AIProject.ts.size()-1).getHeight();
    
    for(int i=1; i<=maxHieght; i++){    //starting from hieght 1
        // Processor-1
        for (int j = 0; j < currentSchedule.processor1.size(); j++) {
            if(currentSchedule.processor1.get(j).getHeight() == i){ // same high
                
                for(int s = 0; s < currentSchedule.processor1.get(j).getPredecessor().size(); s++) {
                    for(int k = 0; k < currentSchedule.processor2.size(); k++){// loop as number of predecessores of the Task to check them one by one
                    if(currentSchedule.processor2.get(k).getId() == currentSchedule.processor1.get(j).getPredecessor().get(s)){//check if predecessore number "s" is equal to the task in the second processor
                        if(currentSchedule.processor1.get(j).getStartTime() < currentSchedule.processor2.get(k).getFinshTime() && currentSchedule.processor1.get(j).getHeight() != 0){//update if start time is less than next task finish time

                            currentSchedule.processor1.get(j).setStartTime(currentSchedule.processor2.get(k).getFinshTime());
                            currentSchedule.processor1.get(j).setFinshTime(currentSchedule.processor1.get(j).getStartTime() + currentSchedule.processor1.get(j).getDuration());
                            break;
                        }
                    }
                  }
              }  
            }
        }
        

        // Processor-2
        for (int j = 0; j < currentSchedule.processor2.size(); j++) {
            if(currentSchedule.processor2.get(j).getHeight() == i){ // match
               // int lastDep = currentSchedule.processor2.get(j).getPredecessor().get(currentSchedule.processor2.get(j).getPredecessor().size()-1);
                
                for(int s = 0; s < currentSchedule.processor2.get(j).getPredecessor().size(); s++) {
                    for(int k = 0; k < currentSchedule.processor1.size(); k++){
                    if(currentSchedule.processor1.get(k).getId() == currentSchedule.processor2.get(j).getPredecessor().get(s)){
                        if(currentSchedule.processor2.get(j).getStartTime() < currentSchedule.processor1.get(k).getFinshTime() && currentSchedule.processor2.get(j).getHeight() != 0){//update

                            currentSchedule.processor2.get(j).setStartTime(currentSchedule.processor1.get(k).getFinshTime());
                            currentSchedule.processor2.get(j).setFinshTime(currentSchedule.processor2.get(j).getStartTime() + currentSchedule.processor2.get(j).getDuration());
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
    if(currentSchedule.processor1.size() == 0){
        getSchedFT = currentSchedule.processor2.get(currentSchedule.processor2.size() - 1).getFinshTime();
    }else if(currentSchedule.processor2.size() == 0){
        getSchedFT = currentSchedule.processor1.get(currentSchedule.processor1.size() - 1).getFinshTime();
    }else if(currentSchedule.processor1.get(currentSchedule.processor1.size()-1).getFinshTime()>currentSchedule.processor2.get(currentSchedule.processor2.size()-1).getFinshTime()) {
        getSchedFT = currentSchedule.processor1.get(currentSchedule.processor1.size() - 1).getFinshTime();
        return getSchedFT;
    }else{
        getSchedFT = currentSchedule.processor2.get(currentSchedule.processor2.size() - 1).getFinshTime();
        return getSchedFT;
    }
    return  getSchedFT;
}

//print all Tasks
    public void print() {
        System.out.println("Schedule Finish Time = "+sFT);
        System.out.println("Processor #1");
        for (int i = 0; i < processor1.size(); i++) {
            System.out.println("Task #["+processor1.get(i).getId()+"] Duration ["+processor1.get(i).getDuration()+
                    "] Height ["+processor1.get(i).getHeight()+"] Start Time ["+ processor1.get(i).getStartTime()
                    +"] Finish Time ["+processor1.get(i).getFinshTime()+"]\n" );
        }
            System.out.println("Processor #2 :\n");
        for (int j = 0; j < processor2.size(); j++) {
            System.out.println("Task #["+processor2.get(j).getId()+"] Duration ["+processor2.get(j).getDuration()+
                    "] Height ["+processor2.get(j).getHeight()+"] Start Time ["+ processor2.get(j).getStartTime()
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
