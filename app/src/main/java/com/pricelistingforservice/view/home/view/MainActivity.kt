package com.pricelistingforservice.view.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.pricelistingforservice.R
import com.pricelistingforservice.databinding.ActivityMainBinding
import com.pricelistingforservice.databinding.BottomSheetAddBinding
import com.pricelistingforservice.databinding.BottomSheetServiceCustomizeBinding
import com.pricelistingforservice.util.DividerItemDecoration
import com.pricelistingforservice.util.Utils
import com.pricelistingforservice.util.getAssetJsonData
import com.pricelistingforservice.util.setupFullHeight
import com.pricelistingforservice.util.toast
import com.pricelistingforservice.view.home.adapter.ServiceAdapter
import com.pricelistingforservice.view.home.model.AddCartItem
import com.pricelistingforservice.view.home.model.ListItem
import com.pricelistingforservice.view.home.model.ServiceResponse
import com.pricelistingforservice.view.home.model.SpecificationsItem
import com.pricelistingforservice.view.home.viewmodel.CommonViewModel


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var bsdServiceList: BottomSheetDialog? = null
    private var bsdAdd: BottomSheetDialog? = null
    lateinit var bindingBottomSheetServiceCustomizeBinding: BottomSheetServiceCustomizeBinding
    lateinit var bindingBottomSheetAddBinding: BottomSheetAddBinding
    lateinit var commonViewModel: CommonViewModel
    lateinit var serviceResponse: ServiceResponse
    var specifications: List<SpecificationsItem> = arrayListOf()
    lateinit var typeTwoAdapter: ServiceAdapter
    lateinit var typeOneAdapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        statusBarColor(R.color.color_0FB7B4)
        initViewModel()
        getData()
        initBottomSheet()
        initLastAddSheet()
        clicks()
    }

    private fun initViewModel() {
        commonViewModel = ViewModelProvider(this)[CommonViewModel::class.java]
    }

    private fun clicks() {
        binding.tvCustomize.setOnClickListener {
            customizeTap(false)
        }

        binding.imgPlus.setOnClickListener {
            bsdAdd?.show()
            setLastItemRepeatText()

        }
        binding.imgMinus.setOnClickListener {
            if (commonViewModel.addToCartItem.size > 1) {
                commonViewModel.addToCartItem.removeLast()
                binding.tvNotificaionCount.text =
                    commonViewModel.addToCartItem.filter { !it.isRepeat }.toList().size.toString()
                binding.tvCount.text = commonViewModel.addToCartItem.size.toString()
            }
        }
    }

    private fun customizeTap(isFromDialg: Boolean) {
        getData()
        setFreshDataToView()
        bsdServiceList?.show()
        if (isFromDialg) {
            bsdAdd?.dismiss()
        }
        bindingBottomSheetServiceCustomizeBinding.nsMain.fullScroll(View.FOCUS_UP)
        bindingBottomSheetServiceCustomizeBinding.appBar.setExpanded(true)
    }

    private fun setLastItemRepeatText() {
        val lastItem = commonViewModel.addToCartItem.last()
        var itemInfo = ""
        for (i in lastItem.serviceResponse.specifications) {
            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        itemInfo = "${j.name}"
                        break
                    }
                }
            }
        }

        for (i in lastItem.serviceResponse.specifications) {
            if (i.type == 2) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        itemInfo = "$itemInfo, ${j.name}"
                    }
                }
            }
        }

        bindingBottomSheetAddBinding.tvLastItems.text = itemInfo.replace("[", "").replace("]", "")
    }

    private fun getData() {
        serviceResponse =
            Gson().fromJson(this.getAssetJsonData(), ServiceResponse::class.java)
        specifications = serviceResponse.specifications
        commonViewModel.serviceResponse = serviceResponse
        commonViewModel.specifications = specifications
        setData()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        binding.tvInsideTitle.text = commonViewModel.serviceResponse.name!![0].toString()
        binding.tvPrice.text =
            "${Utils.getCurrencySymbol("INR")} ${commonViewModel.serviceResponse.price.toString()}"
    }

    @SuppressLint("SetTextI18n")
    fun initBottomSheet() {
        val myDrawerView =
            layoutInflater.inflate(R.layout.bottom_sheet_service_customize, null)
        bsdServiceList = BottomSheetDialog(this, R.style.SheetDialog)
        bindingBottomSheetServiceCustomizeBinding =
            BottomSheetServiceCustomizeBinding.inflate(
                layoutInflater,
                myDrawerView as ViewGroup,
                false
            )
        bsdServiceList?.setContentView(bindingBottomSheetServiceCustomizeBinding.root)
        bsdServiceList?.setCancelable(false)
        bsdServiceList?.behavior?.isDraggable = false

        bindingBottomSheetServiceCustomizeBinding.toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_back)
        bindingBottomSheetServiceCustomizeBinding.toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        bindingBottomSheetServiceCustomizeBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            bsdServiceList?.dismiss()
        })

        bindingBottomSheetServiceCustomizeBinding.tvButtonAddToCard.setOnClickListener {
            addToCartTap()
        }

        bindingBottomSheetServiceCustomizeBinding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        bindingBottomSheetServiceCustomizeBinding.toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)

        bindingBottomSheetServiceCustomizeBinding.toolbarLayout.title =
            commonViewModel.serviceResponse.name!![0].toString()

        bindingBottomSheetServiceCustomizeBinding.imgPlus.setOnClickListener {
            commonViewModel.mainCount = commonViewModel.mainCount + 1
            bindingBottomSheetServiceCustomizeBinding.tvCount.text =
                commonViewModel.mainCount.toString()
            calculateCardAMount()
        }
        bindingBottomSheetServiceCustomizeBinding.imgMinus.setOnClickListener {
            if (commonViewModel.mainCount != 1) {
                commonViewModel.mainCount = commonViewModel.mainCount - 1
                bindingBottomSheetServiceCustomizeBinding.tvCount.text =
                    commonViewModel.mainCount.toString()
                calculateCardAMount()
            }

        }

        bindingBottomSheetServiceCustomizeBinding.clBottom.visibility = View.VISIBLE
        setupFullHeight(bsdServiceList!!)

    }

    private fun addToCartTap() {
        if (checkIsRequiredSelected()) {
            commonViewModel.addToCartItem.add(
                AddCartItem(
                    ServiceResponse(
                        serviceResponse.itemTaxes,
                        serviceResponse.price,
                        serviceResponse.name,
                        serviceResponse.id,
                        typeTwoAdapter.specificationsItem + typeOneAdapter.specificationsItem
                    ), onlyPrice() + serviceResponse.price?.toDouble()!!
                )

            )
            bsdServiceList?.dismiss()
            updateViewAfterCartAdd()
        } else {
            this.toast(getString(R.string.please_select_required_field))
        }
    }

    private fun setFreshDataToView() {
        commonViewModel.mainCount = 1
        bindingBottomSheetServiceCustomizeBinding.tvCount.text =
            commonViewModel.mainCount.toString()
        filterValues()
        typeTwoAdapter()
        typeOneAdapter()
    }

    @SuppressLint("SetTextI18n")
    fun initLastAddSheet() {
        val myDrawerView =
            layoutInflater.inflate(R.layout.bottom_sheet_add, null)
        bsdAdd = BottomSheetDialog(this, R.style.DialogStyle)
        bindingBottomSheetAddBinding =
            BottomSheetAddBinding.inflate(
                layoutInflater,
                myDrawerView as ViewGroup,
                false
            )
        bsdAdd?.setContentView(bindingBottomSheetAddBinding.root)
        bsdAdd?.setCancelable(false)
        bsdAdd?.behavior?.isDraggable = false

        bindingBottomSheetAddBinding.tvRepeat.setOnClickListener {
            commonViewModel.addToCartItem.add(
                AddCartItem(
                    commonViewModel.addToCartItem.last().serviceResponse,
                    commonViewModel.addToCartItem.last().price,
                    true
                )
            )
            bsdAdd?.dismiss()
            updateViewAfterCartAdd()
        }

        bindingBottomSheetAddBinding.tvCustomize.setOnClickListener {
            customizeTap(true)
        }

        bindingBottomSheetAddBinding.imgClose.setOnClickListener {
            bsdAdd?.dismiss()
        }

    }

    private fun updateViewAfterCartAdd() {
        binding.clViewCart.visibility = View.VISIBLE
        binding.llNotificationCount.visibility = View.VISIBLE
        binding.llPlusMinus.visibility = View.VISIBLE
        binding.tvCustomize.visibility = View.GONE
        binding.tvNotificaionCount.text =
            commonViewModel.addToCartItem.filter { !it.isRepeat }.toList().size.toString()
        binding.tvCount.text = commonViewModel.addToCartItem.size.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun filterValues() {
        for (i in commonViewModel.serviceResponse.specifications as ArrayList<SpecificationsItem>) {
            if (i.range == 1 && i.maxRange == 3) {
                i.isRequired = true
                i.isMultipleAllowed = true
            } else {
                i.isMultipleAllowed = false
            }

            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        commonViewModel.serviceResponse.specifications =
                            specifications.filter { it.modifierId.toString() == j.id.toString() && it.type != 1 }
                        calculateCardAMount()
                        commonViewModel.currentPrice = j.price?.toDouble()!!
                        break
                    }
                }
            }
        }
    }

    private fun typeOneAdapter() {
        typeOneAdapter =
            ServiceAdapter(
                commonViewModel.specifications.sortedBy { it.sequenceNumber }
                    .filter { it.type == 1 } as ArrayList<SpecificationsItem>,
                this
            ) { listItem ->
                commonViewModel.serviceResponse.specifications =
                    specifications.filter { it.modifierId.toString() == listItem.id.toString() && it.type != 1 }

                calculateCardAMount()
                typeTwoAdapter.setData(commonViewModel.serviceResponse.specifications.sortedBy { it.sequenceNumber }
                    .filter { it.type == 2 } as ArrayList<SpecificationsItem>)
            }

        bindingBottomSheetServiceCustomizeBinding.rvTypeOne.apply {
            adapter = typeOneAdapter
        }

    }

    private fun typeTwoAdapter() {
        typeTwoAdapter =
            ServiceAdapter(
                commonViewModel.serviceResponse.specifications.sortedBy { it.sequenceNumber }
                    .filter { it.type == 2 } as ArrayList<SpecificationsItem>,
                this
            ) { listItem ->
                calculateCardAMount()
            }

        bindingBottomSheetServiceCustomizeBinding.rvTypeTwo.apply {
            adapter = typeTwoAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    fun calculateCardAMount() {
        commonViewModel.currentPrice = onlyPrice()
        bindingBottomSheetServiceCustomizeBinding.tvButtonAddToCard.text =
            "Add To Cart - ${Utils.getCurrencySymbol("INR")}${((commonViewModel.currentPrice) * commonViewModel.mainCount)}"
    }

    private fun onlyPrice(): Double {
        var price = 0.00
        for (i in commonViewModel.specifications) {
            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        price += j.price?.toDouble()!!
                        break
                    }
                }
            }
        }

        for (i in commonViewModel.serviceResponse.specifications) {
            if (i.type == 2) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        price += ((j.price?.toDouble()!!) * j.count)
                    }
                }
            }
        }
        return price
    }

    private fun checkIsRequiredSelected(): Boolean {
        for (i in typeTwoAdapter.specificationsItem) {
            if (i.isRequired) {
                var isSelected = false
                for (j in i.list) {
                    if (j.isDefaultSelected) {
                        isSelected = j.isDefaultSelected
                    }
                }
                if (!isSelected) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("NewApi")
    fun statusBarColor(id: Int) {
        window.decorView.systemUiVisibility = 0
        window.statusBarColor = ContextCompat.getColor(this, id)
    }
}