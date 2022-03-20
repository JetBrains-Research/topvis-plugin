package com.github.salkaevruslan.topvisplugin.util

import com.intellij.openapi.project.Project
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths


@Serializable
data class FileData(val path: String, val topics: Array<String>, val probs: Array<Double>)

@Serializable
data class RepoData(val path: String, val files: Array<FileData>)


@Serializable
data class Response(val timestamp: String, val data: Array<RepoData>)
class TopicsParser {
    companion object {
        private var projectData: RepoData? = null

        fun parse(project: Project, fileName: String) {
            val path = project.basePath?.let { Paths.get(it, fileName) }?.toUri()
            if (path != null) {
                projectData = Json.decodeFromString<Response>(
                    File(path).readBytes().toString(Charsets.UTF_8)
                ).data.find { repo -> repo.path == project.name }
            }
        }

        fun getTopicsInfo(path: String): String? {
            return if (projectData == null) {
                null
            } else {
                val topics = projectData!!.files.find { file -> file.path == path }
                topics?.topics?.joinToString(", ")
            }
        }
    }
}