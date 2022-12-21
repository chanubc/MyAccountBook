package com.example.myaccountbook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myaccountbook.databinding.FragmentRecordBinding
import com.google.android.material.tabs.TabLayoutMediator

class RecordFragment_bc : Fragment() {
    // param1 으로 들어오는 걸 fragment 에 붙여줄 거임
    // 1) 바인딩 변수 생성
    // onDestroy에 null로 초기화 해야 하기때문에 binding을 2개나 만들어야함
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    var textarr = arrayListOf<String>("지출", "수입")

    //binding은 여기서
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    )
            : View? {
        // Inflate the layout for this fragment
        // 2) 바인딩 변수로 화면에 view 전달
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }


    //함수 호출 여기서
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

            //레이아웃 생성
            initLayout2()
            //스와이프시 애니메이션
            initAnim()

        }

    }

    //swipe시 에니메이션 생성
    private fun initAnim() {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin) // dimen 파일 안에 크기를 정의해두었다.
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth) // dimen 파일이 없으면 생성해야함
        val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        binding.viewpager.apply {
            setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
            }
            setPageTransformer(ZoomOutPageTransformer())
            offscreenPageLimit = 1 // 몇 개의 페이지를 미리 로드 해둘것인지
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onDestroyView() {
        // fragment 가 깨진 경우 바인딩은 null 로
        _binding = null
        super.onDestroyView()
    }

    private fun initLayout2() {
        binding.viewpager.adapter = MyViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout2, binding.viewpager) { tab, position ->
            tab.text = textarr[position]
            //아이콘도 넣을수 있음
        }.attach()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        //첫 실행 시 중복 생성 회피용

    }

    //viewpager2 에니메이션 추가
    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }

}