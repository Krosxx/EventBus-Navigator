package cn.vove7.eventbus_nav.filter

import cn.vove7.eventbus_nav.util.firstParamTypeName
import cn.vove7.eventbus_nav.util.toPsiMethod
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiMethod
import com.intellij.usages.Usage
import com.intellij.usages.UsageInfo2UsageAdapter
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

//import org.jetbrains.kotlin.psi.KtNameReferenceExpression


class ReceiverFilter(
        private val paramType: String
) : Filter {
    override fun shouldShow(usage: Usage): Boolean {
        val element = (usage as UsageInfo2UsageAdapter).element

        if (element is PsiJavaCodeReferenceElement) {
            val me = element.context?.context?.context ?: return false
            if (me is PsiMethod) {
                return me.firstParamTypeName() == paramType
            }
        }
        if (element is KtNameReferenceExpression) {
            val me = element.parent.parent.parent.parent.parent.parent
            if (me is KtNamedFunction) {
                return me.toPsiMethod()?.firstParamTypeName() == paramType
            }
        }
        return false
    }
}
