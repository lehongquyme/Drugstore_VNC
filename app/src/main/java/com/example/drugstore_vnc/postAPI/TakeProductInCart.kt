package com.example.drugstore_vnc.postAPI

import com.example.drugstore_vnc.fragment.manager.model.logout.Logout
import com.example.drugstore_vnc.model.history.purchase.PurchaseHistory
import com.example.drugstore_vnc.model.history.purchaseItem.PurchaseItem
import com.example.drugstore_vnc.model.home.ProductInCart
import com.example.drugstore_vnc.model.home.ProductToCart
import com.example.drugstore_vnc.model.pay.PayProduct
import com.example.drugstore_vnc.model.pay.ResponsePayAdd
import com.example.drugstore_vnc.model.portfolio.Search
import com.example.drugstore_vnc.model.portfolio.SelectCategory
import com.example.drugstore_vnc.model.portfolio.item.CategoryItemProduct
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TakeProductInCart {
    @FormUrlEncoded
    @POST("v2/cart/update")
    fun fetchAddCartCustomersUser(
        @Field("id") idProduct: Int,
        @Field("number") numberProduct: Int,
    ): Call<ProductToCart>

    @FormUrlEncoded
    @POST("v2/cart/payment")
    fun fetchAddOder(
        @Field("data_id[]") idProduct: List<Int>,
        @Field("device") device: Int,
        @Field("ten") name: String,
        @Field("sdt") phone: String,
        @Field("email") email: String,
        @Field("dia_chi") address: String,
        @Field("ma_so_thue") taxCode: String,
        @Field("ghi_chu") note: String,
        @Field("ck_truoc") transfer: Int,
        @Field("voucher") voucher: String,
        @Field("coin") coin: Int,
        @Field("total_price") total_price: String,

        ): Call<PayProduct>

    @POST("v2/cart/index")
    fun fetchTakeCartCustomersUser(): Call<ProductInCart>
    @POST("v2/member/logout")
    fun fetchPostLogout(): Call<Logout>

    @FormUrlEncoded
    @POST("v2/cart/discount")
    fun fetchTakeVoucherAdd(
        @Field("data_id[]") idCart: List<Int>,
        @Field("coin") coin: Int,
        @Field("voucher") voucher: String,
    ): Call<ResponsePayAdd>

    @FormUrlEncoded
    @POST("v2/system/category_type")
    fun fetchTakeCategory(
        @Field("type") type: String,
        @Field("search") search: String?,
        @Field("page") page: Int?,
    ): Call<SelectCategory>

    @FormUrlEncoded
    @POST("v2/product/index")
    fun fetchTakeItemCategory(
        @FieldMap fields: Map<String, Int>,
        @Field("page") page: Int,
    ): Call<CategoryItemProduct>
    @FormUrlEncoded
    @POST("v2/agency/products")
    fun fetchTakeItemAgency(
        @Field("category") category:String?,
        @Field("search") search:String?,
        @Field("hoat_chat") hoat_chat:Int?,
        @Field("nhom_thuoc") nhom_thuoc:Int?,
        @Field("nha_san_xuat") nha_san_xuat:Int?,

    ): Call<CategoryItemProduct>

    @FormUrlEncoded
    @POST("v2/search")
    fun fetchTakeSearch(
        @Field("search") search: String?,
    ): Call<Search>
    @FormUrlEncoded
    @POST("v2/history/payment")
    fun fetchTakeHistoryPurchase(
        @Field("page") page: Int?,
    ): Call<PurchaseHistory>
    @FormUrlEncoded
    @POST("v2/history/payment_details")
    fun fetchTakeHistoryPurchaseDetail(
        @Field("id") id: Int,
        @Field("page") page: Int?
    ): Call<PurchaseItem>

}