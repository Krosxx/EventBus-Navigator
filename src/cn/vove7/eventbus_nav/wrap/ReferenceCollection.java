package cn.vove7.eventbus_nav.wrap;

import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ArrayListSet;
import java.util.*;

public class ReferenceCollection extends AbstractCollection<Reference> {
    private Set<Reference> references = new ArrayListSet<Reference>();
    public static ReferenceCollection EMPTY = new ReferenceCollection(new ArrayList<PsiReference>());

    public ReferenceCollection(Collection<PsiReference> psiReferences) {
        for (PsiReference reference : psiReferences) {
            references.add(new Reference(reference));
        }
    }

    public void clear ()  {
        this.references.clear();
    }

    public ReferenceCollection(ReferenceCollection references) {
        this.references.addAll(references);
    }

    public Iterator<Reference> iterator() {
        return references.iterator();
    }

    public boolean add(Reference reference) {
        return references.add(reference);
    }

    public int size() {
        return references.size();
    }
}