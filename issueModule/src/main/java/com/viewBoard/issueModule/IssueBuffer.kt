package com.viewBoard.issueModule

import com.viewBoard.labelModule.Label

class IssueBuffer
internal constructor (
    IDs: ArrayList<UInt>
) {
    internal val m_IDs: ArrayList<UInt> = IDs

    public fun filter(issueAPI: IssueAPI, labels: ArrayList<Label>) {
        m_IDs.clear()

        for (ID in m_IDs) {
            val issue: Issue? = issueAPI.m_issues[ID]

            if (issue == null)
                continue

            val issuelabels: ArrayList<Label> = issue.getLabels()

            val match: Boolean = issuelabels.any { issueLabel -> labels.any { label -> issueLabel.cmp(label) } }

            if (match)
                m_IDs.add(issue.getID())
        }
    }

    public fun build(issueAPI: IssueAPI) : ArrayList<Issue> {
        val issues: ArrayList<Issue> = ArrayList<Issue>()

        for (ID in m_IDs) {
            val issue: Issue? = issueAPI.m_issues[ID]

            if (issue != null)
                issues.add(issue)
        }

        return issues
    }
}