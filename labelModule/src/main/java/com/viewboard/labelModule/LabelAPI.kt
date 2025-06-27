package com.viewboard.labelModule

abstract class LabelAPI () {
    private val m_labels: ArrayList<Label> = ArrayList<Label>()

    public fun add(name: String, account: String) : Boolean {
        val label: Label = Label(name, account)

        if (flushAddIMPL(label)) {
            m_labels.add(label)
            return true
        }

        return false
    }

    public fun rm(label: Label) : Boolean {
        if (flushRmIMPL(label)) {
            m_labels.remove(label)
            return true
        }

        return false
    }

    public fun track(count: UInt) : UInt {
        val labels = trackIMPL(count)

        m_labels.addAll(labels)

        return labels.size.toUInt()
    }

    public fun getLabel(idx: Int) : Label {
        return m_labels.get(idx)
    }

    public fun getLabels() : ArrayList<Label> {
        return m_labels
    }

    protected abstract fun trackIMPL(count: UInt) : ArrayList<Label>

    protected abstract fun flushAddIMPL(label: Label) : Boolean

    protected abstract fun flushRmIMPL(label: Label) : Boolean
}