package com.example.JanMuhammadKnowledgeOasis

import java.io.Serializable

class CartItemsModel(var id: Int? = null, var book_name: String = "", var ref_number: String = "", var buyer: String = "", var imageFront: String = "", var book_type: String = "") : Serializable{
}