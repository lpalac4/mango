package com.moraware.mango.order

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moraware.domain.models.Order
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter
import com.moraware.mango.databinding.FragmentOrderoptionsDialogBinding
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.product.ProductViewModel

class OrderOptionsDialogFragment : BottomSheetDialogFragment() {
    private lateinit var mListener: OrderListener

    lateinit var viewModel: OrderOptionsViewModel
    lateinit var binding: FragmentOrderoptionsDialogBinding
    var ordersAdapter : OrderItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orderoptions_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(OrderOptionsViewModel::class.java)
        val data : ProductViewModel? = arguments?.getParcelable(ARG_ITEM_MODEL)

        (data)?.let {
            viewModel.setData(it, arguments?.getBoolean(ARG_ITEM_CHECK_ORDERS) ?: false)
        }

        viewModel.orderSuccess.observe(this, Observer {
            dismiss()
            mListener.orderSuccess(it)
        })

        viewModel.allOrdersCancelled.observe(this, Observer { it ->
            dismiss()
        })

        viewModel.getErrorMessage().observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        viewModel.singleOrderUpdated.observe(this, Observer { orderId ->
            ordersAdapter?.updateSingleOrder(orderId)
        })

        viewModel.singleOrderCancelled.observe(this, Observer { orderId ->
            ordersAdapter?.deleteSingleOrder(orderId)
        })

        viewModel.itemThumbnailChanged.observe(this, Observer {
            mListener.orderUpdated(it)
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        ordersAdapter = OrderItemAdapter(this, viewModel)
        binding.orderoptionsOrdersForMealContainer.layoutManager = LinearLayoutManager(context)
        binding.orderoptionsOrdersForMealContainer.adapter = ordersAdapter
        viewModel.loadData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OrderListener) {
            throw RuntimeException("Please ensure parent activity implement OrderOptionsDialogFragment.OrderListener.")
        }

        mListener = context
    }

    interface OrderListener {
        fun orderSuccess(thumbnail: ItemThumbnail)
        fun orderFailure()
        fun orderUpdated(thumbnail: ItemThumbnail)
    }

    companion object {

        private const val ARG_ITEM_MODEL = "OrderOptionsDialogFragment.product_item_model"
        private const val ARG_ITEM_CHECK_ORDERS = "OrderOptionsDialogFragment.check_orders"
        val TAG: String = OrderOptionsDialogFragment::class.java.simpleName

        fun submitOrderInstance(productModel: ProductViewModel): OrderOptionsDialogFragment {
            val fragment = OrderOptionsDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_ITEM_MODEL, productModel)
            fragment.arguments = args
            return fragment
        }

        fun checkOrdersInstance(productModel: ProductViewModel): OrderOptionsDialogFragment {
            val fragment = OrderOptionsDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_ITEM_MODEL, productModel)
            args.putBoolean(ARG_ITEM_CHECK_ORDERS, true)
            fragment.arguments = args
            return fragment
        }
    }

    class OrderItemAdapter(lifecycleOwner: LifecycleOwner, viewModel: OrderOptionsViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {

        private var orders : MutableList<Order> = viewModel.orders.value ?: mutableListOf()

        init {
            viewModel.orders.observe(lifecycleOwner, Observer {
                updateModels(it)
            })
        }

        private fun updateModels(newOrders: MutableList<Order>) {
            this.orders.clear()
            this.orders.addAll(newOrders)
            notifyDataSetChanged()
        }

        override fun getObjForPosition(position: Int): Any {
            return orders[position]
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.adapter_item_orders
        }

        override fun getItemCount(): Int {
            return orders.size
        }

        fun updateSingleOrder(orderId: String) {
            for(i in orders.indices) {
                if(orders[i].id == orderId) {
                    notifyItemChanged(i)
                }
            }
        }

        fun deleteSingleOrder(orderId: String) {
            for(i in orders.indices) {
                if(orders[i].id == orderId) {
                    orders.removeAt(i)
                    notifyItemRemoved(i)
                    return
                }
            }
        }
    }

}
