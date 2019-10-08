package cn.vove7.eventbus_nav.wrap;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import javax.swing.*;

public class Reference {
    private PsiElement psiElement;

    public Reference(PsiReference reference) {
        this.psiElement = reference.getElement();
    }

    public PsiElement getPsiElement() {
        return this.psiElement;
    }

    public VirtualFile containingVirtualFile() {
        return containingFile().getVirtualFile();
    }

    public PsiMethod containingMethod() {
        PsiElement parent;
        PsiElement current = psiElement;
        while (true) {
            parent = current.getParent();
            if (parent instanceof PsiFile) return null;
            if (parent instanceof PsiMethod) return (PsiMethod) parent;
            current = parent;
        }
    }

    public PsiClass containingClass() {
        PsiElement parent;
        PsiElement current = psiElement;
        while (true) {
            parent = current.getParent();
            if (parent instanceof PsiFile) return null;
            if (parent instanceof PsiClass) return (PsiClass) parent;
            current = parent;
        }
    }

    public PsiFile containingFile() {
        return psiElement.getContainingFile();
    }

    public String containingPackage() {
        String fullPackageName = "default";
        try {
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiElement.getContainingFile().getContainingDirectory());
            if (!psiPackage.getQualifiedName().trim().equals(""))
                fullPackageName = psiPackage.getQualifiedName();
        } catch (NullPointerException e) {
            fullPackageName = "default";
        }
        return fullPackageName;
    }

    public Icon icon() {
        if (containingMethod() != null)
            return containingMethod().getIcon(0);
        if (containingClass() != null)
            return containingClass().getIcon(0);
        if (containingPackage() != null)
            return containingFile().getIcon(0);
        return psiElement.getIcon(0);
    }

    public boolean equals(Object reference) {
        return reference != null
                && reference instanceof Reference
                && psiElement.equals(((Reference) reference).psiElement);
    }

    private String getContainingClassName(PsiClass containingClass) {
        String className = containingClass.getName();
        if (className == null)
            return "Anonymous class in " + getContainingClassOrFile(containingClass);
        return className;
    }

    private String getContainingClassOrFile(PsiClass theClass) {
        if (theClass.getContainingClass() != null)
            //noinspection ConstantConditions
            return theClass.getContainingClass().getName();
        else
            return theClass.getContainingFile().getName();
    }
}