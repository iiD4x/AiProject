package ai.project;

import com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.MAX_VALUE;
/*
* Project Tilte : A GENETIC ALGORITHM FOR MULTIPROCESSOR SCHEDULING PROBLEM
*
* Authors Abdullah Alamin ( @abodamin0 )& Ahmed Maaruf ( @iiD4x )
*
* SUPERVISED BY : Prof. Hachemi Bennaceur
*
* this project is for AI ccis 330 course in IMAM University
*
* April 21 2018
*
*
* */
public class AIProject {

    public static int number_of_tasks;
    public static int generationNum = 0;//generation number
    public static int check = MAX_VALUE, i = 0;
    public static Task T;
    public static ArrayList<Task> ts = new ArrayList<>();

    public static ArrayList<Integer> preds, sucs;

    public static Schedule tempSchedule;
    public static ArrayList<Schedule> mainSchedule = new ArrayList<>();

    public static String[] spaceSplit, commaSplitPreds, commaSplitSucs;
    public static int testD, testH, randomNum;

    public static Population tempPopulation;
    public static ArrayList<Population> mainPopulation = new ArrayList<>();
    public static double randomNumDouble;
    private static int bestGeneration=0;

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws FileNotFoundException, IOException {

        ReadFromFile();

        GenerateSchedules();
        callFitnessFunction(mainSchedule);
//        for (int j = 0; j <mainSchedule.size() ; j++) {
//            System.out.println("SchedNum "+(j+1));
//            mainSchedule.get(j).print();
//        }

        InitialPopulation(mainSchedule);//!@#$%^& need to double check
//        mainPopulation.get(0).print();
        do {

            //SELECTION
            ArrayList<Schedule> allSchedules = new ArrayList<Schedule>();
            allSchedules = Selection(mainPopulation.get(generationNum));

            //CROSSOVER
            for (int i = 0; i < 25; i++) {
                ArrayList<Schedule> Crossoved = new ArrayList<Schedule>();
                int minSched = 0, maxSched = 49;
                Random randomNum = new Random();
                int randomSched1 = minSched + randomNum.nextInt(maxSched);
                int randomSched2 = minSched + randomNum.nextInt(maxSched);
                if (randomSched1 == randomSched2) {
                    randomSched2 += 1;
                }
                Crossoved.addAll(callCrossOver(allSchedules.get(randomSched1), allSchedules.get(randomSched2)));
                callFitnessFunction(Crossoved);
                for (int j = 0; j < Crossoved.size(); j++) {
                    randomNumDouble = ThreadLocalRandom.current().nextDouble(0, 1);
                    if(randomNumDouble<0.003){
                        Crossoved.add(j,Mutation(allSchedules.remove(j)));
                    }
                }
                allSchedules.addAll(Crossoved);

            }

            // now allSchedules have all the ( crossedOver & selected ) schedules so we can generate a new population of it
            //Calculate Fitness
            callFitnessFunction(allSchedules);

            //INSERT THE NEW POPULATION
            generateNewPopulation(allSchedules);
            generationNum++;
        } while (Loop(mainPopulation.get(generationNum - 1)));    //checks if we should stop looping or not
//        mainPopulation.get(bestGeneration-1).print();
        System.out.println("Best Finish Time on this run = " + mainPopulation.get(bestGeneration-1).getBestFT() + "\nin Generation number: " + bestGeneration);
        ShowSchedules(mainPopulation.get(bestGeneration-1).schedule);
    }

    //take schedules that are returned from Crossover & Selection method and add them to new Population
    private static void generateNewPopulation(ArrayList<Schedule> wholeSchedules) {
        tempPopulation = new Population();
        tempPopulation.schedule.addAll(wholeSchedules);
        mainPopulation.add(tempPopulation);
    }

    /*finds the best found solution and compare it with the next populations until there isn't any other better solution
    for the next 100 generations*/
    private static boolean Loop(Population population) {
//        stop after 1000 iteration and there is no improvements in the finish time
        if (population.getBestFT() < check) {
            check = population.getBestFT(); //if there is a better solution than the current solution
            bestGeneration = generationNum;
            i = 0;
        } else {
            i++;
        }
        if (i == 2000) {
            return false;
        }

        return true;
    }

    private static void InitialPopulation(ArrayList<Schedule> mainSchedule) {
        tempPopulation = new Population();
        tempPopulation.schedule = mainSchedule;
        mainPopulation.add(tempPopulation);     //add the whole temporary population now
        System.out.println();
    }

