package cn.vove7.eventbus_nav.util

import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.FileContentUtil
import org.jetbrains.kotlin.asJava.LightClassUtil.getLightClassMethod
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.nj2k.postProcessing.resolve
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

object PsiUtils {

    fun isPostMethod(methodName: String): Boolean {
        return methodName in ConfigHelper.postMethodSet.map { it.substring(it.lastIndexOf('.') + 1) }
    }

    @JvmStatic
    fun getClass(psiType: PsiType?, ele: PsiElement): PsiClass? {
        if (psiType is PsiClassType) {
            return psiType.resolve()
        } else if (psiType is PsiPrimitiveType) {
            return psiType.getBoxedType(ele)!!.resolve()
        }
        BusLog.i(psiType)
        return null
    }

    @JvmStatic
    fun isEventBusReceiver(psiElement: PsiElement): Boolean {
        if (psiElement is PsiMethod) {
            if (psiElement.annotations.any { it.qualifiedName == "org.greenrobot.eventbus.Subscribe" }) {
                return true
            }
        }
        if (psiElement is KtNamedFunction) {
            return psiElement.toPsiMethod()?.let { isEventBusReceiver(it) } ?: false
        }
        return false
    }

    @JvmStatic
    fun isEventBusPost(psiElement: PsiElement): Boolean {
        // pkg.class.method
        val fullName: String? = when (psiElement) {
            is PsiCallExpression -> psiElement.resolveMethod()?.fullName
            is KtNameReferenceExpression -> psiElement.resolve()?.fullName
            else -> null
        }
        fullName ?: return false
        //标记 or default
        if (fullName in ConfigHelper.postMethodSet) return true

        val dotIndex = fullName.lastIndexOf('.')
        if (dotIndex < 0) return false
        val className = fullName.substring(0, dotIndex)
        val methodName = fullName.substring(dotIndex + 1)
        //函数所属类
        val project = psiElement.project
        val methodClass by lazy {
            val javaPsiFacade = JavaPsiFacade.getInstance(project)
            javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project))
        }
        return isPostMethod(methodName) && (isEventBusClass(className) ||
                methodClass?.let { isSuperOfEventBus(it) } ?: false)
    }

    @JvmStatic
    fun isEventBusClass(className: String): Boolean {
        return try {
            className in ConfigHelper.postMethodSet.map { it.substring(0, it.lastIndexOf('.')) }
        } catch (e: Exception) {
            println(e.toString())
            false
        }
    }

    @JvmStatic
    fun isSuperOfEventBus(psiClass: PsiClass): Boolean {
        val supers = psiClass.supers
        if (supers.isEmpty()) {
            return false
        }
        for (superClass in supers) {
            try {
                if (superClass.qualifiedName == "org.greenrobot.eventbus.EventBus") {
                    return true
                }
            } catch (e: Exception) {
                println(e.toString())
            }
        }
        return false
    }

}

fun PsiMethod.firstParamTypeName(): String? {
    return PsiUtils.getClass(parameters[0].type as PsiType, this)?.qualifiedName
}

val PsiElement.fullName: String? get() = getKotlinFqName()?.asString()

//kotlinInternalUastUtils
//https://github.com/JetBrains/kotlin/blob/master/plugins/uast-kotlin/src/org/jetbrains/uast/kotlin/internal/kotlinInternalUastUtils.kt
fun KtNamedFunction.toPsiMethod(): PsiMethod? = getLightClassMethod(this)

/**
 * 获取Kt 函数参数
 */
fun KtNamedFunction.firstParamType(): String? {
    //参数类型
    return when (val pType = toPsiMethod()?.parameterList?.parameters?.getOrNull(0)?.type) {
        //基本类型
        is PsiPrimitiveType -> pType.boxedTypeName
        else -> pType?.canonicalText
    }
}

fun String.isKotPrimitiveType(): Boolean {
    return this in KotPrimitiveType2JavaWrap.keys
}

private val KotPrimitiveType2JavaWrap
    get() = mapOf(
            "kotlin.Int" to "java.lang.Integer",
            "kotlin.Boolean" to "java.lang.Boolean",
            "kotlin.Char" to "java.lang.Character",
            "kotlin.Double" to "java.lang.Double",
            "kotlin.Float" to "java.lang.Float",
            "kotlin.Long" to "java.lang.Long",
            "kotlin.Any" to "java.lang.Object",
            "kotlin.String" to "java.lang.String"
    )

//kt cls -> java cls
fun String.toJavaType(): String {
    return KotPrimitiveType2JavaWrap.getOrDefault(this, this)
}

fun refresh() {
//    FileContentUtil.setFileText()
    FileContentUtil.FORCE_RELOAD_REQUESTOR
    FileContentUtil.reparseFiles()
}
