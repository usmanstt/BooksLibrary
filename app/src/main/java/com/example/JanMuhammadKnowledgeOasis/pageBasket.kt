package com.example.JanMuhammadKnowledgeOasis

import android.content.*
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class pageBasket : Fragment(), CBookInterface {

    companion object {
        fun newInstance() = pageBasket()
    }

    private lateinit var viewModel: PageBasketViewModel
    private lateinit var cartList: ArrayList<CartItemsModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    var shp: SharedPreferences? = null
    lateinit var checkedBooks: ArrayList<CartItemsModel>
    var check = true
    private lateinit var checkedItems: ArrayList<CartItemsModel>
    private lateinit var btnCheckOut: Button
    private lateinit var orderList: ArrayList<OrderedBooks>
    private lateinit var buyers: ArrayList<String>
    private lateinit var refs: ArrayList<String>
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page_basket_fragment, container, false)

        recyclerView = view.findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        btnCheckOut = view.findViewById(R.id.checkOutBtn)

        if (shp == null) shp = activity?.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        userName = shp!!.getString("name", "") as String

        cartList = ArrayList()
        refs = ArrayList()
        buyers = ArrayList()
        orderList = ArrayList()
        checkedBooks = ArrayList()
        checkedItems = ArrayList()
        cartAdapter = CartAdapter(cartList, requireContext(), this)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                checkedItemsReciever,
                IntentFilter("items")
        )



//
//        Toast.makeText(activity, orderList.size.toString(), Toast.LENGTH_LONG).show()

        fetchCartItems()
        fetchOrderedItems()

        btnCheckOut.setOnClickListener {
            if (checkedItems.isEmpty()){
                Toast.makeText(activity, "No items selected", Toast.LENGTH_LONG).show()
            }
            else {
//                val intent: Intent = Intent("itemsChecked")
//                intent.putExtra("itemsCheckedList", checkedItems)
//                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

                var intent2 = Intent(activity, CheckOut::class.java)
                intent2.putExtra("itemsCheckedList", checkedItems)
                startActivity(intent2)
            }
        }

        return view
    }

    private fun deleteOrderedItems(i : Int) {

        var Request: StringRequest = object : StringRequest(Request.Method.POST,
                "https://usmansorion.000webhostapp.com/delCItems.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        Toast.makeText(activity, "Book Already Ordered!", Toast.LENGTH_LONG).show()
                        cartList.removeAt(i)
                        recyclerView.adapter = cartAdapter
                        cartAdapter.notifyDataSetChanged()
                    }
                },
                Response.ErrorListener {

                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["ref_number"] = cartList[i].ref_number.toString()
                params["buyer"] = cartList[i].buyer.toString()
                return params
            }
        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(Request)
    }

    private fun fetchOrderedItems() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            orderList.clear()
                            buyers.clear()
                            refs.clear()

                            if (shp == null) shp = activity?.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                //adding the product to product list
                                orderList.add(
                                        OrderedBooks(
                                                orders.getInt("id"),
                                                orders.getString("book_name"),
                                                orders.getString("ref_number"),
                                                orders.getString("order_number"),
                                                orders.getString("buyer"),
                                                orders.getString("delivery_address"),
                                                orders.getString("pick_address"),
                                                orders.getString("delievery_boy"),
                                                orders.getString("contact_num"),
                                                orders.getString("expected_delDate"),
                                                orders.getString("buyer_fullname")
                                        )
                                )
                                buyers.add(orders.getString("buyer"))
                                refs.add(orders.getString("ref_number"))
                            }
                            if (cartList.isNotEmpty()){
                                for (i in 0..cartList.size - 1){
                                    if (buyers.contains(cartList[i].buyer) && refs.contains(cartList[i].ref_number)){
                                        deleteOrderedItems(i)
                                    }
                                }
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                },
                Response.ErrorListener {

                }){
        }
        var req: RequestQueue = Volley.newRequestQueue(requireContext())
        req.add(stringRequest)
    }

    private fun fetchCartItems() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchCartItems.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            cartList.clear()


                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val cart = array.getJSONObject(i)

                                //adding the product to product list
                                if (cart.getString("buyer") == userName){
                                    cartList.add(
                                            CartItemsModel(
                                                    cart.getInt("id"),
                                                    cart.getString("book_name"),
                                                    cart.getString("ref_number"),
                                                    cart.getString("buyer"),
                                                    cart.getString("imageFront"),
                                                    cart.getString("book_type")
                                            )
                                    )
                                }
                            }

                            //creating adapter object and setting it to recyclerview
                            recyclerView.adapter = cartAdapter
                            cartAdapter.notifyDataSetChanged()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                },
                Response.ErrorListener {

                }){

        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(stringRequest)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PageBasketViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun OnBookClicked(currentItem: CartItemsModel, position: Int) {
//        if (check == true){
//            check = false
//            checkedBooks.add(currentItem)
//            Toast.makeText(activity, checkedBooks.size.toString(), Toast.LENGTH_LONG).show()
//        }
//        else{
//            check = true
//            checkedBooks.remove(currentItem)
//            Toast.makeText(activity, checkedBooks.size.toString(), Toast.LENGTH_LONG).show()
//        }
    }

    val checkedItemsReciever: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            checkedItems = intent.getSerializableExtra("itemList") as ArrayList<CartItemsModel>


        }
    }

}