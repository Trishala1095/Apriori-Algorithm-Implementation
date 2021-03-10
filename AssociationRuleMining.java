import java.util.*;

public class AssociationRuleMining {

    public List<AssociationRule> computeAssociationRules(Map<Integer, List<Set<String>>> filterSetMap, Map<Set<String>, Integer> supportCountMap, double minConf) {

        List<AssociationRule> rules = new ArrayList<>();

        for(int key : filterSetMap.keySet()) {
            if(key<2) {
                continue;
            }
//            if(key==3)
            for(Set<String> s : filterSetMap.get(key)) {
                List<List<String>> subsetLists = findAllPossibleSubsets(new ArrayList<>(s));
                // convert these list into sets
                List<Set<String>> subsets = convertListToSet(subsetLists);
                computeAssociations(rules, subsets, supportCountMap, s, minConf);
            }
        }
        return rules;
    }

    private void computeAssociations(List<AssociationRule> res, List<Set<String>> subsetsList, Map<Set<String>, Integer> supportCountMap, Set<String> s, double minConf) {

        for(int i=0; i<subsetsList.size(); i++) {
            for(int j=i+1; j<subsetsList.size(); j++) {
                if(subsetsList.get(i).size()+subsetsList.get(j).size()!=s.size()) continue;

                boolean stop= false;
                for (String value : subsetsList.get(i)) {
                    if (subsetsList.get(j).contains(value)) {
                        stop = true;
                        break;
                    }
                }
                if(stop) continue;
                double conf = ((double) supportCountMap.get(s) / supportCountMap.get(subsetsList.get(i))) * 100;
                if(conf >= minConf) {
                    AssociationRule rule = new AssociationRule();
                    rule.setAntecedent(subsetsList.get(i));
                    rule.setConsequent(subsetsList.get(j));
                    rule.setConfidence(conf);
                    rule.setSourceSetSupportValue(supportCountMap.get(s));
                    res.add(rule);
                }
                conf = ((double) supportCountMap.get(s) / supportCountMap.get(subsetsList.get(j))) * 100;
                if(conf >=minConf) {
                    AssociationRule rule2 = new AssociationRule();
                    rule2.setAntecedent(subsetsList.get(j));
                    rule2.setConsequent(subsetsList.get(i));
                    rule2.setSourceSetSupportValue(supportCountMap.get(s));
                    rule2.setConfidence(conf);
                    res.add(rule2);
                }
            }
        }
    }

    private List<Set<String>> convertListToSet(List<List<String>> list) {
        List<Set<String>> res = new ArrayList<>();

        for(List<String> l : list) {
            Set<String> s = new HashSet<>(l);
            res.add(s);
        }
        return res;
    }

    private List<List<String>> findAllPossibleSubsets(List<String> list) {
        List<List<String>> ans = new ArrayList<>();
        helper(ans, new LinkedList<>(), list, 0);
        return ans;
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
