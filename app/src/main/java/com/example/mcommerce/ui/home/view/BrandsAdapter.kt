import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.ItemBrandsBinding
import SmartCollectionsItem

class BrandAdapter(
    private val onItemClick: (SmartCollectionsItem) -> Unit
) : ListAdapter<SmartCollectionsItem, BrandAdapter.BrandViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBrandsBinding.inflate(inflater, parent, false)
        return BrandViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class BrandViewHolder(
        private var binding: ItemBrandsBinding,
        private val onItemClick: (SmartCollectionsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(smartCollectionsItem: SmartCollectionsItem) {
            binding.apply {
                // Set brand title
                textBrands.text = smartCollectionsItem.title

                // Load brand image using Glide
                Glide.with(imgBrands.context)
                    .load(smartCollectionsItem.image?.src) // Assuming `image.src` contains the image URL
                    .into(imgBrands)

                // Handle item click
                root.setOnClickListener {
                    onItemClick(smartCollectionsItem)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<SmartCollectionsItem>() {
        override fun areItemsTheSame(
            oldItem: SmartCollectionsItem,
            newItem: SmartCollectionsItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SmartCollectionsItem,
            newItem: SmartCollectionsItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
