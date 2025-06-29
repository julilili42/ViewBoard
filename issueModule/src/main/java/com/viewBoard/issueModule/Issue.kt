package com.viewBoard.issueModule

import com.viewBoard.commonModule.Timestamp
import com.viewBoard.labelModule.Label

/**
 * @property ID the ID which is also immutable, use ID=0 if it is not synced with any higher abstraction storage
 * @property title the title
 * @property desc the description, could be null
 * @property creator the user who created the issue, who is also immutable
 * @property timestamp the timestamp of the issue, default = current timestamp
 * @property assignments the users assigned to the issue, max assignments = 255
 * @property labels the labels assigned to the issue, max labels = 255
 */
class Issue
internal constructor (
    title: String,
    desc: String?,
    creator: String,
    state: IssueState,
    assignments: ArrayList<String>,
    labels: ArrayList<Label>,
    timestamp: Timestamp = Timestamp(),
    ID: UInt = 0u
) {
    private var m_ID: UInt = ID
    private var m_title: String = title
    private var m_desc: String? = desc
    private val m_creator: String = creator
    private var m_state: IssueState = state
    private val m_timestamp: Timestamp = timestamp
    private val m_assignments: ArrayList<String> = assignments
    private val m_labels: ArrayList<Label> = labels

    /**
     * Get the ID
     *
     * @return the ID
     */
    public fun getID() : UInt {
        return m_ID
    }

    /**
     * Get the title
     *
     * @return the title
     */
    public fun getTitle() : String {
        return m_title
    }

    /**
     * Update the title
     *
     * @param title the new title
     */
    public fun updateTitle(title: String) {
        m_title = title
    }

    /**
     * Get the description
     *
     * @return the description
     */
    public fun getDesc() : String? {
        return m_desc
    }

    /**
     * Update the description
     *
     * @param desc the new description
     */
    public fun updateDesc(desc: String) {
        m_desc = desc
    }

    /**
     * Reset the description
     */
    public fun resetDesc() {
        m_desc = null
    }

    /**
     * Get the state
     *
     * @return the state
     */
    public fun getState() : IssueState {
        return m_state
    }

    /**
     * Set the state
     *
     * @param state the state
     */
    public fun setState(state: IssueState) {
        m_state = state
    }

    /**
     * Add a user assignment
     *
     * @param assignment the user
     */
    public fun addAssignment(assignment: String) {
        m_assignments.add(assignment)
    }

    /**
     * Remove a user assignment
     *
     * @param assignment the user
     * @return true if the user assignment could be removed, otherwise false
     */
    public fun removeAssignment(assignment: String) : Boolean {
        return m_assignments.remove(assignment)
    }

    /**
     * Remove a user assignment
     *
     * @param assignmentIDX the user index in the assignment list
     * @return true if the user assignment could be removed, otherwise false
     */
    public fun removeAssignment(assignmentIDX: UByte) {
        m_assignments.removeAt(assignmentIDX.toInt())
    }

    /**
     * Remove all user assignments
     */
    public fun resetAssignments() {
        m_assignments.clear()
    }

    /**
     * Add a label
     *
     * @param label the label
     */
    public fun addLabel(label: Label) {
        m_labels.add(label)
        // TODO: call LabelAPI
    }

    /**
     * Remove a label
     *
     * @param label the label
     */
    public fun removeLabel(label: Label) : Boolean {
        return m_labels.remove(label)
        // TODO: call LabelAPI
    }

    /**
     * Remove a label
     *
     * @param labelIDX the label index in the label list
     */
    public fun removeLabel(labelIDX: UByte) {
        m_labels.removeAt(labelIDX.toInt())
        // TODO: call LabelAPI
    }

    /**
     * Remove all labels
     */
    public fun resetLabels() {
        m_labels.clear()
    }

    /**
     * Get all labels
     *
     * @return all labels
     */
    public fun getLabels() : ArrayList<Label> {
        return m_labels
    }

    internal fun setID(ID: UInt) {
        m_ID = ID
    }
}