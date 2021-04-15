package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dany.chatapp.R
import listeners.StatusItemClickListener
import util.StatusListElement
import util.populateImage

class StatusListAdapter(val statusList: ArrayList<StatusListElement>) :
    RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>() {


    private var clickListener: StatusItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusListViewHolder {
        val myView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_status_list, parent, false)
        return StatusListViewHolder(myView)
    }

    fun onRefresh() {
        statusList.clear()
        notifyDataSetChanged()
    }

    fun addElement(element: StatusListElement) {
        statusList.add(element)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(holder: StatusListViewHolder, position: Int) {
        holder.bind(statusList[position], clickListener)
    }

    // To notify database changes
    fun setOnItemClickListener(listener: StatusItemClickListener) {
        clickListener = listener
        notifyDataSetChanged()
    }


    class StatusListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var layout = view.findViewById<RelativeLayout>(R.id.itemLayout)
        private var elementIV = view.findViewById<ImageView>(R.id.itemIV)
        private var elementNameTV = view.findViewById<TextView>(R.id.itemNameTV)
        private var elementTimeTV = view.findViewById<TextView>(R.id.itemTimeTV)

        fun bind(element: StatusListElement, listener: StatusItemClickListener?) {

            //Updating the information:
            populateImage(elementIV.context, element.userUrl, elementIV, R.drawable.default_user)
            elementNameTV.text = element.userName
            elementTimeTV.text = element.statusTime
            layout?.setOnClickListener { listener?.onItemClicked(element) }

        }
    }

}

