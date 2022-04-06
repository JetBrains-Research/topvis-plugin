package org.jetbrains.topvisplugin.search
import com.intellij.ide.actions.SearchEverywherePsiRenderer
import com.intellij.ide.actions.searcheverywhere.FileSearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer
import com.intellij.ide.util.gotoByName.GotoFileModel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFilePathWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import com.intellij.util.ObjectUtils
import org.jetbrains.topvisplugin.settings.TopvisPluginSettings
import org.jetbrains.topvisplugin.util.TopicsParser
import java.awt.FontMetrics
import java.io.File
import java.util.*
import javax.swing.JList
import javax.swing.ListCellRenderer
import kotlin.math.max

class FileWithTopicsSearchEverywhereContributor(event: AnActionEvent) : FileSearchEverywhereContributor(event) {
    private val myModelForRenderer: GotoFileModel?

    init {
        myModelForRenderer = GotoFileModel(event.getRequiredData(CommonDataKeys.PROJECT))
    }

    override fun getGroupName(): String {
        return "Files with topics"
    }

    override fun getElementsRenderer(): ListCellRenderer<Any> {
        return object : SearchEverywherePsiRenderer(this) {
            override fun getItemMatchers(list: JList<*>, value: Any): ItemMatchers {
                val defaultMatchers = super.getItemMatchers(list, value)
                return if (value !is PsiFileSystemItem || myModelForRenderer == null) {
                    defaultMatchers
                } else GotoFileModel.convertToFileItemMatchers(
                    defaultMatchers,
                    value, myModelForRenderer
                )
            }

            override fun getContainerTextForLeftComponent(
                element: PsiElement,
                name: String,
                maxWidth: Int,
                fm: FontMetrics
            ): String? {
                // almost copied from original FileSearchEverywhereContributor
                val presentablePath = extractPresentablePath(element)
                var text =
                    ObjectUtils.chooseNotNull(presentablePath, SymbolPresentationUtil.getSymbolContainerText(element))
                if (text == null || text == name) return null
                if (text.startsWith("(") && text.endsWith(")")) {
                    text = text.substring(1, text.length - 1)
                }
                if (presentablePath == null && (text.contains("/") || text.contains(File.separator)) && element is PsiFileSystemItem) {
                    val project = element.getProject()
                    val basePath = Optional.ofNullable(project.basePath)
                        .map { filePath: String? ->
                            FileUtil.toSystemDependentName(
                                filePath!!
                            )
                        }
                        .orElse(null)
                    val file = element.virtualFile
                    if (file != null) {
                        text = FileUtil.toSystemDependentName(text)
                        val filePath = FileUtil.toSystemDependentName(file.path)
                        text = if (basePath != null && FileUtil.isAncestor(basePath, filePath, true)) {
                            ObjectUtils.notNull(FileUtil.getRelativePath(basePath, text, File.separatorChar), text)
                        } else {
                            val rootPath = Optional.ofNullable(GotoFileCellRenderer.getAnyRoot(file, project))
                                .map { root: VirtualFile ->
                                    FileUtil.toSystemDependentName(
                                        root.path
                                    )
                                }
                                .filter { root: String? ->
                                    basePath != null && FileUtil.isAncestor(
                                        basePath,
                                        root!!, true
                                    )
                                }
                                .orElse(null)
                            if (rootPath != null) ObjectUtils.notNull(
                                FileUtil.getRelativePath(
                                    rootPath,
                                    text,
                                    File.separatorChar
                                ), text
                            ) else FileUtil.getLocationRelativeToUserHome(text)
                        }
                    }
                }
                var topicsString = ""
                if (element is PsiFile) {
                    val projectPath = element.project.basePath
                    val enabled = element.project.service<TopvisPluginSettings>().topicsEnabled
                    if (enabled) {
                        val topicsInfo = projectPath?.let { element.virtualFile.path.removePrefix(it) }
                            ?.let { TopicsParser.getTopicsInfo(it) }
                        if (topicsInfo != null) {
                            topicsString = "($topicsInfo) "
                        }
                    }
                }
                val `in` = text!!.startsWith("in ")
                if (`in`) text = text.substring(3)
                val left = if (`in`) "in " else ""
                val adjustedText = "$topicsString$left$text"
                if (maxWidth < 0) return adjustedText
                val fullWidth = fm.stringWidth(adjustedText)
                if (fullWidth < maxWidth) return adjustedText
                val separator =
                    if (text.contains("/")) "/" else if (SystemInfo.isWindows && text.contains("\\")) "\\" else if (text.contains(
                            "."
                        )
                    ) "." else if (text.contains("-")) "-" else " "
                val parts = LinkedList(
                    StringUtil.split(
                        text, separator
                    )
                )
                var index: Int
                while (parts.size > 1) {
                    index = parts.size / 2 - 1
                    parts.removeAt(index)
                    if (fm.stringWidth(left + StringUtil.join(parts, separator) + "...") < maxWidth) {
                        parts.add(index, "...")
                        return "$topicsString$left" + StringUtil.join(parts, separator)
                    }
                }
                val adjustedWidth = max(adjustedText.length * maxWidth / fullWidth - 1, left.length + 3)
                return StringUtil.trimMiddle(adjustedText, adjustedWidth)
            }

            private fun extractPresentablePath(element: PsiElement?): String? {
                if (element == null) return null
                val file = element.containingFile
                if (file != null) {
                    val virtualFile = file.virtualFile
                    if (virtualFile is VirtualFilePathWrapper) return (virtualFile as VirtualFilePathWrapper).presentablePath
                }
                return null
            }
        }
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return FileWithTopicsSearchEverywhereContributor(initEvent)
        }
    }
}