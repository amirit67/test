package ir.payebash.models.event

import android.content.Context

import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.library.baseAdapters.BR

import java.io.Serializable
import java.util.ArrayList
import com.google.gson.annotations.SerializedName

import ir.payebash.R
import ir.payebash.models.general.FollwoersItem

class EventModel : BaseObservable(), Serializable {

    @SerializedName("isWoman")
    @get:Bindable
    val isWoman: Boolean = false

    @SerializedName("images")
    val images: String? = null

    @SerializedName("cost")
    @get:Bindable
    val cost: String? = null

    @SerializedName("city")
    var city: String? = null
        set(city) {
            field = city
            notifyPropertyChanged(BR.eventItem)
        }

    @SerializedName("users")
    private val follwes: List<FollwoersItem>? = null

    @SerializedName("isImmediate")
    @get:Bindable
    val isImmediate: Boolean = false

    @SerializedName("service")
    val service: Int = 0

    /*public EventModel(Context context) {
		this.context = context;
	}*/

    @SerializedName("Id")
    var postId: String? = null

    @SerializedName("title")
    @get:Bindable
    val title: String? = null

    @SerializedName("createDate")
    val createDate: String? = null

    @SerializedName("numberFollowers")
    @get:Bindable
    val numberFollowers: String? = null

    internal var context: Context? = null

    val followers: List<FollwoersItem>
        get() = follwes ?: ArrayList()
}
