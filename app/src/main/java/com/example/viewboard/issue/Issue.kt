package com.example.viewboard.issue

class Issue (
    private val ID: UInt,                       // immutable //
    private val title: String,
    private val desc: String?,                  // could be null //
    private val creator: String,                // immutable //
    private val assignments: ArrayList<String>, // max assignments = 255 //
    private val labels: ArrayList<String>       // max labels      = 255 //
) {
    private val m_ID: UInt = ID
    private var m_title: String = title
    private var m_desc: String? = desc
    private val m_creator: String = creator
    private val m_assignments: ArrayList<String> = assignments
    private val m_labels: ArrayList<String> = labels

    public fun getID() : UInt {
        return m_ID
    }

    public fun getTitle() : String {
        return m_title
    }

    public fun updateTitle(title: String) {
        m_title = title
    }

    public fun getDesc() : String? {
        return m_desc
    }

    public fun updateDesc(desc: String) {
        m_desc = desc
    }

    public fun resetDesc() {
        m_desc = null
    }

    public fun addAssignment(assignment: String) {
        m_assignments.add(assignment)
    }

    public fun removeAssignment(assignment: String) : Boolean {
        return m_assignments.remove(assignment)
    }

    public fun removeAssignment(assignmentIDX: UByte) {
        m_assignments.removeAt(assignmentIDX.toInt())
    }

    public fun resetAssignments() {
        m_assignments.clear()
    }

    public fun addLabel(label: String) {
        m_labels.add(label)
    }

    public fun removeLabel(label: String) : Boolean {
        return m_labels.remove(label)
    }

    public fun removeLabel(labelIDX: UByte) {
        m_labels.removeAt(labelIDX.toInt())
    }

    public fun resetLabels() {
        m_labels.clear()
    }
}