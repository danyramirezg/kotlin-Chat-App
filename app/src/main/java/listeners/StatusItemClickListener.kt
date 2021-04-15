package listeners

import util.StatusListElement

interface StatusItemClickListener {
    fun onItemClicked(statusElement: StatusListElement)

}