package cn.vove7.eventbus_nav.action

import cn.vove7.eventbus_nav.util.fullName
import cn.vove7.eventbus_nav.util.ConfigHelper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ex.MessagesEx
import com.intellij.psi.PsiMethod
import com.intellij.util.FileContentUtil


/**
 * # MarkPosterAction
 * Created by 11324.
 * Date: 2019/10/8
 */
class UnMarkPosterAction : BaseMarkPosterAction() {

    override fun onPerform(method: PsiMethod) {

        val fullName = method.fullName ?: ""
        val result = MessagesEx.showOkCancelDialog(
                "是否取消标记($fullName)为Poster函数？",
                "标记Poster函数", null
        )
        if (result != Messages.OK) {
            return
        }
        ConfigHelper.postMethodSet.also { list ->
            list.remove(fullName)
            ConfigHelper.postMethodSet = list
        }
        FileContentUtil.reparseFiles(method.containingFile.virtualFile)
    }


    override fun shouldShow(method: PsiMethod): Boolean {
        return method.fullName in ConfigHelper.postMethodSet
    }
}