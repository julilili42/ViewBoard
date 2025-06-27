package com.example.viewboard.issue

import com.viewBoard.labelModule.Label

abstract class IssueAPI () {
    private val m_issues: ArrayList<Issue> = ArrayList<Issue>()
    private val m_issueBuffer: HashMap<String, ArrayList<Issue>> = HashMap<String, ArrayList<Issue>>()

    public fun add(issue: Issue) : Boolean {
        if (flushAddIMPL(issue)) {
            m_issues.add(issue)
            return true
        }

        return false
    }

    public fun rm(issue: Issue) : Boolean {
        if (flushRmIMPL(issue)) {
            m_issues.remove(issue)
            return true
        }

        return false
    }

    public fun update(issue: Issue, newIssue: Issue) : Boolean {
        if (flushUpdateIMPL(issue, newIssue)) {
            m_issues.set(m_issues.indexOf(issue), newIssue)
            return true
        }

        return false
    }

    public fun track(count: UInt) : UInt {
        val issues = trackIMPL(count)

        m_issues.addAll(issues)

        return issues.size.toUInt()
    }

    public fun filter(name: String, labels: ArrayList<Label>) {
        var filteredIssues: ArrayList<Issue> = ArrayList<Issue>()

        for (issue in m_issues) {
            val issuelabels: ArrayList<Label> = issue.getLabels()

            val match = issuelabels.any { issueLabel -> labels.any { label -> issueLabel.getName() == label.getName() } }

            filteredIssues.add(issue)
        }

        m_issueBuffer.put(name, filteredIssues)
    }

    public fun getIssueBuffer(name: String) : ArrayList<Issue>? {
        return m_issueBuffer[name]
    }

    public fun rmIssueBuffer(name: String) {
        m_issueBuffer.remove(name)
    }

    public fun rmAllIssueBuffers() {
        m_issueBuffer.clear()
    }

    protected abstract fun trackIMPL(count: UInt) : ArrayList<Issue>

    protected abstract fun flushAddIMPL(issue: Issue) : Boolean

    protected abstract fun flushRmIMPL(issue: Issue) : Boolean

    protected abstract fun flushUpdateIMPL(issue: Issue, newIssue: Issue) : Boolean
}