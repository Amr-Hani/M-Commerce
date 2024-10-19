package com.example.mcommerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcommerce.databinding.FragmentCartBinding
import com.example.mcommerce.model.pojos.prouduct.Product
import com.example.mcommerce.ui.cart.view.CartAdapter

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private val initialProducts = listOf(
        Product("https://www.picng.com/upload/spider_man/png_spider_man_76928.png", "ADIDAS | CLASSIC BACKPACK", 1096.9, 1, 3),
        Product("https://w7.pngwing.com/pngs/915/345/png-transparent-multicolored-balloons-illustration-balloon-balloon-free-balloons-easter-egg-desktop-wallpaper-party-thumbnail.png", "PALLADIUM | PALLATECH HI TX", 2506.41, 1, 5)
    )

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        cartAdapter = CartAdapter(
            onItemChanged = { updateSubtotal() },
            onItemDeleted = { product -> deleteProduct(product) }
        )

        binding.recyclerViewCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        cartAdapter.submitList(initialProducts)
        updateSubtotal()

        binding.btnCheckout.setOnClickListener {

        }

        return binding.root
    }

    private fun updateSubtotal() {
        val subtotal = cartAdapter.currentList.sumOf { it.price * it.quantity }
        binding.cartSubtotal.text = "Subtotal: EGP $subtotal"
    }

    private fun deleteProduct(product: Product) {
        val updatedList = cartAdapter.currentList.toMutableList().apply {
            remove(product)
        }
        cartAdapter.submitList(updatedList)
        updateSubtotal()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}