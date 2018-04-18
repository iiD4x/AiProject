package ai.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.MAX_VALUE;

public class AIProject {

    public static int number_of_tasks;
    public static int generationNum = 0;//generation number
    public static int check = MAX_VALUE ,i ;
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

//        ShowSchedules();

        callInitialTime();

        System.out.println("\n+++++++++FitnessFunction()+++++++++++");
        callFitnessFunction();

       // ShowSchedules();

        InitialPopulation(mainSchedule);//!@#$%^& need to double check




        do {
            ArrayList<Schedule> allSchedules = Selection(mainPopulation.get(generationNum));
            for (int i = 0; i < 50; i++) {
                int minHT=0,maxHT=49;
                Random randomNum = new Random();
                int crossoverSite1 = minHT + randomNum.nextInt(maxHT);
                int crossoverSite2 = minHT + randomNum.nextInt(maxHT);

                allSchedules.addAll(callCrossOver(allSchedules.get(crossoverSite1),allSchedules.get(crossoverSite2)));
            }
            // now allSchedules have all the ( crossedOver & selected ) schedules so we can generate a new population of it
            generateNewPopulation(allSchedules);
            generationNum++;

        }while( Loop(mainPopulation.get(generationNum-1)));    //checks if we should stop looping or not
        System.out.println("Solution for this population is :"+mainPopulation.get(mainPopulation.size()-1).getBestFT()+" in generationNum : "+generationNum);
    }

    //take schedules that are returned from Crossover & Selection method and add them to new Population
    private static void generateNewPopulation(ArrayList<Schedule> wholeSchedules ) {
        tempPopulation = new Population();
        tempPopulation.schedule.addAll(wholeSchedules);
        mainPopulation.add(tempPopulation);
    }

    /*finds the best found solution and compare it with the next populations until there isn't any other better solution
    for the next 100 generations*/
    private static boolean Loop(Population population) {
//        stop after 1000 iteration and there is no improvements in the finish time
        if (population.getBestFT() < check){
            check = population.getBestFT(); //if there is a better solution than the current solution
            i=0;
        }else {
            i++;
        }
        if(i == 1000){
            return false;
        }

        return true;
    }

    private static void InitialPopulation(ArrayList<Schedule> mainSchedule) {
        tempPopulation = new Population();
//        for (int i = 0; i < mainSchedule.size(); i++) { //to add all schedules of this generation to the population
//            tempPopulation.schedule.add(mainSchedule.get(i));   //fill the schedule inside the temporary population
//        }
        tempPopulation.schedule = mainSchedule;
        mainPopulation.add(tempPopulation);     //add the whole temporary population now
//        generationNum++;
    }

    private static ArrayList<Schedule> Selection(Population currentPopulation) {
        ArrayList<Schedule> s= new ArrayList<>();
        int z = (currentPopulation.schedule.size() /2);
        for(int i = 0; i < z ; i++) {           //loop as half of this population Schedule size ( generation )

            int totalSum = 0;

            for (int j = currentPopulation.schedule.size()-1; j >= 0 ; j--) {
                totalSum += currentPopulation.schedule.get(j).getsFT();
            }

            int rand = ThreadLocalRandom.current().nextInt(0, totalSum + 1);
            int partialSum = 0;

            for (int k = currentPopulation.schedule.size()-1; k >= 0 ; k--) {
                partialSum += currentPopulation.schedule.get(k).getsFT();
                if (partialSum >= rand) {       //!@#$%
                    s.add( currentPopulation.schedule.get(i));
                    break;      //finish this iteration and add this schedule then select another one again
                }
            }

        }
        return s;
    }

    public static void ReadFromFile() throws FileNotFoundException {
        String InstanceToRead="";
        System.out.println("Choose Instance file [1 for instance1 ,2 for instance2,3 for instance3,4 for TestSample]");
        Scanner readfiles = new Scanner(System.in);
        int ChoosedFile = readfiles.nextInt();
        if (ChoosedFile == 1) InstanceToRead = "Instance1.txt";
        else if (ChoosedFile==2) InstanceToRead = "Instance2.txt";
        else if (ChoosedFile==3) InstanceToRead = "Instance3.txt";
        else if (ChoosedFile==4) InstanceToRead = "sample.txt";
        else{
            System.out.println("Wrong input");
        }

        File readFromFile = new File(InstanceToRead);
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
                //preds.add(null);
            }
            if (!spaceSplit[1].equals("-")) {
                commaSplitSucs = spaceSplit[1].split(",");

                for (int i = 0; i < commaSplitSucs.length; i++) {
                    sucs.add(Integer.parseInt(commaSplitSucs[i]));
                }

            } else {
                // sucs.add(null);
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

    private static void Mutation(Schedule S1) {
        Task mutationTaskP1 = new Task(),mutationTaskP2 = new Task();
        int tempInx1 = 0,tempInx2 = 0;

        int minHT=0,maxHT=AIProject.ts.get(AIProject.ts.size()-1).getHight();
        Random randomNum = new Random();
        int mutationHeight = 0;
        boolean foundP1P2 = false;
        for (int k = 0; k < number_of_tasks && !foundP1P2; k++) {
            mutationHeight = minHT + randomNum.nextInt(maxHT);
            for (int i = 0; i < S1.processor1.size(); i++) {
                if(S1.processor1.get(i).getHight()== mutationHeight && !foundP1P2){
                    for (int j = 0; j < S1.processor2.size(); j++) {
                        if(S1.processor2.get(j).getHight()== mutationHeight) {
                            mutationTaskP1 = S1.processor1.remove(i);
                            tempInx1 = i;
                            mutationTaskP2 = S1.processor2.remove(j);
                            tempInx2 = j;
                            foundP1P2 = true;
                            break;
                        }
                    }
                }
            }
        }
        S1.processor1.add(tempInx1,mutationTaskP2);
        S1.processor2.add(tempInx2,mutationTaskP1);
    }

    private static void GenerateSchedules() {
        BubbleSort(ts);
        for (int j = 0; j < 100; j++) {
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
            if (mainSchedule.get(i).processor1.isEmpty()) {    //if this processor is empty
                mainSchedule.get(i).processor1.add(mainSchedule.get(i).processor2.remove(mainSchedule.get(i).processor2.size() - 1));

            } else if (mainSchedule.get(i).processor2.isEmpty()) {    //if this processor is empty
                mainSchedule.get(i).processor2.add(mainSchedule.get(i).processor1.remove(mainSchedule.get(i).processor1.size() - 1));
            }
        }

    }

    public static void ShowSchedules() {

        for (int j = 0; j < mainSchedule.size(); j++) {
            System.out.println("******************\nSchedule " + (j + 1) + " : ");
            System.out.println("Schedule Finish Time = "+mainSchedule.get(j).sFT);
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

    private static ArrayList<Schedule> callCrossOver(Schedule S1,Schedule S2) {
        int minHT=0,maxHT=AIProject.ts.get(AIProject.ts.size()-1).getHight();
        Random randomNum = new Random();
        int crossoverSite = minHT + randomNum.nextInt(maxHT);
        //System.out.println("The CrossOver Site : "+crossoverSite);
//        int maxSchedSize = selectedSchedules.size(); Maximum Size = PopSize/2
        ArrayList<Schedule> CrossOvedSchedules = new ArrayList<Schedule>();
        Schedule newSchedule1 = new Schedule();
        Schedule newSchedule2 = new Schedule();
        ArrayList<Task> crossOverS1P1Tasks = new ArrayList<Task>();
        ArrayList<Task> crossOverS1P2Tasks = new ArrayList<Task>();
        ArrayList<Task> crossOverS2P1Tasks = new ArrayList<Task>();
        ArrayList<Task> crossOverS2P2Tasks = new ArrayList<Task>();

            //The New Schedule 1 Processor 1 (Lower from S1 High from S2)
            for (int j = 0; j < S1.processor1.size(); j++){
                if (S1.processor1.get(j).getHight() <= crossoverSite) {
                    crossOverS1P1Tasks.add(S1.processor1.get(j));
                }
            }
            for (int j = 0; j < S2.processor1.size(); j++){
                if (S2.processor1.get(j).getHight() > crossoverSite) {
                    crossOverS1P1Tasks.add(S2.processor1.get(j));
                }
            }
            //The New Schedule 1 Processor 2 (Lower from S1 High from S2)
            for (int j = 0; j < S1.processor2.size(); j++){
                if (S1.processor2.get(j).getHight() <= crossoverSite) {
                    crossOverS1P2Tasks.add(S1.processor2.get(j));
                }
            }
            for (int j = 0; j < S2.processor2.size(); j++){
                if (S2.processor2.get(j).getHight() > crossoverSite) {
                    crossOverS1P2Tasks.add(S2.processor2.get(j));
                }
            }

            //The New Schedule 2 Processor 1 (Lower from S2 High from S1)
            for (int j = 0; j < S2.processor1.size(); j++){
                if (S2.processor1.get(j).getHight() <= crossoverSite) {
                    crossOverS2P1Tasks.add(S2.processor1.get(j));
                }
            }
            for (int j = 0; j < S1.processor1.size(); j++){
                if (S1.processor1.get(j).getHight() > crossoverSite) {
                    crossOverS2P1Tasks.add(S1.processor1.get(j));
                }
            }
            //The New Schedule 2 Processor 2 (Lower from S2 High from S1)
            for (int j = 0; j < S2.processor2.size(); j++){
                if (S2.processor2.get(j).getHight() <= crossoverSite) {
                    crossOverS2P2Tasks.add(S2.processor2.get(j));
                }
            }
            for (int j = 0; j < S1.processor2.size(); j++){
                if (S1.processor2.get(j).getHight() > crossoverSite) {
                    crossOverS2P2Tasks.add(S1.processor2.get(j));
                }
            }

        //Adding processes into new schedules
        newSchedule1.processor1 = crossOverS1P1Tasks;
        newSchedule1.processor2 = crossOverS1P2Tasks;
        newSchedule2.processor1 = crossOverS2P1Tasks;
        newSchedule2.processor2 = crossOverS2P2Tasks;
        CrossOvedSchedules.add(newSchedule1);
        CrossOvedSchedules.add(newSchedule2);

        return CrossOvedSchedules;
    }

    private static void callFitnessFunction() {
        for (int i = 0; i < mainSchedule.size(); i++) {
//            System.out.println("******************\nSchedule " + (i + 1));
            mainSchedule.get(i).initialTime();
//            mainSchedule.get(i).FitnessFunction();
            mainSchedule.get(i).setSchedFinishTime(mainSchedule.get(i).FitnessFunction());
//            mainSchedule.get(i).print();
        }
    }
}
