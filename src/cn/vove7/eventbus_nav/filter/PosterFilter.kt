package cn.vove7.eventbus_nav.filter

import cn.vove7.eventbus_nav.util.BusLog
import cn.vove7.eventbus_nav.util.PsiUtils
import cn.vove7.eventbus_nav.util.toJavaType
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import com.intellij.usages.Usage
import com.intellij.usages.UsageInfo2UsageAdapter
import org.jetbrains.kotlin.idea.debugger.sequence.psi.KotlinPsiUtil
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * 过滤 post 参数
 */
class PosterFilter(
//参数 类名
        private val paramTypeName: String
) : Filter {

    init {
        BusLog.e("搜索type: $paramTypeName")
    }

    override fun shouldShow(usage: Usage): Boolean {
        var element = (usage as UsageInfo2UsageAdapter).element
        if (element.containingFile is KtFile) {
            val type = let {
                val argList = element.nextSibling as KtValueArgumentList
                val type = argList.arguments[0].getArgumentExpression()?.resolveType()
                if (type != null) {
                    KotlinPsiUtil.getTypeWithoutTypeParameters(type).toJavaType()
                } else {
                    null
                }
            }
            BusLog.i("kt type: $type ${element.parent.text}")
            if (PsiUtils.isEventBusPost(element) && type == paramTypeName) {
                return true
            }
        }

        if (element is PsiReferenceExpression) {
            if ((element.parent.also { element = it }) is PsiMethodCallExpression) {
                val callExpression = element as PsiMethodCallExpression
                val ps = callExpression.argumentList.expressionTypes
                if (ps.size == 1) {
                    if (paramTypeName == PsiUtils.getClass(ps[0], element)!!.qualifiedName) {
                        // pattern : EventBus.getDefault().post(new Event());
                        return true
                    }
                }
            }
        }
//                if ((element.parent.also { element = it }) is PsiExpressionStatement) {
//                    if ((element.parent.also { element = it }) is PsiCodeBlock) {
//                        val codeBlock = element as PsiCodeBlock
//                        val statements = codeBlock.statements
//                        for (statement in statements) {
//                            if (statement is PsiDeclarationStatement) {
//                                val elements = statement.declaredElements
//                                for (variable in elements) {
//                                    if (variable is PsiLocalVariable) {
//                                        val psiClass = PsiUtils.getClass(variable.typeElement.type, variable)
//                                        try {
//                                            if (paramTypeName == psiClass!!.qualifiedName) {
//                                                // pattern :
//                                                //   Event event = new Event();
//                                                //   EventBus.getDefault().post(event);
//                                                return true
//                                            }
//                                        } catch (e: NullPointerException) {
//                                            println(e.toString())
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        return false
    }


}
