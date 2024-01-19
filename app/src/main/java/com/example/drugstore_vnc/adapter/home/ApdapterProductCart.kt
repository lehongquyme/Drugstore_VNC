package com.example.drugstore_vnc.adapter.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.databinding.FragmentListCartBinding
import com.example.drugstore_vnc.model.home.ProductInCartCustomer
import com.example.drugstore_vnc.model.home.totalprice.TotalPrice
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterProductCart(
    private val context: Context,
    private val viewModel: ViewModelProductAPI,
    private val binding: FragmentListCartBinding // Add binding as a parameter

) :
    RecyclerView.Adapter<AdapterProductCart.ViewHolder>() {
    private var items: MutableList<ProductInCartCustomer> = mutableListOf()
    private val selectedItems = mutableListOf<Boolean>()
    private var itemCheckListener: OnItemCheckListener? = null
    @SuppressLint("NotifyDataSetChanged")
    fun setList(item: MutableList<ProductInCartCustomer>) {
        items = item
        notifyDataSetChanged()
    }

    interface OnItemCheckListener {
        fun onItemCheckChanged()
    }

    fun setOnItemCheckListener(listener: OnItemCheckListener) {
        this.itemCheckListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAllItems() {
        selectedItems.fill(true)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deselectAllItems() {
        selectedItems.fill(false)
        notifyDataSetChanged()
    }

    fun areAllItemsSelected(): Boolean {
        return selectedItems.all { it }
    }

    fun totalPriceForSelectedItems(): TotalPrice {
        var totalPrice = 0
        var among = 0
        var order = 0
        selectedItems.forEachIndexed { index, it ->
            if (it) {
                totalPrice += items[index].so_luong * items[index].discount_price
                among += items[index].so_luong
                order++
            }
        }
        return TotalPrice(among, totalPrice, order, 0.0)
    }

    private fun totalForSelectedItems() {
        var among: Int
        var totalPrice: Int
        var order: Int
        var km = 0.0
        selectedItems.forEachIndexed { index, it ->
            if (it) {
                among = items[index].so_luong
                totalPrice = items[index].discount_price
                order = items[index].id
                km = items[index].khuyen_mai.toDouble()
                CheckToPay.checkAddPay(1, TotalPrice(among, totalPrice, order, km))
            } else {
                among = items[index].so_luong
                totalPrice = items[index].discount_price
                order = items[index].id
                CheckToPay.checkAddPay(0, TotalPrice(among, totalPrice, order, km))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        selectedItems.clear()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemrecycleviewlistcart, parent, false)
        items.forEach { _ ->
            selectedItems.add(true)
        }
        totalForSelectedItems()
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.checkCartItem.isChecked = selectedItems[position]
        holder.checkCartItem.setOnCheckedChangeListener { _, isChecked ->
            selectedItems[position] = isChecked
            itemCheckListener?.onItemCheckChanged()
            viewModel.updateCheckBoxState(position, isChecked)
            updateTotalViews(totalPriceForSelectedItems())
            totalPriceForSelectedItems()
            totalForSelectedItems()
        }
        CoroutineScope(Dispatchers.IO).launch {
            val isUrlReachable = AddImageSignUpGeneral.isUrlReachable(item.img_url)
            withContext(Dispatchers.Main) {
                if (isUrlReachable) {
                    Picasso.get().load(item.img_url).into(holder.imageView)
                } else {
                    holder.imageView.setImageResource(R.drawable.flashimage)
                }
            }
        }
        if (item.khuyen_mai > 0) {
            holder.KM.text = "-${item.khuyen_mai}%"
        } else {
            holder.KM.visibility = View.GONE
        }
        val itemsHashTag = item.tags.map { it.name }.toMutableList()
        val adapterHashTag = ApdapterHashTag(itemsHashTag)
        holder.hashTag.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterHashTag
        }
        holder.nameItem.text = item.ten_san_pham
        holder.packing.text = item.quy_cach_dong_goi
        holder.price.text = "${item.don_gia} VND"
        if (item.discount_price < item.don_gia) {
            holder.price.text = "${item.don_gia} VND"
            holder.price.setTypeface(null, Typeface.NORMAL)
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.sellPrice.text = "${item.discount_price} VND"
            holder.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.sellPrice.visibility = View.GONE
        }
        if (item.so_luong_toi_thieu > 0) {
            holder.amongMin.text =
                context.getString(R.string.minimumquantity) + " ${item.so_luong_toi_thieu}"
        } else {
            holder.amongMin.visibility = View.GONE
        }
        if (item.so_luong_toi_da > 0) {
            holder.amongMax.text =
                context.getString(R.string.minimumquantity) + " ${item.so_luong_toi_da}"
        } else {
            holder.amongMax.visibility = View.GONE
        }
        if (item.bonus_coins > 0) {
            holder.bonusCoins.text =
                context.getString(R.string.bonus_Coins) + " ${item.bonus_coins}"
        } else {
            holder.bonusCoins.visibility = View.GONE
        }
        var amongnow: Int = item.so_luong
        holder.edtAmongItem.setText("$amongnow")
        holder.edtAmongItem.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText = holder.edtAmongItem.text.toString().trim()
                if (inputText.isNotEmpty()) {
                    val newQuantity = inputText.toInt()
                    if (newQuantity>0) {
                        amongnow = newQuantity
                        viewModel.updateQuantityInCart(item.id, amongnow)

                    } else{
                        amongnow=1
                        viewModel.updateQuantityInCart(item.id, amongnow)
                        StyleableToast.makeText(
                            context,
                            context.getString(R.string.minCheck)+ " $amongnow",
                            R.style.failImage
                        ).show()
                    }
                    updateTotalViews(totalPriceForSelectedItems())
                    notifyDataSetChanged()
                }else  {
                    StyleableToast.makeText(
                        context,
                        context.getString(R.string.addFailCheck),
                        R.style.failImage
                    ).show()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        holder.addItem.setOnClickListener {
            if (item.so_luong_toi_da > 1) {
                if (amongnow < item.so_luong_toi_da) {
                    amongnow++
                    holder.edtAmongItem.setText("$amongnow")
                    viewModel.updateQuantityInCart(item.id, amongnow)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.maxnimum),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                amongnow++
                holder.edtAmongItem.setText("$amongnow")
                viewModel.updateQuantityInCart(item.id, amongnow)
            }
            updateTotalViews(totalPriceForSelectedItems())
            notifyDataSetChanged()
        }
        holder.minusItem.setOnClickListener {
            if (item.so_luong_toi_thieu > 0) {
                if (amongnow > item.so_luong_toi_thieu) {
                    amongnow--
                    holder.edtAmongItem.setText("$amongnow")
                    viewModel.updateQuantityInCart(item.id, amongnow)
                } else {
                    showDeleteConfirmationDialog(item, position)
                }
            } else {
                if (amongnow > 1) {
                    amongnow--
                    holder.edtAmongItem.setText("$amongnow")
                    viewModel.updateQuantityInCart(item.id, amongnow)
                } else {
                    showDeleteConfirmationDialog(item, position)
                }
            }
            updateTotalViews(totalPriceForSelectedItems())
            notifyDataSetChanged()
        }
        holder.deleteItem.setOnClickListener {
            showDeleteConfirmationDialog(item, position)
        }
    }

    private fun showDeleteConfirmationDialog(item: ProductInCartCustomer, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle(context.getString(R.string.minimum))
            .setMessage(context.getString(R.string.doyouwantdeletethisorder))
            .setNegativeButton(context.getString(R.string.cancle)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                try {
                    if (items.isNotEmpty() && position >= 0 && position < items.size) {
                        val item = items[position]
                        viewModel.updateQuantityInCart(item.id, 0)
                        items =
                            items.filterIndexed { index, _ -> index != position }.toMutableList()
                        if (position < selectedItems.size) {
                            selectedItems.removeAt(position)
                        }
                        itemCheckListener?.onItemCheckChanged()
                        updateTotalViews(totalPriceForSelectedItems())
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val KM: TextView = itemView.findViewById(R.id.txtItemKMCart)
        val imageView: ImageView = itemView.findViewById(R.id.imageItemProductCart)
        val hashTag: RecyclerView = itemView.findViewById(R.id.hashTagCart)
        val nameItem: TextView = itemView.findViewById(R.id.nameItemProductCart)
        val packing: TextView = itemView.findViewById(R.id.packingItemProductCart)
        val amongMin: TextView = itemView.findViewById(R.id.amongMinItemProductCart)
        val amongMax: TextView = itemView.findViewById(R.id.amongMaxItemProductCart)
        val bonusCoins: TextView = itemView.findViewById(R.id.bonusCoinsItemProductCart)
        val price: TextView = itemView.findViewById(R.id.priceItemProductCart)
        val sellPrice: TextView = itemView.findViewById(R.id.sellPriceItemProductCart)
        val checkCartItem: CheckBox = itemView.findViewById(R.id.checkboxItemCart)
        val deleteItem: ImageView = itemView.findViewById(R.id.deleteItemCart)
        val addItem: ImageView = itemView.findViewById(R.id.addCart)
        val minusItem: ImageView = itemView.findViewById(R.id.minusCart)
        val edtAmongItem: EditText = itemView.findViewById(R.id.edtAmongCart)
    }

    private fun updateTotalViews(total: TotalPrice) {
        binding.priceAmongTotalProduct.text = total.price.toString()
        binding.amongTotalProduct.text = total.among.toString()
        binding.priceAmongTotalOders.text = total.oder.toString()
    }
}