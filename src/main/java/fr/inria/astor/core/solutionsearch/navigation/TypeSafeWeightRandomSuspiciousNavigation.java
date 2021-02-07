package fr.inria.astor.core.solutionsearch.navigation;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.WeightElement;
import spoon.support.reflect.code.CtArrayWriteImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSafeWeightRandomSuspiciousNavigation implements SuspiciousNavigationStrategy {
    @Override
    public List<ModificationPoint> getSortedModificationPointsList(List<ModificationPoint> modificationPoints) {

        //remove array writes, We dont want the following transformation which is not valid:
        // 'var[i] = valueAssignedToThisVar'   to 'otherVar + 1 = valueAssignedToThisVar'
        List<ModificationPoint> remaining = modificationPoints.stream()
                .filter(modificationPoint -> !modificationPoint.getCodeElement().getClass()
                        .equals(CtArrayWriteImpl.class)).collect(Collectors.toList());
        List<ModificationPoint> solution = new ArrayList<>();


        for (int i = 0; i < modificationPoints.size(); i++) {
            List<WeightElement<?>> we = new ArrayList<>();
            double sum = 0;
            for (ModificationPoint gen : remaining) {
                double susp = ((SuspiciousModificationPoint) gen).getSuspicious().getSuspiciousValue();
                sum += susp;
                WeightElement<?> w = new WeightElement<>(gen, 0);
                w.weight = susp;
                we.add(w);
            }

            if (sum != 0) {

                for (WeightElement<?> weightCtElement : we) {
                    weightCtElement.weight = weightCtElement.weight / sum;
                }

                WeightElement.feedAccumulative(we);
                WeightElement<?> selected = WeightElement.selectElementWeightBalanced(we);

                ModificationPoint selectedg = (ModificationPoint) selected.element;
                remaining.remove(selectedg);
                solution.add(selectedg);
            } else {
                solution.addAll(remaining);
                break;
            }
        }
        return solution;
    }
}
