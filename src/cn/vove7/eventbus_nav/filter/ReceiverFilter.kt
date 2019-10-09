package cn.vove7.eventbus_nav.filter

import cn.vove7.eventbus_nav.util.PsiUtils
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.usages.Usage
import com.intellij.usages.UsageInfo2UsageAdapter
import org.jetbrains.kotlin.psi.KtNameReferenceExpression


class ReceiverFilter : Filter {
    override fun shouldShow(usage: Usage): Boolean {
        val element = (usage as UsageInfo2UsageAdapter).element
        if (element is PsiJavaCodeReferenceElement) {
            val method = element.context?.context?.context?.context ?: return false
            return PsiUtils.isEventBusReceiver(method)
        }
        if (element is KtNameReferenceExpression) {
            return PsiUtils.isEventBusReceiver(element.parent.parent.parent.parent.parent)
        }
        return false
    }
}
