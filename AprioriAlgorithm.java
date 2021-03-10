import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AprioriAlgorithm {
    // main data set
    private List<Set<String>> transactions = new ArrayList<>();
    private static int itemCount;

    private static double minSupportValue; 
    private static double minConfValue;


    public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter a number between 1-5(inclusive) to indicate data set to use:");

        String dataSetNum = myObj.nextLine();  // Read user input
        int dataSetNumInt = Integer.parseInt(dataSetNum);
        System.out.println("Okay. Using dataSet number: " + dataSetNum);  // Output user input
        // prompt for support
        System.out.println("Please enter min support value in percentage.\nFor ex: for 60% just say 60");
        String minSupport = myObj.nextLine();  // Read user input
        minSupportValue = Double.parseDouble(minSupport);
        // prompt for confidence
        System.out.println("Please enter min confidence value in percentage.\nFor ex: for 60% just say 60");
        String minConf = myObj.nextLine();  // Read user input
        minConfValue = Double.parseDouble(minConf);

        System.out.println("Min Support Value: " + minSupportValue + "\nMin Conf Value: " + minConfValue );
        Instant apStart = Instant.now();
        AprioriAlgorithm obj = new AprioriAlgorithm();
//        obj.setup(5);
        obj.setup(dataSetNumInt);

        Instant apEnd = Instant.now(); 

        Instant bfStart = Instant.now();
        BruteForce bf = new BruteForce();

        bf.init(dataSetNumInt, minSupportValue, minConfValue);
//        bf.init(5, minSupportValue, minConfValue);
        Instant bfEnd = Instant.now();
        long apDiff = Duration.between(apStart, apEnd).toMillis();
        long bfDiff = Duration.between(bfStart, bfEnd).toMillis();
        System.out.println("\nApriori Time: " + apDiff + " ms");
        System.out.println("Brute force Time: " + bfDiff + " ms");
        long diff = bfDiff-apDiff;
        System.out.println("Apriori is " + diff + " ms faster then BruteForce.");
        System.exit(0);
    }

    private void setup(int dataSetNum) {

        // one map for all support count set
        Map<Set<String>, Integer> supportCountMap = new HashMap<>();

        // store data into local variable
        readFilesAndCreateOneSetMapEntries(dataSetNum, supportCountMap);
//        System.out.println(transactions.toString());
//        System.out.println(supportCountMap.size());
//        for(Set<String> s : supportCountMap.keySet()) {
//            System.out.println("Set:" + s.toString() + "values: " + supportCountMap.get(s));
//        }

        // filter the one item set
        List<Set<String>> freqItemList = generateFirstFilterSet(supportCountMap);

        // filtered lists of sets for kth size set
        Map<Integer, List<Set<String>>> filterSetMap = new HashMap<>();
        filterSetMap.put(1, freqItemList);

        int k = 1;
        do {
            k++;
            List<Set<String>> candidateList = generateNextCandidateSet(filterSetMap.get(k-1));
            for (Set<String> transaction : transactions) {
                List<Set<String>> candidateList2 = checkExistanceOfSetInTransaction(candidateList, transaction);

                for (Set<String> itemSet : candidateList2) {
                    supportCountMap.put(itemSet, supportCountMap.getOrDefault(itemSet, 0) + 1);
                }
            }

            filterSetMap.put(k, getNextItemSets(candidateList, supportCountMap));

        } while (!filterSetMap.get(k).isEmpty());

//        for(int i : filterSetMap.keySet()) {
//            for(Set<String> s : filterSetMap.get(i)) {
//                System.out.println("map entries: key " + i + " value: " + s.toString() + " count: " + supportCountMap.get(s));
//            }
//        }
        System.out.println("\n------Apriori Output--------\n");
        AssociationRuleMining associationRuleMining = new AssociationRuleMining();
        List<AssociationRule> rules = associationRuleMining.computeAssociationRules(filterSetMap, supportCountMap, minConfValue);
        for(AssociationRule r : rules) {
            System.out.println(r.getAntecedent() + " -> " + r.getConsequent() + " Support Value: " + r.getSourceSetSupportValue() + " Confidence value: " + r.getConfidence());
        }

    }

    private void readFilesAndCreateOneSetMapEntries(int dataSetNum, Map<Set<String>, Integer> supportCountMap) {
        try {
            File input = new File("Dataset" + dataSetNum +  " - Sheet1.csv");
            Scanner table = new Scanner(input);
            System.out.println("\n-------Input data set------");
            while (table.hasNextLine()) {
                String line = table.nextLine();
                System.out.println(line);
                String[] tokens = line.split(",");
                HashSet<String> set = new HashSet<>();
                for (String s : tokens) {
                    set.add(s);
                    HashSet<String> temp = new HashSet<>();
                    temp.add(s);
                    supportCountMap.put(temp, supportCountMap.getOrDefault(temp, 0) + 1);
                }
                transactions.add(set);
            }
            table.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private List<Set<String>> generateFirstFilterSet(Map<Set<String>, Integer> supportCountMap) {
        List<Set<String>> freqItemList = new ArrayList<>();
        for(Map.Entry<Set<String>, Integer> entry : supportCountMap.entrySet()) {
            double ratio = (double) entry.getValue() / transactions.size();
            ratio *= 100;
            if(ratio >= minSupportValue) {
                freqItemList.add(entry.getKey());
            }
        }
        return freqItemList;
    }

    private List<Set<String>> getNextItemSets(List<Set<String>> candidateList, Map<Set<String>, Integer> supportCountMap) {
        List<Set<String>> res = new ArrayList<>(candidateList.size());

        for (Set<String> itemSet : candidateList) {
            if (supportCountMap.containsKey(itemSet)) {
                double ratio = (double)supportCountMap.get(itemSet) / transactions.size();
                ratio *= 100;
                if (ratio >= minSupportValue) {
                    res.add(itemSet);
                }
            }
        }
        return res;
    }

    private List<Set<String>> checkExistanceOfSetInTransaction(List<Set<String>> candidateList, Set<String> transaction) {
        List<Set<String>> res = new ArrayList<>(candidateList.size());

        for (Set<String> c : candidateList) {
            if (transaction.containsAll(c)) {
                res.add(c);
            }
        }
        return res;
    }

    private List<Set<String>> generateNextCandidateSet(List<Set<String>> prevList) {
        List<List<String>> list = new ArrayList<>(prevList.size());

        for (Set<String> itemSet : prevList) {
            List<String> temp = new ArrayList<>(itemSet);
            Collections.sort(temp);
            list.add(temp);
        }

        int listSize = list.size();

        List<Set<String>> res = new ArrayList<>();

        for(int i=0; i< listSize; i++) {
            for(int j=i+1; j< listSize; j++) {
                Set<String> candidate = tryMergingItemSets(list.get(i), list.get(j));
                if (candidate != null) {
                    res.add(candidate);
                }
            }
        }
        return res;
    }

    private Set<String> tryMergingItemSets(List<String> itemSet1, List<String> itemSet2) {
        int length = itemSet1.size();

        for (int i = 0; i < length - 1; ++i) {
            if (!itemSet1.get(i).equals(itemSet2.get(i))) {
                return null;
            }
        }

        if (itemSet1.get(length - 1).equals(itemSet2.get(length - 1))) {
            return null;
        }

        Set<String> res = new HashSet<>(length + 1);

        for (int i = 0; i < length - 1; ++i) {
            res.add(itemSet1.get(i));
        }

        res.add(itemSet1.get(length - 1));
        res.add(itemSet2.get(length - 1));
        return res;
    }
}
