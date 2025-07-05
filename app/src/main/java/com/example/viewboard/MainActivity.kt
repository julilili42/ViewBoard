package com.example.viewboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Navigation
import com.example.viewboard.ui.theme.ComposeLoginScreenInitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAPI.init()

        enableEdgeToEdge()

        val l = LabelLayout(name = "3. label", creator = "ich")
       // FirebaseAPI.addLabel(l)

        val i = IssueLayout(title = "3. issue", creator = "ich")
        //FirebaseAPI.addIssue(i)

        val p = ProjectLayout(name = "first project", creator = "ich", issues = arrayListOf("SlIsrElzBCUuNoPG3G7K", "OplQrgTrggRIW9yDNQ8a", "f7DmLeYkwfQ7IkA6tze3"))
       // FirebaseAPI.addProject(p)
       /* lifecycleScope.launch {
            val startMonth = (0..1).random()    // 0 oder 1
            val endMonth   = (4..7).random()
            for (i in 1..20) {
                val projectName = "Project #$i"
                val p = ProjectLayout(
                    name = projectName,
                    creator = "ich",
                    startMonth = startMonth,
                    endMonth = endMonth,
                    issues = arrayListOf("SlIsrElzBCUuNoPG3G7K", "OplQrgTrggRIW9yDNQ8a", "f7DmLeYkwfQ7IkA6tze3")// leer oder fülle nach Bedarf
                )
                try {
                    FirebaseAPI.addProject(p)
                    println("Projekt angelegt: $projectName")
                } catch (e: Exception) {
                    println("Fehler beim Anlegen von $projectName: ${e.localizedMessage}")
                }
            }
        }
        lifecycleScope.launch {
            val p = FirebaseAPI.getProject("ysZaMVY24jnSyLuE5FKJ")
            println(p!!.creationTS.getFull())
            p!!.issues.add("SlIsrElzBCUuNoPG3G7K")
            p!!.issues.add("OplQrgTrggRIW9yDNQ8a")
            p!!.issues.add("f7DmLeYkwfQ7IkA6tze3")
//            FirebaseAPI.updProject("ysZaMVY24jnSyLuE5FKJ", p)
        }
*/
        val v = ViewLayout(name = "first view", creator = "ich", issues = arrayListOf("SlIsrElzBCUuNoPG3G7K"))
//        FirebaseAPI.addView(v)

        lifecycleScope.launch {
            val labels = FirebaseAPI.getLabels()
            labels.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        lifecycleScope.launch {
          val issues = FirebaseAPI.getIssues()
            issues.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        lifecycleScope.launch {
            val projects = FirebaseAPI.getMyProjects()
            projects.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        lifecycleScope.launch {
            val views = FirebaseAPI.getViews()
            views.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        lifecycleScope.launch {
            val views = FirebaseAPI.getIssues("ysZaMVY24jnSyLuE5FKJ")
            views.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}
