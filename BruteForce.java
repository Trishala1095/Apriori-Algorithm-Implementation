import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BruteForce {

    private List<Set<String>> transactions = new ArrayList<>();

    public void init(int dataSetNum, double minSupport, double minConfValue) {
        Set<String> initialSet = new HashSet<>();
        readFilesAndCreateOneSetMapEntries(dataSetNum, initialSet);
//        System.out.println(initialSet);
        List<List<String>> subsetList = findAllPossibleSubsets(new ArrayList<>(initialSet));
//        System.out.println("size: " + subsetList.size());
//        for(List<String> s: subsetList) {
//            System.out.println(s.toString());
//        }
        Map<Set<String>, Integer> supportCountMap = new HashMap<>();
        initializeSupportCountMap(subsetList, supportCountMap);

        calculateSupportCounts(supportCountMap);
//        System.out.println("---------Map Entries-------- ");
//        for(Set<String> s : supportCountMap.keySet()) {
//            System.out.println(s.toString() + " - " + supportCountMap.get(s));
//        }

        Map<Integer, List<Set<String>>> filterSetMap = new HashMap<>();
        filterUsingMinSupport(filterSetMap, supportCountMap, minSupport);
//        for(int s : filterSetMap.keySet()) {
//            for(Set<String> tmp : filterSetMap.get(s)) {
//                System.out.println(tmp.toString() + " - " + supportCountMap.get(tmp));
//            }
//        }
        System.out.println("\n------BruteForce Output--------\n");
        AssociationRuleMining associationRuleMining = new AssociationRuleMining();
        List<AssociationRule> rules = associationRuleMining.computeAssociationRules(filterSetMap, supportCountMap, minConfValue);
        for(AssociationRule r : rules) {
            System.out.println(r.getAntecedent() + " -> " + r.getConsequent() + " Support Value: " + r.getSourceSetSupportValue() + "  Confidence value: " + r.getConfidence());
        }
    }


    private void readFilesAndCreateOneSetMapEntries(int dataSetNum, Set<String> itemSet) {
        try {
            File input = new File("Dataset" + dataSetNum +  " - Sheet1.csv");
            Scanner table = new Scanner(input);
//            System.out.println("Input data set:");
            while (table.hasNextLine()) {
                String line = table.nextLine();
//                System.out.println(line);
                String[] tokens = line.split(",");
                HashSet<String> set = new HashSet<>();
                for (String s : tokens) {
                    set.add(s);
                    itemSet.add(s);
                }
                transactions.add(set);
            }
            table.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void initializeSupportCountMap(List<List<String>> subsetList, Map<Set<String>, Integer> supportCountMap) {
        for(List<String> list : subsetList) {
            Set<String> s = new HashSet<>(list);
            supportCountMap.put(s, 0);
        }
    }

    private void calculateSupportCounts(Map<Set<String>, Integer> supportCountMap) {

        for(Set<String> set : supportCountMap.keySet()) {
            for(Set<String> transaction: transactions) {
                if(transaction.containsAll(set)) {
                    supportCountMap.put(set, supportCountMap.get(set)+1);
                }
            }
        }
    }

    private List<List<String>> findAllPossibleSubsets(List<String> list) {
        List<List<String>> ans = new ArrayList<>();
        helper(ans, new LinkedList<>(), list, 0);
        return ans;
    }

    private void filterUsingMinSupport(Map<Integer, List<Set<String>>> filterSetMap, Map<Set<String>, Integer> supportCountMap, double minSupport) {

        for(Set<String> s : supportCountMap.keySet()) {
            double ratio = ((double) supportCountMap.get(s) / transactions.size()) * 100;
            if(ratio > minSupport) {
                List<Set<String>> list = filterSetMap.computeIfAbsent(s.size(), k -> new ArrayList<>());
                list.add(s);
                filterSetMap.put(s.size(), list);
            }
        }
    }

    private void helper(List<List<String>> ans, LinkedList<String> curr, List<String> list, int s) {
        if(curr.size()!=0 && curr.size()!=list.size()) {
            ans.add(new ArrayList<>(curr));
        }
        for(int i=s; i<list.size(); i++) {
            curr.addLast(list.get(i));
            helper(ans, curr, list, i+1);
            curr.removeLast();
        }
    }
}
