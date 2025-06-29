package com.viewBoard.issueModule

import com.viewBoard.commonModule.Timestamp
import com.viewBoard.labelModule.Label

abstract class IssueAPI () {
    internal val m_issues: HashMap<UInt, Issue> = HashMap<UInt, Issue>()
    private var m_lastFetchTS: Timestamp = Timestamp()

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
        m_lastFetchTS.now()

        val issues: ArrayList<Issue> = fetchIMPL(m_issues, count)

        for (issue in issues) {
            m_issues.put(issue.getID(), issue)
        }

        return issues.size.toUInt()
    }

    public fun fetch(IDs: ArrayList<UInt>) : Boolean {
        m_lastFetchTS.now()

        val issues: ArrayList<Issue> = fetchIMPL(m_issues, IDs)

        if (IDs.size != issues.size)
            return false

        for (issue in issues) {
            m_issues.put(issue.getID(), issue)
        }

        return true
    }

    public fun refetch() : UInt {
        val newFetchTS: Timestamp = Timestamp()

        val count: UInt = refetchIMPL(m_issues, m_lastFetchTS)

        m_lastFetchTS = newFetchTS

        return count
    }

    public fun getGlobalIssueBuffer() : IssueBuffer {
        return IssueBuffer(ArrayList<UInt>(m_issues.keys))
    }

    // TODO: add getAccountViews(name) : ArrayList<IssueBuffer>

    protected abstract fun fetchIMPL(issues: HashMap<UInt, Issue>, count: UInt) : ArrayList<Issue>

    protected abstract fun fetchIMPL(issues: HashMap<UInt, Issue>, IDs: ArrayList<UInt>) : ArrayList<Issue>

    protected abstract fun refetchIMPL(issues: HashMap<UInt, Issue>, timestamp: Timestamp) : UInt

    protected abstract fun flushAddIMPL(issue: Issue) : UInt

    protected abstract fun flushRmIMPL(issue: Issue) : Boolean

    protected abstract fun flushUpdateIMPL(issue: Issue, newIssue: Issue) : Boolean
}