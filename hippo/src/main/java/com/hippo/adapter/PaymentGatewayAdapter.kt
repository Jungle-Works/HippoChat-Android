package com.hippo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hippo.R
import com.hippo.callback.OnPaymentItemClickListener
import com.hippo.model.payment.AddedPaymentGateway
import com.hippo.utils.RoundedCornersTransformation
import kotlinx.android.synthetic.main.hippo_payment_item.view.*

/**
 * Created by gurmail on 2020-05-06.
 * @author gurmail
 */
class PaymentGatewayAdapter(var arralyLisy: ArrayList<AddedPaymentGateway>, var onItemListener: OnPaymentItemClickListener) : RecyclerView.Adapter<PaymentGatewayAdapter.ViewHolder>() {

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.hippo_payment_item, parent, false))
    }

    override fun getItemCount(): Int {
        return arralyLisy.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, arralyLisy[position], onItemListener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, addedPayment: AddedPaymentGateway, onItemListener: OnPaymentItemClickListener) = with(itemView) {
            itemView.name.text = addedPayment.gatewayName
            itemView.item.setOnClickListener {
                onItemListener.onItemClickListener(addedPayment)
            }
            val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.hippo_placeholder)
                .error(R.drawable.hippo_placeholder)
                .fitCenter()
                .priority(Priority.HIGH)

            Glide.with(context).asBitmap()
                .apply(options)
                .load(addedPayment.gatewayImage)
                .into(itemView.image)


//            val myOptions = RequestOptions
//                .bitmapTransform(RoundedCornersTransformation(context, 1, 1))
//                .placeholder(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(ContextCompat.getDrawable(context, R.drawable.hippo_placeholder))
//
//            Glide.with(context).load(addedPayment.gatewayImage)
//                .apply(myOptions)
//                .into(itemView.image)


        }
    }
}