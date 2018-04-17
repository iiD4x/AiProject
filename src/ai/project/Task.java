
package ai.project;

import java.util.ArrayList;
import java.util.*;

public class Task {
// add task ID    
public ArrayList<Integer> predecessor =new ArrayList<>();
public ArrayList<Integer> successor = new ArrayList<>() ;
private int duration; //execution time
private int hight;
private int startTime , finshTime;
private int id;
public boolean done ;   //to indecate if the task in the process has been finished or not yet

//  constructor

    public Task(ArrayList<Integer> pre, ArrayList<Integer> suc, int duration, int hight , int id) {
        this.done = true;
        this.startTime = 0;
        this.finshTime = 0;
        this.predecessor = pre;
        this.successor = suc;
        this.duration = duration;
        this.hight = hight;
        this.id = id;
    }

    Task() {
       //initially does nothing
    }

//  setters

    public void setSuccessor(Integer successor) {
        this.successor.add(successor);
    }

    public void setPredecessor(Integer predecessor) {
        this.predecessor.add(predecessor);
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFinshTime(int finshTime) {
        this.finshTime = finshTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    
    public void setDone(boolean t){
        this.done = t;
    }

// getters
    public String printPredecessore(){
        String p = "";
        for (int i = 0; i < predecessor.size(); i++) {
           // System.out.print(predecessor.get(i)+",");
           p +=  String.valueOf(predecessor.get(i))+" ";
        }
        return p;
    }
    
    public String printSuccessores(){
        String s = " ";
        for (int i = 0; i < successor.size(); i++) {
           // System.out.print(predecessor.get(i)+",");
           s += String.valueOf(successor.get(i)) +" ";
        }
        return s;
    }
    
    public int getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public int getHight() {
        return hight;
    }

    public ArrayList<Integer> getPredecessor() {
        return predecessor;
    }

    public ArrayList<Integer> getSuccessor() {
        return successor;
    }

    public int getFinshTime() {
        return finshTime;
    }

    public int getStartTime() {
        return startTime;
    }
    
    public boolean getDone(){
        return this.done;
    }
    
}
