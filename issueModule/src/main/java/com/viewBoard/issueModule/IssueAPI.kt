package com.viewBoard.issueModule

import com.viewBoard.commonModule.Timestamp
import com.viewBoard.labelModule.Label

abstract class IssueAPI () {
    internal val m_issues: HashMap<UInt, Issue> = HashMap<UInt, Issue>()
//    private val m_issueBuffer: HashMap<String, IssueBuffer> = HashMap<String, IssueBuffer>()
    private val m_IDs: ArrayList<UInt> = ArrayList<UInt>()
    private var m_lastID: UInt = 0u
    private var m_lastREQ: Timestamp = Timestamp()

    public fun add(title: String, desc: String?, creator: String, assignments: ArrayList<String>, labels: ArrayList<Label>) : Boolean {
        val issue: Issue = Issue(title, desc, creator, IssueState.NEW, assignments, labels)

        var ID: UInt = flushAddIMPL(issue)

        if (ID != 0u) {
            issue.setID(ID)
            m_issues.put(ID, issue)

            return true
        }

        return false
    }

    public fun rm(issue: Issue) : Boolean {
        if (flushRmIMPL(issue)) {
            m_issues.remove(issue.getID(), issue)
            return true
        }

        return false
    }

    public fun update(issue: Issue, newIssue: Issue) : Boolean {
        if (flushUpdateIMPL(issue, newIssue)) {
            m_issues[issue.getID()] = newIssue
            return true
        }

        return false
    }

    public fun fetch(count: UInt) : UInt {
        m_lastREQ.now()
        val issues: ArrayList<Issue> = fetchIMPL(m_lastID, count)

        for (issue in issues) {
            m_issues.put(issue.getID(), issue)
            m_IDs.add(issue.getID())
        }

        m_lastID = issues.last().getID()

        return issues.size.toUInt()
    }

    public fun refetch() : UInt {
        val tmpLastREQ: Timestamp = m_lastREQ
        m_lastREQ.now()
        return refetchIMPL(m_issues, m_lastID, tmpLastREQ)
    }

    // TODO: maybe remove -> replaced ?
//    public fun filterGlobal(name: String, labels: ArrayList<Label>) {
//        var filteredIssues: ArrayList<UInt> = ArrayList<UInt>()
//
//        for (issue in m_issues) {
//            val issuelabels: ArrayList<Label> = issue.value.getLabels()
//
//            val match: Boolean = issuelabels.any { issueLabel -> labels.any { label -> issueLabel.cmp(label) } }
//
//            if (match)
//                filteredIssues.add(issue.key)
//        }
//
//        m_issueBuffer.put(name, IssueBuffer(filteredIssues))
//    }
//
//    public fun getIssueBuffer(name: String) : IssueBuffer? {
//        return m_issueBuffer[name]
//    }
//
//    public fun rmIssueBuffer(name: String) {
//        m_issueBuffer.remove(name)
//    }
//
//    public fun rmAllIssueBuffers() {
//        m_issueBuffer.clear()
//    }

    public fun getGlobalIssueBuffer() : IssueBuffer {
        return IssueBuffer(m_IDs)
    }

    // TODO: add getAccountViews(name) : ArrayList<IssueBuffer>

    protected abstract fun fetchIMPL(lastID: UInt, count: UInt) : ArrayList<Issue>

    protected abstract fun refetchIMPL(issues: HashMap<UInt, Issue>, lastID: UInt, timestamp: Timestamp) : UInt

    protected abstract fun flushAddIMPL(issue: Issue) : UInt

    protected abstract fun flushRmIMPL(issue: Issue) : Boolean

    protected abstract fun flushUpdateIMPL(issue: Issue, newIssue: Issue) : Boolean
}