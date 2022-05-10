package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class pageSearch : Fragment(), BookInterface {

    companion object {
        fun newInstance() = pageSearch()
    }

    private lateinit var viewModel: PageSearchViewModel
    private lateinit var btnSearch: EditText
    private lateinit var spinnerFilter: Spinner
    private lateinit var spinnerFiltertype: Spinner
    private lateinit var filterItemsLevel: ArrayList<String>
    private lateinit var filterItemsType: ArrayList<String>
    private lateinit var booksList: ArrayList<BooksModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private val URL_PRODUCTS = "https://usmansorion.000webhostapp.com/fetchBooks.php"
    private lateinit var filteredlist: ArrayList<BooksModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page_search_fragment, container, false)

        btnSearch = view.findViewById(R.id.searchField)

        filteredlist = ArrayList()

        btnSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

        })

        spinnerFilter = view.findViewById(R.id.filterLevel)
        spinnerFiltertype = view.findViewById(R.id.filterType)
        recyclerView = view.findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        booksList = ArrayList()
        booksAdapter = BooksAdapter(booksList, requireContext(), this)

        filterItemsLevel = arrayListOf("All", "Primary", "Middle", "High", "FA/FSc", "Graduation")
        filterItemsType = arrayListOf(
                "All", "Science", "Fiction/Story/Novels", "Math", "Language",
                "Religious", "History", "Information Technology", "Magazines & News", "School Books"
        )

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.selectedItem.toString() == "All"){
                    loadBooks()
                }
                else if (parent!!.selectedItem.toString() == "Primary"){
                    loadPrimaryBooks()
                }
                else if (parent!!.selectedItem.toString() == "Middle"){
                    loadMiddleBooks()
                }
                else if (parent!!.selectedItem.toString() == "High"){
                    loadHighBooks()
                }
                else if (parent!!.selectedItem.toString() == "FA/FSc"){
                    loadFABooks()
                }
                else if (parent!!.selectedItem.toString() == "Graduation"){
                    loadGradBooks()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        spinnerFiltertype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.selectedItem.toString() == "All"){
                    loadBooks()
                }
                else if (parent!!.selectedItem.toString() == "Science"){
                    loadScienceBooks()
                }
                else if (parent!!.selectedItem.toString() == "Fiction/Story/Novels"){
                    loadFicBooks()
                }
                else if (parent!!.selectedItem.toString() == "Math"){
                    loadMathBooks()
                }
                else if (parent!!.selectedItem.toString() == "Language"){
                    loadLangBooks()
                }
                else if (parent!!.selectedItem.toString() == "Magazines & News"){
                    loadMagBooks()
                }
                else if (parent!!.selectedItem.toString() == "History"){
                    loadHistBooks()
                }
                else if (parent!!.selectedItem.toString() == "Information Technology"){
                    loadITBooks()
                }
                else if (parent!!.selectedItem.toString() == "Religious"){
                    loadRgBooks()
                }
                else if (parent!!.selectedItem.toString() == "School Books"){
                    loadScBooks()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }




        var arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filterItemsLevel
        )
        spinnerFilter.adapter = arrayAdapter
        spinnerFilter.prompt = "Select Level"

        var arrayAdapterType: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filterItemsType
        )
        spinnerFiltertype.adapter = arrayAdapterType
        spinnerFiltertype.prompt = "Select Type"

        loadBooks()

        return view
    }

    private fun loadITBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Information Technology") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadHistBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "History") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadRgBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Religious") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadScBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "School Books") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadMagBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Magazines & News") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadLangBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Language") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadMathBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Math") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadFicBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Fiction/Story/Novels") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadScienceBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_type") == "Science") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadGradBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_level") == "Graduation") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadFABooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_level") == "FA/FSc") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadHighBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_level") == "High") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadMiddleBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_level") == "Middle") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadPrimaryBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)

                            if (books.getString("book_level") == "Primary") {

                                var imageFront = books.getString("imageFront")
                                var url =
                                    "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


                                //adding the product to product list
                                booksList.add(
                                    BooksModel(
                                        books.getInt("id"),
                                        books.getString("book_name"),
                                        books.getString("ref_number"),
                                        books.getString("book_type"),
                                        books.getString("book_level"),
                                        books.getString("author"),
                                        books.getString("publisher"),
                                        books.getString("imageBack"),
                                        url,
                                        books.getString("rating"),
                                        books.getString("loan_status")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

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

    private fun loadBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                URL_PRODUCTS,
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            booksList.clear()
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val books = array.getJSONObject(i)
                                var imageFront = books.getString("imageFront")
                                var url = "https://usmansorion.000webhostapp.com/front_cover/"+imageFront


                                //adding the product to product list
                                booksList.add(
                                        BooksModel(
                                                books.getInt("id"),
                                                books.getString("book_name"),
                                                books.getString("ref_number"),
                                                books.getString("book_type"),
                                                books.getString("book_level"),
                                                books.getString("author"),
                                                books.getString("publisher"),
                                                books.getString("imageBack"),
                                                url,
                                                books.getString("rating"),
                                                books.getString("loan_status")
                                        )
                                )
                            }

                            //creating adapter object and setting it to recyclerview
                            recyclerView.adapter = booksAdapter
                            booksAdapter.notifyDataSetChanged()

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

    private fun filter(toString: String) {
        filteredlist.clear()
        var booksAdapter = BooksAdapter(filteredlist, requireContext(), this)
        for (i in 0..booksList.size - 1){
            if (booksList[i].book_name.toLowerCase().contains(toString.toLowerCase())){
                filteredlist.add(booksList[i])
            }
        }
        recyclerView.adapter = booksAdapter
        booksAdapter.notifyDataSetChanged()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PageSearchViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun OnBookClicked(currentItem: BooksModel, position: Int) {
        var intent = Intent(activity, BookDetails::class.java)
        intent.putExtra("name",currentItem.book_name.toString())
        intent.putExtra("type", currentItem.book_type.toString())
        intent.putExtra("level", currentItem.book_level.toString())
        intent.putExtra("backImage", "https://usmansorion.000webhostapp.com/back_cover/"+currentItem.imageBack.toString())
        intent.putExtra("frontImage", currentItem.imageFront.toString())
        intent.putExtra("rating", currentItem.rating.toString())
        intent.putExtra("quantity", currentItem.loan_status.toString())
        intent.putExtra("author", currentItem.author)

        startActivity(intent)
    }

}