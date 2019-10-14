package cn.vove7.eventbus_nav.lineprovider

import cn.vove7.eventbus_nav.action.ShowUsagesAction
import cn.vove7.eventbus_nav.filter.Filter
import cn.vove7.eventbus_nav.filter.PosterFilter
import cn.vove7.eventbus_nav.filter.ReceiverFilter
import cn.vove7.eventbus_nav.util.*
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtilBase
import com.intellij.ui.awt.RelativePoint
import org.jetbrains.kotlin.idea.debugger.sequence.psi.KotlinPsiUtil
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.types.KotlinType
import java.awt.event.MouseEvent

class EventBus3LineMarkerProvider : LineMarkerProvider {

    @Suppress("DEPRECATION")
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {
        ConfigHelper.init(psiElement.project)

        val psiMethod by lazy { psiElement.psiMethod() }
        return when {
            PsiUtils.isEventBusPost(psiElement) -> {
                LineMarkerInfo(
                        psiElement, psiElement.textRange,
                        ICON_NAV, 0, { "Show Receiver" },
                        ::showReceivers, GutterIconRenderer.Alignment.CENTER
                )
            }
            PsiUtils.isEventBusReceiver(psiElement) -> {
                val methodElement = psiMethod ?: return null
                val markElement = methodElement.nameIdentifier!!
                LineMarkerInfo(markElement, markElement.textRange ?: methodElement.textRange,
                        ICON_NAV, 0, { "Show Poster" },
                        ::showPosters, GutterIconRenderer.Alignment.CENTER
                )
            }
            //Bus function
            psiMethod != null -> {
                if (psiMethod?.fullName in ConfigHelper.postMethodSet) {
                    val markElement = psiMethod!!.nameIdentifier!!
                    LineMarkerInfo(psiElement, markElement.textRange ?: psiMethod!!.textRange,
                            ICON_BUS, 0, { "Show References" },
                            ::showReferences, GutterIconRenderer.Alignment.CENTER
                    )
                } else null
            }
            else -> null
        }
    }

    private fun PsiElement.psiMethod(): PsiMethod? {
        if (this is PsiMethod) return this
        else if (this is KtNamedFunction) return this.toPsiMethod()
        return null
    }

    private val ICON_NAV = IconLoader.getIcon("/icons/near_me.svg")
    private val ICON_BUS = IconLoader.getIcon("/icons/bus.svg")

    private val MAX_USAGES = 100

    private fun showPosters(e: MouseEvent, ele: PsiElement) {
        val psiElement = ele.context as PsiMethod
        val project = psiElement.project

        val paramType = psiElement.firstParamTypeName() ?: return
        BusLog.i("paramType: $paramType")

        val findEleUsageList = mutableListOf<PsiElement>()
        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        ConfigHelper.postMethodSet.forEach { fullName ->
            val (clsName, methodName) = fullName.let {
                val dotIndex = it.lastIndexOf('.')
                it.substring(0, dotIndex) to it.substring(dotIndex + 1)
            }

            val myBusClass = javaPsiFacade.findClass(clsName, GlobalSearchScope.allScope(project))
            val myPostMethod = myBusClass?.findMethodsByName(methodName, false)?.get(0)
                ?: return@forEach
            findEleUsageList.add(myPostMethod)
        }
        ShowUsagesAction(PosterFilter(paramType), ShowUsagesAction.TYPE_POST).startFindUsages(findEleUsageList.toTypedArray(), RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES)
    }

    /**
     * 解析post 数据类型
     * 根据类型 搜索引用，过滤函数
     */
    private fun showReceivers(e: MouseEvent, psiElement: PsiElement) {
        val global = GlobalSearchScope.allScope(psiElement.project)
        val javaPsiFacade = JavaPsiFacade.getInstance(psiElement.project)
        val subscribeCls = javaPsiFacade.findClass("org.greenrobot.eventbus.Subscribe", global)
            ?: return
        //Java 函数调用
        if (psiElement is PsiMethodCallExpression) {
            val expressionTypes = psiElement.argumentList.expressionTypes
            if (expressionTypes.isNotEmpty()) {
                val eventClass = PsiUtils.getClass(expressionTypes[0], psiElement) ?: return
                eventClass.qualifiedName?.also {
                    ShowUsagesAction(ReceiverFilter(it), ShowUsagesAction.TYPE_RECEIVER).startFindUsages(subscribeCls, RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES)
                    return
                }
            }
        }
        //Kotlin 函数调用
        if (psiElement is KtReferenceExpression) {
            val argList = psiElement.nextSibling as KtValueArgumentList
            val type = argList.arguments[0].getArgumentExpression()?.resolveType() as KotlinType
            val ktType = KotlinPsiUtil.getTypeWithoutTypeParameters(type)
            val clsType = ktType.toJavaType()
            ShowUsagesAction(ReceiverFilter(clsType), ShowUsagesAction.TYPE_RECEIVER).startFindUsages(subscribeCls,
                    RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES)
        }
    }

    private fun showReferences(e: MouseEvent, psiElement: PsiElement) {
        ShowUsagesAction(Filter { true }, -1).startFindUsages(psiElement,
                RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES)
        AllIcons.General.Remove

    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {

    }
}
