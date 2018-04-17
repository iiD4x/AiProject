package ai.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class AIProject {

    public static int number_of_tasks;
    public static int generationNum = 0;//generation number
    public static Task T;
    public static ArrayList<Task> ts = new ArrayList<>();

    public static ArrayList<Integer> preds, sucs;

    public static Schedule tempSchedule;
    public static ArrayList<Schedule> mainSchedule = new ArrayList<>();

    public static String[] spaceSplit, commaSplitPreds, commaSplitSucs;
    public static int testD, testH, randomNum;

    public static Population tempPopulation;
    public static ArrayList<Population> mainPopulation = new ArrayList<>();

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws FileNotFoundException, IOException {

        ReadFromFile();

        GenerateSchedules();
        ShowSchedules();
        callInitialTime();
        System.out.println("\n+++++++++FitnessFunction()+++++++++++");
        callFitnessFunction();

        // ShowSchedules();
        addToPopulation();//!@#$%^& need to double check

        do {

            Selection(mainPopulation.get(generationNum));
            //crossOver
            //add to next generation
            //generationNum++
            //check if we should stop or not

        }while(generationNum == 0);
    }

    private static void addToPopulation() {
        tempPopulation = new Population();
        for (int i = 0; i < mainSchedule.size(); i++) { //to add all schedules of this generation to the population
            tempPopulation.schedule.add(mainSchedule.get(i));   //fill the schedule inside the temporary population
        }
        mainPopulation.add(tempPopulation);     //add the whole population now

    }

    private static ArrayList<Schedule> Selection(Population mainPopulation) {
        ArrayList<Schedule> s= new ArrayList<>();
        for(int i = 0; i <(mainPopulation.schedule.size() /2); i++) {   //loop as half of this population ( generation )

            int totalSum = 0;

            for (int j = 0; j < mainPopulation.schedule.size(); j++) {
                totalSum += mainPopulation.schedule.get(j).getsFT();
            }

            int rand = ThreadLocalRandom.current().nextInt(0, totalSum + 1);
            int partialSum = 0;

            for (int k = 0; k < mainPopulation.schedule.size(); k++) {
                partialSum += mainPopulation.schedule.get(k).getsFT();
                if (partialSum >= rand) {       //!@#$%
                    s.add( mainPopulation.schedule.get(i));
                    break;      //finish this iteration and add this schedule then select another one again
                }
            }

        }
        return s;
    }

    public static void ReadFromFile() throws FileNotFoundException {
        String In1 = "Instance1.txt";
        String In2 = "Instance2.txt";
        String In3 = "Instance3.txt";
        String TestSample = "sample.txt";
        File readFromFile = new File(In1);
        Scanner readsc = new Scanner(readFromFile);

        number_of_tasks = readsc.nextInt();     //read scanner
        readsc.nextLine();
        int ii = 1;

        while (readsc.hasNext()) {
            preds = new ArrayList<>();
            sucs = new ArrayList<>();

            spaceSplit = readsc.nextLine().split(" ");      //split line according to spaces

            if (!spaceSplit[0].equals("-")) {
                commaSplitPreds = spaceSplit[0].split(",");

                for (int i = 0; i < commaSplitPreds.length; i++) {
                    preds.add(Integer.parseInt(commaSplitPreds[i]));
                }

            } else {
                //preds.add(null);      //if it has no predecessors then do nothing
            }
            if (!spaceSplit[1].equals("-")) {
                commaSplitSucs = spaceSplit[1].split(",");

                for (int i = 0; i < commaSplitSucs.length; i++) {
                    sucs.add(Integer.parseInt(commaSplitSucs[i]));
                }

            } else {
                // sucs.add(null);      //if it has no successores then do nothing
            }

            testD = Integer.parseInt(spaceSplit[2]);
            testH = Integer.parseInt(spaceSplit[3]);
            int id = ii;
            //String name = String.valueOf((ii+100)); // names are just indexes of tasks plus 100
            T = new Task(preds, sucs, testD, testH, id);

            ts.add(T);
            ii++;
        }

    }

    public static void BubbleSort(ArrayList<Task> array) {
        Task temp;
        boolean sorted = true;
        for (int i = 0; i < array.size() - 1 && sorted; i++) {
            sorted = false;
            //System.out.println("loop 1");
            for (int j = array.size() - 1; j > i; j--) {
                //  System.out.println("loop 2");
                if (array.get(j).getHight() < array.get(j - 1).getHight()) {

                    temp = array.get(j - 1);
                    array.set(j - 1, array.get(j));
                    array.set(j, temp);
                    sorted = true;
                }
            }

        }

        // assign IDs for all Tasks in ts
        for (int i = 0; i < ts.size(); i++) {
            ts.get(i).setId(i + 1);
        }

//           for(int k=0; k<array.size(); k++){
//            System.out.println("\nTask Num #"+(k+1)+" : "+array.get(k).getName()+" , Height Of "+array.get(k).getHight());
//        }
    }

    private static void GenerateSchedules() {
        BubbleSort(ts);
        for (int j = 0; j < number_of_tasks; j++) {       //nCr
            tempSchedule = new Schedule();
            for (int i = 0; i < ts.size(); i++) {
                randomNum = ThreadLocalRandom.current().nextInt(1, 3);
                if (randomNum == 1) {
                    tempSchedule.processor1.add(ts.get(i));
                } else {
                    tempSchedule.processor2.add(ts.get(i));
                }
            }
            mainSchedule.add(tempSchedule);
        }
        for (int i = 0; i < mainSchedule.size(); i++) {
            if (mainSchedule.get(i).processor1.isEmpty()) {    //if this gene is empty
                mainSchedule.get(i).processor1.add(mainSchedule.get(i).processor2.remove(mainSchedule.get(i).processor2.size() - 1));

            } else if (mainSchedule.get(i).processor2.isEmpty()) {    //if this gene is empty
                mainSchedule.get(i).processor2.add(mainSchedule.get(i).processor1.remove(mainSchedule.get(i).processor1.size() - 1));
            }
        }

    }

    public static void ShowSchedules() {

        for (int j = 0; j < mainSchedule.size(); j++) {
            System.out.println("******************\nSchedule " + (j + 1) + " : ");
            System.out.print("P1 : ");
            for (int i = 0; i < mainSchedule.get(j).processor1.size(); i++) {
                System.out.print("["+mainSchedule.get(j).processor1.get(i).getId()+"]");

            }
            System.out.println("");
            System.out.print("P2 : ");
            for (int i = 0; i < mainSchedule.get(j).processor2.size(); i++) {
                System.out.print("["+mainSchedule.get(j).processor2.get(i).getId()+"]");
            }
            System.out.println("");
        }
    }

    private static void callInitialTime() {
        for (int i = 0; i < mainSchedule.size(); i++) {
//            System.out.println("\tSchedule " + (i + 1));
            mainSchedule.get(i).initialTime();
//            mainSchedule.get(i).print();
        }
    }

    private static void callFitnessFunction() {
        for (int i = 0; i < mainSchedule.size(); i++) {
            System.out.println("******************\nSchedule " + (i + 1));
            mainSchedule.get(i).initialTime();
//            mainSchedule.get(i).FitnessFunction();
            mainSchedule.get(i).setSchedFinishTime(mainSchedule.get(i).FitnessFunction());      //calculate finish time then set it to schedule finish time
            mainSchedule.get(i).print();
        }
    }
}
