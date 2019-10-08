package cn.vove7.eventbus_nav.action

import cn.vove7.eventbus_nav.util.toPsiMethod
import cn.vove7.eventbus_nav.util.ConfigHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.psi.KtNamedFunction


/**
 * # BaseMarkPosterAction
 * Created by 11324.
 * Date: 2019/10/8
 */
abstract class BaseMarkPosterAction : AnAction() {

    private val PsiElement.containingMethod: PsiMethod? get() = psiMethod() ?: context?.psiMethod()

    private fun PsiElement.psiMethod(): PsiMethod? {
        if (this is PsiMethod) return this
        else if (this is KtNamedFunction) return this.toPsiMethod()
        return null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val ele = event.getData(PlatformDataKeys.PSI_ELEMENT)
        val method = ele?.containingMethod ?: return
        ConfigHelper.init(method.project)
        onPerform(method)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = false
        val ele = e.getData(PlatformDataKeys.PSI_ELEMENT)
        val method = ele?.containingMethod ?: return
        e.presentation.isEnabledAndVisible = shouldShow(method)
    }

    abstract fun onPerform(method: PsiMethod)

    /**
     * 控制选项是否显示
     */
    abstract fun shouldShow(method: PsiMethod): Boolean

}