    private static ArrayList<Schedule> Selection(Population currentPopulation) {
        ArrayList<Schedule> selectedSchedules = new ArrayList<>();
        int z = (currentPopulation.schedule.size() / 2);
        for (int i = 0; i < 50; i++) {           //loop as half of this population Schedule size ( generation )

            int totalSum = 0;

            for (int j = currentPopulation.schedule.size() - 1; j >= 0; j--) {
                totalSum += currentPopulation.schedule.get(j).getsFT();
            }

            int rand = ThreadLocalRandom.current().nextInt(0, totalSum + 1);
            int partialSum = 0;

            for (int k = currentPopulation.schedule.size() - 1; k >= 0; k--) {
                partialSum += currentPopulation.schedule.get(k).getsFT();
                if (partialSum >= rand) {       //!@#$%
                    selectedSchedules.add(currentPopulation.schedule.get(i));
                    break;      //finish this iteration and add this schedule then select another one again
                }
            }

        }
        return selectedSchedules;
    }

    public static void ReadFromFile() throws FileNotFoundException {
        String InstanceToRead = "";
        System.out.println("Choose Instance file \n(1) for instance1" +
                "\n(2) for instance2" +
                "\n(3) for instance3" +
                "\n(4) for TestSample");
        Scanner readfiles = new Scanner(System.in);
        int ChoosedFile = readfiles.nextInt();
        switch (ChoosedFile) {
            case 1:
                InstanceToRead = "Instance1.txt";
                break;
            case 2:
                InstanceToRead = "Instance2.txt";
                break;
            case 3:
                InstanceToRead = "Instance3.txt";
                break;
            case 4:
                InstanceToRead = "sample.txt";
                break;
            default:
                System.out.println("Wrong input");
                break;
        }
        System.out.println("+++++ Reading from: "+InstanceToRead+" +++++");
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
                if (array.get(j).getHeight() < array.get(j - 1).getHeight()) {

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
//            System.out.println("\nTask Num #"+(k+1)+" : "+array.get(k).getName()+" , Height Of "+array.get(k).getHeight());
//        }
    }

    private static Schedule Mutation(Schedule S1) {
        Task mutationTaskP1 = new Task(), mutationTaskP2 = new Task();
        int tempInx1 = 0, tempInx2 = 0;

        int minHT = 0, maxHT = AIProject.ts.get(AIProject.ts.size() - 1).getHeight();
        Random randomNum = new Random();
        int mutationHeight = 0;
        boolean foundP1P2 = false;
        for (int k = 0; k < number_of_tasks && !foundP1P2; k++) {
            mutationHeight = minHT + randomNum.nextInt(maxHT);
            for (int i = 0; i < S1.processor1.size(); i++) {
                if (S1.processor1.get(i).getHeight() == mutationHeight && !foundP1P2) {
                    for (int j = 0; j < S1.processor2.size(); j++) {
                        if (S1.processor2.get(j).getHeight() == mutationHeight) {
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
        S1.processor1.add(tempInx1, mutationTaskP2);
        S1.processor2.add(tempInx2, mutationTaskP1);
        return S1;
    }

    private static void GenerateSchedules() {
        BubbleSort(ts);
        Task old, temp;
        for (int j = 0; j < 100; j++) {
            tempSchedule = new Schedule();
            for (int i = 0; i < ts.size(); i++) {
                randomNum = ThreadLocalRandom.current().nextInt(1, 3);
                old = ts.get(i);
                temp = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                temp.setDone(old.getDone());
                temp.setStartTime(old.getStartTime());
                temp.setFinshTime(old.getFinshTime());
                if (randomNum == 1) {
                    tempSchedule.processor1.add(temp);
                } else {
                    tempSchedule.processor2.add(temp);
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

    public static void ShowSchedules(ArrayList<Schedule> bestScd) {

        for (int j = 0; j < bestScd.size(); j++) {
            if(mainPopulation.get(bestGeneration-1).getBestFT()==bestScd.get(j).sFT) {
            System.out.println("\n******************\nSchedule " + (j + 1) + " Generation number "+bestGeneration+" :");
//                System.out.println("Schedule Finish Time = " + bestScd.get(j).sFT);
                System.out.print("P1 : ");
                for (int i = 0; i < bestScd.get(j).processor1.size(); i++) {
                    System.out.print("[" + bestScd.get(j).processor1.get(i).getId() + "]");

                }
                System.out.println("");
                System.out.print("P2 : ");
                for (int i = 0; i < bestScd.get(j).processor2.size(); i++) {
                    System.out.print("[" + bestScd.get(j).processor2.get(i).getId() + "]");
                }
                System.out.println("");
                break;
            }
        }
    }

    private static ArrayList<Schedule> callCrossOver(Schedule S1, Schedule S2) {
        int minHT = 0, maxHT = AIProject.ts.get(AIProject.ts.size() - 1).getHeight();
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

        Task old, new1;
        //The New Schedule 1 Processor 1 (Lower from S1 High from S2)
        for (int j = 0; j < S1.processor1.size(); j++) {
            old = S1.processor1.get(j);
            if (old.getHeight() <= crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS1P1Tasks.add(new1);
//                crossOverS1P1Tasks.add(S1.processor1.get(j));
            }
        }
        for (int j = 0; j < S2.processor1.size(); j++) {
            old = S2.processor1.get(j);
            if (old.getHeight() > crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS1P1Tasks.add(new1);
//                crossOverS1P1Tasks.add(S2.processor1.get(j));
            }
        }
        //The New Schedule 1 Processor 2 (Lower from S1 High from S2)
        for (int j = 0; j < S1.processor2.size(); j++) {
            old = S1.processor2.get(j);
            if (old.getHeight() <= crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS1P2Tasks.add(new1);
//                crossOverS1P2Tasks.add(S1.processor2.get(j));
            }
        }
        for (int j = 0; j < S2.processor2.size(); j++) {
            old = S2.processor2.get(j);
            if (old.getHeight() > crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS1P2Tasks.add(new1);
//                crossOverS1P2Tasks.add(S2.processor2.get(j));
            }
        }

        //The New Schedule 2 Processor 1 (Lower from S2 High from S1)
        for (int j = 0; j < S2.processor1.size(); j++) {
            old = S2.processor1.get(j);
            if (old.getHeight() <= crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS2P1Tasks.add(new1);
//                crossOverS2P1Tasks.add(S2.processor1.get(j));
            }
        }
        for (int j = 0; j < S1.processor1.size(); j++) {
            old = S1.processor1.get(j);
            if (old.getHeight() > crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS2P1Tasks.add(new1);
//                crossOverS2P1Tasks.add(S1.processor1.get(j));
            }
        }
        //The New Schedule 2 Processor 2 (Lower from S2 High from S1)
        for (int j = 0; j < S2.processor2.size(); j++) {
            old = S2.processor2.get(j);
            if (old.getHeight() <= crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS2P2Tasks.add(new1);
//                crossOverS2P2Tasks.add(S2.processor2.get(j));
            }
        }
        for (int j = 0; j < S1.processor2.size(); j++) {
            old = S1.processor2.get(j);
            if (old.getHeight() > crossoverSite) {
                new1 = new Task(old.getPredecessor(), old.getSuccessor(), old.getDuration(), old.getHeight(), old.getId());
                new1.setStartTime(old.getStartTime());
                new1.setFinshTime(old.getFinshTime());
                new1.setDone(old.getDone());
                crossOverS2P2Tasks.add(new1);
//                crossOverS2P2Tasks.add(S1.processor2.get(j));
            }
        }

        //Adding processes into new schedules
        newSchedule1.processor1 = crossOverS1P1Tasks;
        newSchedule1.processor2 = crossOverS1P2Tasks;
        newSchedule2.processor1 = crossOverS2P1Tasks;
        newSchedule2.processor2 = crossOverS2P2Tasks;
        CrossOvedSchedules.add(newSchedule1);
        CrossOvedSchedules.add(newSchedule2);
        callFitnessFunction(CrossOvedSchedules);
        return CrossOvedSchedules;
    }

    private static void callFitnessFunction(ArrayList<Schedule> currentSchedule) {
        for (int i = 0; i < currentSchedule.size(); i++) {
//            System.out.println("SchedNum "+(i+1));
            currentSchedule.get(i).initialTime();
//            mainSchedule.get(i).FitnessFunction();
            int schedFinTime = currentSchedule.get(i).FitnessFunction(currentSchedule.get(i));
            currentSchedule.get(i).setSchedFinishTime(schedFinTime);
//            currentSchedule.get(i).print();
        }
    }
}
