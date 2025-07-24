package com.example.viewboard.backend.util

import com.example.viewboard.backend.data.ProjectLayout
import com.example.viewboard.backend.data.ViewLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.collections.ifEmpty

/**
 * Get all views from projects
 *
 * @param views the views to be filtered
 * @param projs the projects
 *
 * @return the views that have been filtered
 */
fun filterViewsByProjects(views: Flow<List<ViewLayout>>, projs: Flow<List<ProjectLayout>>) : Flow<List<ViewLayout>> {
    val possibleViewIds: Flow<List<String>> = projs.map { proj ->
        proj.flatMap { it.views }
    }

    return views.combine(possibleViewIds) { viewList, idList ->
        viewList.filter { it.id in idList }
            .ifEmpty { emptyList() }
    }
}

/**
 * Get all views from projects
 *
 * @param views the views to be filtered
 * @param projs the projects
 *
 * @return the views that have been filtered
 */
//fun filterViewsByProjects(views: Flow<List<ViewLayout>>, projs: Set<ProjectLayout>) : Flow<List<ViewLayout>> {
//    val possibleViewIds: Set<String> = projs.flatMap { it.views }.toSet()
//
//    return views.map { view ->
//        view.filter { it.id in possibleViewIds }
//    }
//}

/**
 * Get all views from projects
 *
 * @param views the views to be filtered
 * @param projs the projects
 *
 * @return the views that have been filtered
 */
fun filterViewsByProjects(views: Flow<List<ViewLayout>>, projs: Set<String>) : Flow<List<ViewLayout>> {
    val projList = FirebaseAPI.getAllProjects().map { proj ->
        proj.filter { it.id in projs }
    }

    val possibleViewIds = projList.map { proj ->
        proj.flatMap { it.views }
    }

    return views.combine(possibleViewIds) { viewList, idList ->
        viewList.filter { it.id in idList }
            .ifEmpty { emptyList() }
    }
}

/**
 * Get all views from creator
 *
 * @param views the views to be filtered
 * @param creatorID the id of the creator
 *
 * @return the views that have been filtered
 */
fun filterViewsByCreator(views: Flow<List<ViewLayout>>, creatorID: String?) : Flow<List<ViewLayout>> {
    return if (creatorID != null) {
        views.map { views ->
            views.filter { it.creator == creatorID }
        }
    } else {
        flowOf(emptyList())
    }
}

/**
 * Search for a view in projects
 *
 * @param view the view
 * @param projs the projects to be searched
 *
 * @return the project, if something was found
 */
fun getProjectByView(view: String, projs: Flow<List<ProjectLayout>>) : String? {
    return runBlocking {
        projs.map { list ->
        list.firstOrNull{view in it.views}?.id
        }
        .first()
    }
}

/**
 * Search for a view in projects
 *
 * @param view the view
 * @param projs the projects to be searched
 *
 * @return the project, if something was found
 */
fun getProjectByView(view: String, projs: List<ProjectLayout>) : String? {
    return projs.firstOrNull{view in it.views}?.id
}